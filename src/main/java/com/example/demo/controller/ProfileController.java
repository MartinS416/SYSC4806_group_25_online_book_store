package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final CustomerRepository customerRepo;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(CustomerRepository customerRepo,
                             PasswordEncoder passwordEncoder) {
        this.customerRepo = customerRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Pull data for customer profile
     * @param model
     * @param principal
     * @return
     */
    @GetMapping
    public String viewProfile(Model model, Principal principal) {

        Customer customer = getLoggedInCustomer(principal);
        model.addAttribute("customer", customer);

        return "profile";
    }

    /**
     * updates customer information
     * @param firstName
     * @param lastName
     * @param phone
     * @param principal
     * @param model
     * @return
     */
    @PostMapping("/update")
    public String updateProfile(@RequestParam(required = false) String firstName,
                                @RequestParam(required = false) String lastName,
                                @RequestParam(required = false) String phone,
                                Principal principal,
                                Model model) {

        Customer customer = getLoggedInCustomer(principal);

        if (firstName != null && !firstName.isBlank()) {
            customer.setFirstName(firstName);
        }

        if (lastName != null && !lastName.isBlank()) {
            customer.setLastName(lastName);
        }

        if (phone != null && !phone.isBlank()) {
            customer.setPhone(phone);
        }

        customerRepo.save(customer);

        model.addAttribute("customer", customer);
        model.addAttribute("success", "Profile updated successfully!");

        return "profile";
    }

    /**
     * updates a password for a user
     * @param oldPassword
     * @param newPassword
     * @param confirmPassword
     * @param principal
     * @param model
     * @return
     */
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Principal principal,
                                 Model model) {

        Customer customer = getLoggedInCustomer(principal);

        if (!passwordEncoder.matches(oldPassword, customer.getPassword())) {
            model.addAttribute("error", "Old password is incorrect.");
            model.addAttribute("customer", customer);
            return "profile";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New passwords do not match.");
            model.addAttribute("customer", customer);
            return "profile";
        }

        customer.setPassword(passwordEncoder.encode(newPassword));
        customerRepo.save(customer);

        model.addAttribute("customer", customer);
        model.addAttribute("success", "Password changed successfully!");

        return "profile";
    }

    /**
     * helper function to get user information
     * @param principal
     * @return
     */
    private Customer getLoggedInCustomer(Principal principal) {
        return customerRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found!"));
    }

}
