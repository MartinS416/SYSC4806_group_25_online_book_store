package com.bookstore.security;

import com.bookstore.common.model.Customer;
import com.bookstore.common.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of UserDetailsService for loading customer details
 * based on email address during authentication.
 */
@Service
public class CustomDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomDetailsService.class);
    private final CustomerRepository repo;

    public CustomDetailsService(CustomerRepository repo) {
        this.repo = repo;
    }

    /**
     * Loads user details by email address for authentication.
     * Email is used as the username for login purposes.
     *
     * @param email the customer's email address
     * @return UserDetails containing customer information and authorities
     * @throws UsernameNotFoundException if no customer exists with the given email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Attempting to load user details for email: {}", email);

        Customer customer = repo.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found for email: {}", email);
                    return new UsernameNotFoundException("User not found: " + email);
                });

        log.debug("Successfully loaded user details for email: {}", email);
        return new CustomUserDetails(customer);
    }
}