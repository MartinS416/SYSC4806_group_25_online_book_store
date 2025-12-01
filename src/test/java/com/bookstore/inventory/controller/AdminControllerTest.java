package com.bookstore.inventory.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link AdminController}.
 *
 * <h2>Test Category:</h2> Unit Tests (UT) – Web/controller layer.
 * <h2>Scope:</h2> Admin landing page view and welcome message.
 *
 * @author Lavji, Fareen
 * @version 3.0
 * @since 2025-12-01
 */
@DisplayName("AdminController Unit Tests")
class AdminControllerTest {

    private final AdminController controller = new AdminController();

    @Test
    @DisplayName("adminHome sets welcome message and returns view")
    void adminHome_setsMessageAndView() {
        Model model = new ExtendedModelMap();

        String view = controller.adminHome(model);

        assertEquals("admin/admin-home", view);
        assertEquals("Welcome, Admin!", model.getAttribute("message"));
    }
}