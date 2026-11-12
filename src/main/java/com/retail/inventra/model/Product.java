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
    private Double purchasePrice;
    private Integer stockQuantity;
    private LocalDate expiryDate;
    private String category = "General";
    private String sku;
    
    @Column(length = 1000)
    private String imageUrl;

    public Product() {}

    public Product(String name, Double price, Double purchasePrice, Integer stockQuantity, LocalDate expiryDate, String category, String sku, String imageUrl) {
        this.name = name;
        this.price = price;
        this.purchasePrice = purchasePrice;
        this.stockQuantity = stockQuantity;
        this.expiryDate = expiryDate;
        this.category = (category != null && !category.isBlank()) ? category : "General";
        this.sku = sku;
        this.imageUrl = imageUrl;
    }

    // Backwards-compatible constructor
    public Product(String name, Double price, Integer stockQuantity, LocalDate expiryDate) {
        this(name, price, price != null ? Math.round(price * 0.8 * 100.0) / 100.0 : null, stockQuantity, expiryDate, "General", null, null);
    }

    // Helper: Check if product has expired
    public boolean isExpired() {
        if (expiryDate == null) return false;
        return expiryDate.isBefore(LocalDate.now());
    }

    // Helper: Check if product expires within 30 days
    public boolean isExpiringSoon() {
        if (expiryDate == null) return false;
        return !isExpired() && expiryDate.isBefore(LocalDate.now().plusDays(30));
    }

    // Helper: Check if product expires within the next 15 days
    public boolean isExpiringWithin15Days() {
        if (expiryDate == null) return false;
        LocalDate today = LocalDate.now();
        return !expiryDate.isBefore(today) && !expiryDate.isAfter(today.plusDays(15));
    }

    // Helper: Calculate profit margin %
    public int getMarginPercent() {
        if (purchasePrice == null || purchasePrice <= 0 || price == null) return 20;
        return (int) Math.round(((price - purchasePrice) / purchasePrice) * 100.0);
    }

    // Helper: Fallback image if null
    public String getDisplayImage() {
        if (imageUrl != null && !imageUrl.isBlank()) return imageUrl;
        return "https://images.unsplash.com/photo-1542838132-92c53300491e?w=500&auto=format&fit=crop&q=80";
    }

    // Helper: Generate SKU if missing
    public String getDisplaySku() {
        if (sku != null && !sku.isBlank()) return sku;
        return "SKU-" + (category != null ? category.substring(0, Math.min(3, category.length())).toUpperCase() : "GEN") + "-" + String.format("%04d", id != null ? id : 1);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(Double purchasePrice) { this.purchasePrice = purchasePrice; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
