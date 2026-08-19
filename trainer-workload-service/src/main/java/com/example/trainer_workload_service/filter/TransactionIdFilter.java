package com.example.trainer_workload_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Order(1)
@Component
public class TransactionIdFilter extends OncePerRequestFilter {

    private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";
    private static final String TRANSACTION_ID_MDC_KEY = "transactionId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String transactionId = request.getHeader(TRANSACTION_ID_HEADER);

        if (transactionId == null || transactionId.isBlank()) {
            transactionId = UUID.randomUUID().toString();
        }

        MDC.put(TRANSACTION_ID_MDC_KEY, transactionId);

        response.setHeader(
                TRANSACTION_ID_HEADER,
                transactionId
        );

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(TRANSACTION_ID_MDC_KEY);
        }
    }
}
