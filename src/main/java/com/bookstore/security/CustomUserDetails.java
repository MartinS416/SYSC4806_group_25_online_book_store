package com.bookstore.security;

import com.bookstore.common.model.Customer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Wrapper for Customer used as the authenticated principal.
 * Implements UserDetails to integrate with Spring Security authentication.
 */
public class CustomUserDetails implements UserDetails {

    private final Customer customer;

    public CustomUserDetails(Customer customer) {
        this.customer = customer;
    }

    /**
     * Exposes the underlying Customer for tests or service code if needed.
     * This is safe because the principal is only available for code running under authentication.
     *
     * @return the underlying Customer entity
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Returns the display name of the customer.
     *
     * @return customer's display name
     */
    public String getDisplayName() {
        return customer.getUsername();
    }

    /**
     * Returns the email as the username for authentication purposes.
     *
     * @return customer's email
     */
    @Override
    public String getUsername() {
        return customer.getEmail();
    }

    /**
     * Returns the encoded password for authentication.
     *
     * @return customer's password
     */
    @Override
    public String getPassword() {
        return customer.getPassword();
    }

    /**
     * Returns the user's ROLE_ authority.
     * Example: ROLE_USER or ROLE_ADMIN
     *
     * @return collection of granted authorities
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