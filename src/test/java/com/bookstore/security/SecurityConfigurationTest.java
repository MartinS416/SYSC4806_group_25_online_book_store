package com.bookstore.security;

import com.bookstore.common.model.Customer;
import com.bookstore.common.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Configuration-level tests for {@link SecurityConfiguration}.
 * <p>
 * These tests verify that the security beans and authentication provider
 * are properly wired in the application context and function correctly.
 */
@SpringBootTest
class SecurityConfigurationTest {

    @Autowired
    DaoAuthenticationProvider authProvider;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CustomDetailsService customDetailsService;

    @MockitoBean
    CustomerRepository customerRepository;

    /**
     * Verifies that the configured {@link DaoAuthenticationProvider} uses
     * the {@link CustomDetailsService} and {@link PasswordEncoder} beans
     * by attempting an authentication and verifying it succeeds.
     * <p>
     * This underpins the login flow by ensuring that user lookups and password
     * checks are delegated correctly through the authentication provider.
     */
    @Test
    void authProvider_usesCustomDetailsServiceAndPasswordEncoder() {
        // Arrange
        String email = "test@example.com";
        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setPassword(encodedPassword);
        customer.setRole("USER");

        when(customerRepository.findByEmail(email))
                .thenReturn(Optional.of(customer));

        // Act - Attempt authentication
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(email, rawPassword);
        Authentication result = authProvider.authenticate(authToken);

        // Assert - Authentication succeeded, and the principle is CustomUserDetails
        assertThat(result.isAuthenticated()).isTrue();
        assertThat(result.getPrincipal()).isInstanceOf(CustomUserDetails.class);
        CustomUserDetails details = (CustomUserDetails) result.getPrincipal();
        assertThat(details.getUsername()).isEqualTo(email);
    }

    /**
     * Verifies that authentication fails with an incorrect password.
     */
    @Test
    void authProvider_rejectsIncorrectPassword() {
        // Arrange
        String email = "test@example.com";
        String encodedPassword = passwordEncoder.encode("correctPassword");

        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setPassword(encodedPassword);
        customer.setRole("USER");

        when(customerRepository.findByEmail(email))
                .thenReturn(Optional.of(customer));

        // Act & Assert - Authentication should fail
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(email, "wrongPassword");

        assertThatThrownBy(() -> authProvider.authenticate(authToken))
                .isInstanceOf(BadCredentialsException.class);
    }

    /**
     * Verifies that when a customer exists, {@link CustomDetailsService}
     * returns a valid {@link CustomUserDetails}.
     */
    @Test
    void customDetailsService_loadsUserByEmail() {
        // Arrange
        String email = "test@example.com";
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setPassword("encoded");
        customer.setRole("USER");

        when(customerRepository.findByEmail(email))
                .thenReturn(Optional.of(customer));

        // Act
        var details = customDetailsService.loadUserByUsername(email);

        // Assert
        assertThat(details).isInstanceOf(CustomUserDetails.class);
        assertThat(details.getUsername()).isEqualTo(email);
        assertThat(details.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    /**
     * Verifies that CustomDetailsService throws UsernameNotFoundException
     * when the user doesn't exist.
     */
    @Test
    void customDetailsService_throwsExceptionWhenUserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        when(customerRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> customDetailsService.loadUserByUsername(email))
                .isInstanceOf(org.springframework.security.core.userdetails.UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}