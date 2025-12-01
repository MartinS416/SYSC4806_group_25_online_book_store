package com.bookstore.common.controller;

import com.bookstore.common.model.Customer;
import com.bookstore.common.repository.CustomerRepository;
import com.bookstore.common.service.LoginService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
    private final LoginService loginService;
    private final CustomerRepository customerRepo;


    @Value("${app.base-url}")
    private String appBaseUrl;

    public LoginController(LoginService loginService,
                           CustomerRepository customerRepo) {
        this.loginService = loginService;
        this.customerRepo = customerRepo;

    }


    /**
     * mapping for login page
     * @param model
     * @return
     */
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("user", new Customer());
        return "login"; // Thymeleaf page
    }


//    @GetMapping("/debug")
//    @ResponseBody
//    public Object debug(@AuthenticationPrincipal Object p) {
//        return p.getClass().getName();
//    }

    /**
     * mapping for registration page
     * @param customer
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/register")
    public String register(@ModelAttribute Customer customer, RedirectAttributes redirectAttributes) {

        try {
            loginService.registerUser(customer);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login?registerError";
        }

        redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
        return "redirect:/login#pills-login";
    }

}
