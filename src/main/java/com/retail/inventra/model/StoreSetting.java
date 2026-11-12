package com.retail.inventra.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class StoreSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String storeName;
    private String address;
    private String phone;
    private String email = "store@inventra.retail";
    private String gstin = "29AABCU9603R1Z7";
    private Double gstPercentage = 5.0;

    public StoreSetting() {
    }

    public StoreSetting(String storeName, String address, String phone) {
        this.storeName = storeName;
        this.address = address;
        this.phone = phone;
        this.email = "store@inventra.retail";
        this.gstin = "29AABCU9603R1Z7";
        this.gstPercentage = 5.0;
    }

    public StoreSetting(String storeName, String address, String phone, String email, String gstin, Double gstPercentage) {
        this.storeName = storeName;
        this.address = address;
        this.phone = phone;
        this.email = (email != null && !email.isBlank()) ? email : "store@inventra.retail";
        this.gstin = (gstin != null && !gstin.isBlank()) ? gstin : "29AABCU9603R1Z7";
        this.gstPercentage = (gstPercentage != null && gstPercentage >= 0) ? gstPercentage : 5.0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGstin() {
        return gstin;
    }

    public void setGstin(String gstin) {
        this.gstin = gstin;
    }

    public Double getGstPercentage() {
        return gstPercentage;
    }

    public void setGstPercentage(Double gstPercentage) {
        this.gstPercentage = gstPercentage;
    }
}
