package com.bookstore.common.model;

import jakarta.persistence.*;

/**
 * Defines addresses that are stored within a customer's profile and extracted for shipping.
 */
@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id",  nullable = false)
    private Customer customer;

    private String firstName;
    private String lastName;
    private String street;
    private String unit;
    private String city;
    private String region;
    private String postcode;
    private String country;

    // CONSTRUCTORS //
    public Address() {}

    public Address(Long id, String firstName, String lastName,
                   String street, String unit,
                   String city, String region, String postcode,
                   String country) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;

        this.street = street;
        this.unit = unit;

        this.city = city;
        this.region = region;
        this.postcode =  postcode;
        this.country = country;
    }

    // GETTERS //
    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getStreet() { return street; }
    public String getUnit() { return unit; }
    public String getCity() { return city; }
    public String getRegion() { return region; }
    public String getPostcode() { return postcode; }
    public String getCountry() { return country; }

    // SETTERS //
    public void setCustomer(Customer customer) { this.customer = customer; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setStreet(String street) { this.street = street; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setCity(String city) { this.city = city; }
    public void setRegion(String region) { this.region = region; }
    public void setPostcode(String postcode) { this.postcode = postcode; }
    public void setCountry(String country) { this.country = country; }
}
