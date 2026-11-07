package com.retail.inventra.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double price;
    private Integer stockQuantity;
    private LocalDate expiryDate;

    public Product() {}

    public Product(String name, Double price, Integer stockQuantity, LocalDate expiryDate) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.expiryDate = expiryDate;
    }

    // Helper method to check if product has expired
    public boolean isExpired() {
        if (expiryDate == null) return false;
        return expiryDate.isBefore(LocalDate.now());
    }

    // Helper method to check if product expires within 30 days
    public boolean isExpiringSoon() {
        if (expiryDate == null) return false;
        return !isExpired() && expiryDate.isBefore(LocalDate.now().plusDays(30));
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
}
