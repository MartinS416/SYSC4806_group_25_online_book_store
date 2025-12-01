package com.bookstore.security;

import com.bookstore.common.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link CustomUserDetails}.
 * <p>
 * These tests verify that the security principal correctly exposes email,
 * password, display name, and role-based authorities derived from the
 * underlying {@link Customer}, as expected by the security TDS.
 */
class CustomUserDetailsTest {

    /**
     * Verifies that {@link CustomUserDetails} maps the {@link Customer}'s
     * email, password, username, and role to the corresponding
     * {@link org.springframework.security.core.userdetails.UserDetails}
     * fields and authorities.
     * <p>
     * Confirms:
     * <ul>
     *     <li>Email → {@code getUsername()}.</li>
     *     <li>Password → {@code getPassword()}.</li>
     *     <li>Username → {@code getDisplayName()}.</li>
     *     <li>Role → {@code ROLE_<role>} authority.</li>
     * </ul>
     */
    @Test
    void exposesEmailPasswordRoleAndDisplayName() {
        Customer customer = new Customer();
        customer.setEmail("user@example.com");
        customer.setPassword("secret");
        customer.setUsername("Display Name");
        customer.setRole("ADMIN");

        CustomUserDetails details = new CustomUserDetails(customer);

        assertThat(details.getUsername()).isEqualTo("user@example.com");
        assertThat(details.getPassword()).isEqualTo("secret");
        assertThat(details.getDisplayName()).isEqualTo("Display Name");
        assertThat(details.getCustomer()).isSameAs(customer);

        Collection<? extends GrantedAuthority> authorities =
                details.getAuthorities();
        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");

        assertThat(details.isAccountNonExpired()).isTrue();
        assertThat(details.isAccountNonLocked()).isTrue();
        assertThat(details.isCredentialsNonExpired()).isTrue();
        assertThat(details.isEnabled()).isTrue();
    }
}