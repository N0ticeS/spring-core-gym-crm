package com.example.core.security.service;

import com.example.core.model.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Getter
public class CustomUserDetails implements UserDetails {
    private final Long id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean active;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.active = user.isActive();

        this.authorities = Collections.singleton(
                new SimpleGrantedAuthority(
                        "ROLE_" + user.getRole().name()
                )
        );
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        CustomUserDetails that = (CustomUserDetails) object;

        return Objects.equals(id, that.id)
                && Objects.equals(authorities, that.authorities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, authorities);
    }
}
