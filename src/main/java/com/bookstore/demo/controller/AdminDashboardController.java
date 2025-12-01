package com.bookstore.demo.controller;

import com.bookstore.demo.dto.OrderLineDto;
import com.bookstore.pos.model.Order;
import com.bookstore.pos.model.OrderLine;
import com.bookstore.pos.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class AdminDashboardController {

    private final OrderService orderService;

    public AdminDashboardController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {

        // Summary Cards (today/total/etc.)
        model.addAttribute("dailyRevenue", orderService.getDailyRevenue());
        model.addAttribute("topBooks", orderService.getTopSellingBooks());
        model.addAttribute("categoryRevenue", orderService.getRevenueByCategory());
        model.addAttribute("orders", orderService.getRecentOrders());

        return "admin/dashboard"; // loads templates/admin/dashboard.html
    }

    @GetMapping("/admin/orders/{id}/items")
    @ResponseBody
    public List<OrderLineDto> getOrderItems(@PathVariable Long id) {
        return orderService.getOrderLineDTOs(id);
    }

}
