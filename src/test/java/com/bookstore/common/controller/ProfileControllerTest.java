package com.bookstore.common.controller;

import com.bookstore.common.model.Customer;
import com.bookstore.common.repository.CustomerRepository;
import com.bookstore.pos.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ProfileController}.
 *
 * <h2>Test Category:</h2> Unit Tests (UT) – Web/controller layer.
 * <h2>Scope:</h2> Viewing profile, updating profile fields, and changing password.
 * <h2>Dependencies:</h2> {@link CustomerRepository}, {@link PasswordEncoder} (mocked).
 *
 * @author Lavji, Fareen
 * @version 3.0
 * @since 2025-12-01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProfileController Unit Tests")
class ProfileControllerTest {

    @Mock
    private CustomerRepository customerRepo;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ProfileController controller;

    private Principal principalWithEmail() {
        return () -> "user@example.com";
    }

    @Test
    @DisplayName("viewProfile loads logged-in customer and populates model")
    void viewProfile_populatesModel() {
        Customer c = new Customer();
        when(customerRepo.findByEmail("user@example.com"))
                .thenReturn(Optional.of(c));
        Model model = new ExtendedModelMap();

        String view = controller.viewProfile(model, principalWithEmail());

        assertEquals("profile", view);
        assertSame(c, model.getAttribute("customer"));
    }

    @Test
    @DisplayName("updateProfile updates non-blank fields and saves customer")
    void updateProfile_updatesAndSaves() {
        Customer c = new Customer();
        when(customerRepo.findByEmail("user@example.com"))
                .thenReturn(Optional.of(c));
        Model model = new ExtendedModelMap();

        String view = controller.updateProfile(
                "John", "Doe", "12345",
                principalWithEmail(),
                model
        );

        assertEquals("profile", view);
        assertEquals("John", c.getFirstName());
        assertEquals("Doe", c.getLastName());
        assertEquals("12345", c.getPhone());
        verify(customerRepo).save(c);
        assertSame(c, model.getAttribute("customer"));
        assertEquals("Profile updated successfully!", model.getAttribute("success"));
    }

    @Test
    @DisplayName("updateProfile ignores blank fields and still saves")
    void updateProfile_ignoresBlankFields() {
        Customer c = new Customer();
        c.setFirstName("Existing");
        when(customerRepo.findByEmail("user@example.com"))
                .thenReturn(Optional.of(c));
        Model model = new ExtendedModelMap();

        String view = controller.updateProfile(
                "", null, "",
                principalWithEmail(),
                model
        );

        assertEquals("profile", view);
        assertEquals("Existing", c.getFirstName());
        verify(customerRepo).save(c);
    }

    @Test
    @DisplayName("changePassword returns error when old password does not match")
    void changePassword_oldPasswordIncorrect() {
        Customer c = new Customer();
        c.setPassword("encoded");
        when(customerRepo.findByEmail("user@example.com"))
                .thenReturn(Optional.of(c));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);
        Model model = new ExtendedModelMap();

        String view = controller.changePassword(
                "wrong", "new", "new",
                principalWithEmail(),
                model
        );

        assertEquals("profile", view);
        assertEquals("Old password is incorrect.", model.getAttribute("error"));
        assertSame(c, model.getAttribute("customer"));
        verify(customerRepo, never()).save(any());
    }

    @Test
    @DisplayName("changePassword returns error when new passwords do not match")
    void changePassword_newPasswordsDoNotMatch() {
        Customer c = new Customer();
        c.setPassword("encoded");
        when(customerRepo.findByEmail("user@example.com"))
                .thenReturn(Optional.of(c));
        when(passwordEncoder.matches("old", "encoded")).thenReturn(true);
        Model model = new ExtendedModelMap();

        String view = controller.changePassword(
                "old", "a", "b",
                principalWithEmail(),
                model
        );

        assertEquals("profile", view);
        assertEquals("New passwords do not match.", model.getAttribute("error"));
        assertSame(c, model.getAttribute("customer"));
        verify(customerRepo, never()).save(any());
    }

    @Test
    @DisplayName("changePassword encodes new password and saves on success")
    void changePassword_success() {
        Customer c = new Customer();
        c.setPassword("encoded");
        when(customerRepo.findByEmail("user@example.com"))
                .thenReturn(Optional.of(c));
        when(passwordEncoder.matches("old", "encoded")).thenReturn(true);
        when(passwordEncoder.encode("new")).thenReturn("new-encoded");
        Model model = new ExtendedModelMap();

        String view = controller.changePassword(
                "old", "new", "new",
                principalWithEmail(),
                model
        );

        assertEquals("profile", view);
        assertEquals("new-encoded", c.getPassword());
        verify(customerRepo).save(c);
        assertEquals("Password changed successfully!", model.getAttribute("success"));
        assertSame(c, model.getAttribute("customer"));
    }
}