package com.retail.inventra.model;

import jakarta.persistence.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phone;
    private Double creditBalance = 0.0;

    public Customer() {}

    public Customer(String name, String phone, Double creditBalance) {
        this.name = name;
        this.phone = phone;
        this.creditBalance = (creditBalance != null) ? creditBalance : 0.0;
    }

    // Helper: Generate 1-Click WhatsApp Debt Collection URL
    public String getWhatsAppReminderUrl() {
        if (phone == null || creditBalance == null || creditBalance <= 0) {
            return "#";
        }
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        if (cleanPhone.length() == 10) {
            cleanPhone = "91" + cleanPhone;
        }
        String message = String.format(
            "Namaste %s, this is a gentle reminder from Inventra Store. Your pending Khata balance is ₹%.2f. Kindly settle it at your earliest convenience. Thank you!",
            (name != null ? name : "Customer"),
            creditBalance
        );
        try {
            return "https://api.whatsapp.com/send?phone=" + cleanPhone + "&text=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "https://api.whatsapp.com/send?phone=" + cleanPhone;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Double getCreditBalance() { return creditBalance; }
    public void setCreditBalance(Double creditBalance) { this.creditBalance = creditBalance; }
}
