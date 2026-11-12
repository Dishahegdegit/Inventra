package com.retail.inventra.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;
    private Double totalAmount;
    private String paymentType; // CASH or UDHAR
    private Integer itemCount;
    private LocalDateTime saleTime;

    public Sale() {}

    public Sale(String invoiceNumber, Double totalAmount, String paymentType, Integer itemCount, LocalDateTime saleTime) {
        this.invoiceNumber = invoiceNumber;
        this.totalAmount = (totalAmount != null) ? totalAmount : 0.0;
        this.paymentType = (paymentType != null) ? paymentType : "CASH";
        this.itemCount = (itemCount != null) ? itemCount : 1;
        this.saleTime = (saleTime != null) ? saleTime : LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public Integer getItemCount() { return itemCount; }
    public void setItemCount(Integer itemCount) { this.itemCount = itemCount; }

    public LocalDateTime getSaleTime() { return saleTime; }
    public void setSaleTime(LocalDateTime saleTime) { this.saleTime = saleTime; }
}
