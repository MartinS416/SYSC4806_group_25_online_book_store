package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerRepository customers;

    public CustomerController(CustomerRepository customers) {
        this.customers = customers;
    }

    @GetMapping
    public List<Customer> all() {
        return customers.findAll();
    }

    @GetMapping("/{id}")
    public Customer get(@PathVariable Long id) {
        return customers.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
    }

    @PostMapping
    public ResponseEntity<Customer> create(@RequestBody Customer input) {
        input.setId(null);
        Customer saved = customers.save(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public Customer update(@PathVariable Long id, @RequestBody Customer input) {
        Customer existing = customers.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        return getCustomer(input, existing, customers);
    }

    public static Customer getCustomer(@RequestBody Customer input, Customer existing, CustomerRepository customers) {
        existing.setUsername(input.getUsername());
        existing.setPassword(input.getPassword());
        existing.setEmail(input.getEmail());
        existing.setFirstName(input.getFirstName());
        existing.setLastName(input.getLastName());
        existing.setPhone(input.getPhone());
        return customers.save(existing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!customers.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found");
        }
        customers.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
