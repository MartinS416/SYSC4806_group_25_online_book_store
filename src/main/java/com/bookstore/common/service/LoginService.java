package com.bookstore.common.service;

import com.bookstore.common.model.Customer;
import com.bookstore.common.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * service for registering and logging in
 */
@Service
public class LoginService {

    private final CustomerRepository repo;
    private final PasswordEncoder encoder;

    public LoginService(CustomerRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    /**
     * checks if email exists
     * @param email
     * @return
     */
    public boolean emailExists(String email) {
        return repo.findByEmail(email).isPresent();
    }

    /**
     * registers users
     * @param customer
     * @return
     */
    public Customer registerUser(Customer customer) {
        if (emailExists(customer.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        String encoded = encoder.encode(customer.getPassword());
        customer.setPassword(encoded);

        try {
            Customer saved = repo.save(customer);

            return saved;
        } catch (Exception e) {
            throw e;
        }
    }
}
