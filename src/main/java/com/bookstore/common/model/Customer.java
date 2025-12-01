package com.bookstore.common.model;

import com.bookstore.pos.model.Cart;
import com.bookstore.pos.model.Order;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    private String role = "USER";



    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orderInfos = new ArrayList<>();

    @OneToMany(mappedBy = "customer",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cart> carts = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

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

    public Customer(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // GETTERS //
    public Long getId() { return id; }
    public String getUsername() {  return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getFirstName() {  return firstName; }
    public String getLastName() {  return lastName; }
    public String getPhone() { return phone; }
    public List<Order> getOrderInfos() {  return orderInfos; }
    public List<Cart> getCarts() {  return carts; }
    public List<Address> getAddresses() {  return addresses; }
    public String getRole() {return role;}


    // SETTERS //
    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRole(String role) {this.role = role;}
    // DB Management //
    public void addOrderInfo(Order order) { orderInfos.add(order); }
    public void addCart(Cart cart) { carts.add(cart); }
    public void addAddress(Address address) { addresses.add(address); }

    public void getOrderInfo(int orderNumber) { orderInfos.get(orderNumber); }
    public void getOrderInfo(Order order) { orderInfos.get(orderInfos.indexOf(order)); }
    public void removeCart(Cart cart) { carts.remove(cart); }
    public void removeAddress(Address address) { addresses.remove(address); }

    // PRIVATE //
    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
