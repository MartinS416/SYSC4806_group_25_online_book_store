package com.bookstore.common.controller;

import com.bookstore.common.model.Customer;
import com.bookstore.common.repository.CustomerRepository;
import com.bookstore.common.service.LoginService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link LoginController}.
 *
 * <h2>Test Category:</h2> Unit Tests (UT) – Web/controller layer.
 * <h2>Scope:</h2> Login page model setup and registration success/error flows.
 * <h2>Dependencies:</h2> {@link LoginService}, {@link CustomerRepository} (mocked).
 *
 * @author Lavji, Fareen
 * @version 3.0
 * @since 2025-12-01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LoginController Unit Tests")
class LoginControllerTest {

    @Mock
    private LoginService loginService;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private LoginController controller;

    @Test
    @DisplayName("showLoginPage adds empty Customer as 'user' and returns login view")
    void showLoginPage_populatesModel() {
        Model model = new ExtendedModelMap();

        String view = controller.showLoginPage(model);

        assertEquals("login", view);
        assertInstanceOf(Customer.class, model.getAttribute("user"));
    }

    @Test
    @DisplayName("register success adds success flash and redirects to login#pills-login")
    void register_success_redirectsWithSuccess() {
        Customer customer = new Customer();

        String view = controller.register(customer, redirectAttributes);

        assertEquals("redirect:/login#pills-login", view);
        verify(loginService).registerUser(customer);
        verify(redirectAttributes).addFlashAttribute(eq("success"), anyString());
        verify(redirectAttributes, never()).addFlashAttribute(eq("error"), any());
    }

    @Test
    @DisplayName("register failure adds error flash and redirects with registerError")
    void register_failure_redirectsWithError() {
        Customer customer = new Customer();
        doThrow(new IllegalArgumentException("bad"))
                .when(loginService).registerUser(customer);

        String view = controller.register(customer, redirectAttributes);

        assertEquals("redirect:/login?registerError", view);
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("bad"));
        verify(redirectAttributes, never()).addFlashAttribute(eq("success"), any());
    }
}