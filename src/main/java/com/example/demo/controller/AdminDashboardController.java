package com.example.demo.controller.admin;

import com.example.demo.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

        return "admin/dashboard"; // loads templates/admin/dashboard.html
    }
}
