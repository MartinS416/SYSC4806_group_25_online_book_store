package com.bookstore.security;

import com.bookstore.common.model.Customer;
import com.bookstore.common.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CustomDetailsService}.
 * <p>
 * These tests validate the behaviour of the security service responsible
 * for loading users by email and mapping them to {@link CustomUserDetails},
 * as referenced in the TDS security/service test cases.
 */
class CustomDetailsServiceTest {

    CustomerRepository repo = mock(CustomerRepository.class);
    CustomDetailsService service = new CustomDetailsService(repo);

    /**
     * Ensures that when a {@link Customer} exists for the given email,
     * {@link CustomDetailsService#loadUserByUsername(String)} returns a
     * {@link CustomUserDetails} with matching username and password.
     * <p>
     * This supports the positive path of the login-related test cases.
     */
    @Test
    void loadUserByUsername_returnsCustomUserDetails_whenCustomerExists() {
        Customer customer = new Customer();
        customer.setEmail("test@example.com");
        customer.setPassword("encoded");
        customer.setRole("USER");

        when(repo.findByEmail("test@example.com"))
                .thenReturn(Optional.of(customer));

        UserDetails details = service.loadUserByUsername("test@example.com");

        assertThat(details).isInstanceOf(CustomUserDetails.class);
        assertThat(details.getUsername()).isEqualTo("test@example.com");
        assertThat(details.getPassword()).isEqualTo("encoded");

        verify(repo).findByEmail("test@example.com");
        verifyNoMoreInteractions(repo);
    }

    /**
     * Ensures that when no {@link Customer} exists for the given email,
     * {@link CustomDetailsService#loadUserByUsername(String)} throws
     * {@link UsernameNotFoundException} with no unexpected repository calls.
     * <p>
     * This supports the negative-path expectations for invalid login attempts
     * in the security test design.
     */
    @Test
    void loadUserByUsername_throwsWhenCustomerMissing() {
        when(repo.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing@example.com"));

        verify(repo).findByEmail("missing@example.com");
        verifyNoMoreInteractions(repo);
    }
}