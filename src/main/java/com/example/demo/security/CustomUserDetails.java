package com.example.demo.security;

import com.example.demo.model.Customer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Wrapper for Customer used as the authenticated principal.
 */
public record CustomUserDetails(Customer customer) implements UserDetails {

    public String getDisplayName() {
        return customer.getUsername();
    }

    /**
     * Convenience: expose the underlying Customer for tests or service code if needed.
     * This is safe because principal is only available for code running under authentication.
     */
    @Override
    public Customer customer() {
        return customer;
    }

    @Override
    public String getUsername() {
        return customer.getEmail();
    }

    @Override
    public String getPassword() {
        return customer.getPassword();
    }

    /**
     * Return the user's ROLE_ authority.
     * Example: ROLE_USER or ROLE_ADMIN
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + customer.getRole()));
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
        return true;
    }
}
