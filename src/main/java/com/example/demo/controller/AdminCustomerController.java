package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
