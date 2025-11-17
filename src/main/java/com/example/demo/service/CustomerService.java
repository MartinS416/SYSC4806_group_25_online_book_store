package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.model.Address;
import com.example.demo.model.Cart;
import com.example.demo.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.controller.CustomerController.getCustomer;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer create(Customer customer) { return customerRepository.save(customer); }

    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
    }

    public List<Customer> findAll() { return customerRepository.findAll(); }

    public Customer update(Long id, Customer updated) {
        Customer c = findById(id);

        return getCustomer(updated, c, customerRepository);
    }

    public void delete(Long id) { customerRepository.deleteById(id); }

    // RELATIONSHIP HELPERS //

    public void addAddress(Customer customer, Address address) {
        customer.addAddress(address);
        address.setCustomer(customer);
        customerRepository.save(customer);
    }

    public void addCart(Customer customer, Cart cart) {
        customer.addCart(cart);
        cart.setCustomer(customer);
        customerRepository.save(customer);
    }
}