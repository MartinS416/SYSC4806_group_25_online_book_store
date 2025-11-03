package com.example.demo.model;

import jakarta.persistence.*;

import java.util.ArrayList;

/**
 * Defines bookstore customers and their commercial history.
 */
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private ArrayList<Order> orderInfos = new ArrayList<>();

    @OneToMany(mappedBy = "customer")
    private ArrayList<Cart> carts = new ArrayList<>();

    @OneToMany(mappedBy = "customer")
    private ArrayList<Address> addresses = new ArrayList<>();

    // CONSTRUCTORS //
    public Customer() {
    }

    public Customer(String username, String password, String email, String firstName, String lastName, String phone) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    // GETTERS //
    public Long getId() { return id; }
    public String getUsername() {  return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getFirstName() {  return firstName; }
    public String getLastName() {  return lastName; }
    public String getPhone() { return phone; }

    // SETTERS //
    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setPhone(String phone) { this.phone = phone; }
}
