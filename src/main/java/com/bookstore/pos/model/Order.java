package com.bookstore.pos.model;

import com.bookstore.demo.model.Address;
import com.bookstore.demo.model.Customer;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines customer orders to be completed by bookstore.
 */
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // contact snapshot captured at order time
    private String name;
    private String email;
    private String phone;
    private int total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.NEW;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    private Instant createdAt = Instant.now();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLine> orderLines = new ArrayList<>();

    // CONSTRUCTORS //
    public Order() {}

    // GETTERS //
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Customer getCustomer() { return customer; }
    public Address getAddress() { return address; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public Instant getCreatedAt() { return createdAt; }
    public Payment getPayment() { return payment; }
    public List<OrderLine> getOrderLines() { return orderLines; }

    // SETTERS //
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public void setAddress(Address address) { this.address = address; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setPayment(Payment payment) { this.payment = payment; if (payment != null) payment.setOrder(this); }
    public void setOrderLines(List<OrderLine> orderLines) { this.orderLines = orderLines; }

    // ORDER LINE MANAGEMENT //
    public void addOrderLine(OrderLine line) {
        orderLines.add(line);
        line.setOrder(this);

        BigDecimal lineTotal = line.getPrice().multiply(BigDecimal.valueOf(line.getQuantity()));
        totalAmount = totalAmount.add(lineTotal);
    }

    public void removeOrderLine(OrderLine line) {
        orderLines.remove(line);
        line.setOrder(null);

        BigDecimal lineTotal = line.getPrice().multiply(BigDecimal.valueOf(line.getQuantity()));
        totalAmount = totalAmount.subtract(lineTotal);
    }
}
