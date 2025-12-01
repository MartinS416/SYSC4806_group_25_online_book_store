package com.bookstore.common.service;

import com.bookstore.common.model.Customer;
import com.bookstore.common.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link LoginService}.
 *
 * <h2>Test Category:</h2> Unit Tests (UT) – Service layer.
 * <h2>Scope:</h2>
 * <ul>
 *   <li>Email existence checks.</li>
 *   <li>User registration, including duplicate email rejection and password encoding.</li>
 * </ul>
 *
 * @author Skach, Martin; Lavji, Fareen
 * @version 3.0
 * @since 2025.12.01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LoginService Unit Tests")
class LoginServiceTest {

    @Mock
    private CustomerRepository repo;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private LoginService loginService;

    @Test
    @DisplayName("emailExists returns true when repository finds email")
    void emailExists_trueWhenFound() {
        when(repo.findByEmail("a@b.com")).thenReturn(Optional.of(new Customer()));

        assertTrue(loginService.emailExists("a@b.com"));
    }

    @Test
    @DisplayName("emailExists returns false when repository does not find email")
    void emailExists_falseWhenMissing() {
        when(repo.findByEmail("a@b.com")).thenReturn(Optional.empty());

        assertFalse(loginService.emailExists("a@b.com"));
    }

    @Test
    @DisplayName("registerUser throws when email already exists")
    void registerUser_emailAlreadyExists_throws() {
        Customer c = new Customer();
        c.setEmail("a@b.com");
        when(repo.findByEmail("a@b.com")).thenReturn(Optional.of(new Customer()));

        assertThrows(IllegalArgumentException.class, () -> loginService.registerUser(c));
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("registerUser encodes password and saves customer")
    void registerUser_encodesPasswordAndSaves() {
        Customer c = new Customer();
        c.setEmail("a@b.com");
        c.setPassword("plain");
        when(repo.findByEmail("a@b.com")).thenReturn(Optional.empty());
        when(encoder.encode("plain")).thenReturn("encoded");
        when(repo.save(c)).thenReturn(c);

        Customer saved = loginService.registerUser(c);

        assertSame(c, saved);
        assertEquals("encoded", c.getPassword());
        verify(repo).save(c);
    }
}