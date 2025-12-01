package com.bookstore.inventory.controller;

import com.bookstore.pos.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link AdminDashboardController}.
 *
 * <h2>Test Category:</h2> Unit Tests (UT) – Web/controller layer.
 * <h2>Scope:</h2> Admin dashboard summary model population.
 * <h2>Dependencies:</h2> {@link OrderService} (mocked).
 *
 * @author Lavji, Fareen
 * @version 3.0
 * @since 2025-12-01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AdminDashboardController Unit Tests")
class AdminDashboardControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private AdminDashboardController controller;

    @Test
    @DisplayName("dashboard populates summary model and returns view")
    void dashboard_populatesModel() {
        Model model = new ExtendedModelMap();

        String view = controller.dashboard(model);

        assertEquals("admin/dashboard", view);
        assertNotNull(model.getAttribute("dailyRevenue"));
        assertNotNull(model.getAttribute("topBooks"));
        assertNotNull(model.getAttribute("categoryRevenue"));
        assertNotNull(model.getAttribute("orders"));

        verify(orderService).getDailyRevenue();
        verify(orderService).getTopSellingBooks();
        verify(orderService).getRevenueByCategory();
        verify(orderService).getRecentOrders();
    }
}