package com.example.core.security.jwt;

import com.example.core.security.service.CustomUserDetailsService;
import com.example.core.security.token.TokenBlacklistService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        var authorizationHeader =
                request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {

            filterChain.doFilter(request, response);
            return;
        }

        var token = authorizationHeader.substring(BEARER_PREFIX.length());

        try {
            if (tokenBlacklistService.isRevoked(token)) {
                log.warn("JWT authentication failed: token is revoked");

                filterChain.doFilter(request, response);
                return;
            }

            var username = jwtService.extractUsername(token);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                var userDetails = customUserDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, userDetails)) {
                    var authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authentication.setDetails(new WebAuthenticationDetailsSource()
                            .buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (JwtException | IllegalArgumentException exception) {
            log.warn("JWT authentication failed: {}",
                    exception.getMessage()
            );
        }

        filterChain.doFilter(request, response);
    }
}
