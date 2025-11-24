package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.service.LoginService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
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
