package com.bookstore.demo.controller;

import com.bookstore.demo.model.Customer;
import com.bookstore.demo.repository.CustomerRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/customers")
public class AdminCustomerController {

    private final CustomerRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AdminCustomerController(CustomerRepository repo) {
        this.repo = repo;
    }

    // -------------------------
    // LIST + SEARCH
    // -------------------------
    @GetMapping
    public String list(@RequestParam(required = false) String keyword, Model model) {

        List<Customer> customers =
                (keyword == null || keyword.isBlank())
                        ? repo.findAll()
                        : repo.search(keyword.toLowerCase());

        model.addAttribute("customers", customers);
        model.addAttribute("keyword", keyword);

        return "admin/customers";
    }

    // -------------------------
    // ADD NEW CUSTOMER
    // -------------------------
    @GetMapping("/new")
    public String newCustomer(Model model) {
        model.addAttribute("customer", new Customer());
        return "admin/customer-form";
    }

    // -------------------------
    // EDIT CUSTOMER
    // -------------------------
    @GetMapping("/edit/{id}")
    public String editCustomer(@PathVariable Long id, Model model) {
        Customer customer = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        model.addAttribute("customer", customer);
        return "admin/customer-form";
    }

    // -------------------------
    // SAVE (ADD OR EDIT)
    // -------------------------
    @PostMapping
    public String saveCustomer(@ModelAttribute Customer customer) {

        // If creating a new customer → encode password
        if (customer.getId() == null) {
            customer.setPassword(encoder.encode(customer.getPassword()));
        } else {
            // If editing → keep existing password
            Customer existing = repo.findById(customer.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
            customer.setPassword(existing.getPassword());
        }

        repo.save(customer);
        return "redirect:/admin/customers";
    }

    // -------------------------
    // DELETE
    // -------------------------
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/admin/customers";
    }

    /**
     * Handles the AJAX request to check for duplicate emails.
     * The request body should contain the email and the customer ID (for edit mode).
     * @param payload A Map containing "email" and "id" (id may be null for new customer).
     * @return A Map containing the key "isDuplicate" with a boolean value.
     */
    @PostMapping("/check_email")
    @ResponseBody // Tells Spring to return the data directly (JSON) instead of a view name
    public Map<String, Boolean> checkDuplicateEmail(@RequestBody Map<String, Object> payload) {
        String email = (String) payload.get("email");
        // ID comes in as a Long, Integer, or null depending on how Thymeleaf renders it.
        // We'll safely cast it, treating null as 0 for comparison if needed.
        Number idNumber = (Number) payload.get("id");
        Long customerId = (idNumber != null) ? idNumber.longValue() : 0L;

        // 1. Find a customer in the database by the provided email
        Optional<Customer> foundCustomer = repo.findByEmail(email);

        boolean isDuplicate;

        if (foundCustomer.isEmpty()) {
            // Email not found at all -> not a duplicate
            isDuplicate = false;
        } else {
            // Email is found. Check if it belongs to the customer we are currently editing.
            // If the found customer's ID matches the current customerId (editing their own email), it's OK.
            // If the IDs don't match, or if customerId is 0 (new user), it's a duplicate.
            isDuplicate = !foundCustomer.get().getId().equals(customerId);
        }

        // Return a JSON object like: {"isDuplicate": true} or {"isDuplicate": false}
        return Map.of("isDuplicate", isDuplicate);
    }
}
