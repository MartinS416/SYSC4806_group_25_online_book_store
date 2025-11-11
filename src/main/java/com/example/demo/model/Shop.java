package com.example.demo.model;

import jakarta.persistence.*;

/**
 * Feature --> Extend architecture to accommodate multiple merchants/ stores.
 */
@Entity
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    // CONSTRUCTORS //
    public Shop() {}

    public Shop(String name, Merchant merchant) {
        this.name = name;
        this.merchant = merchant;
    }

    // GETTERS //
    public Long getId() { return id; }
    public String getName() { return name; }
    public Merchant getMerchant() { return merchant; }

    // SETTERS //
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setMerchant(Merchant merchant) { this.merchant = merchant; }
}
