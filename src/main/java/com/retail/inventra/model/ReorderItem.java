package com.retail.inventra.model;

public class ReorderItem {
    private Product product;
    private String name;
    private String sku;
    private String category;
    private int currentStock;
    private int recommendedQuantity;
    private double unitCost;
    private double lineCost;
    private String priority;
    private String reason;

    public ReorderItem() {}

    public ReorderItem(Product product, String name, String sku, String category, int currentStock, int recommendedQuantity, double unitCost, double lineCost, String priority, String reason) {
        this.product = product;
        this.name = name;
        this.sku = sku;
        this.category = category;
        this.currentStock = currentStock;
        this.recommendedQuantity = recommendedQuantity;
        this.unitCost = unitCost;
        this.lineCost = lineCost;
        this.priority = priority;
        this.reason = reason;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public int getRecommendedQuantity() {
        return recommendedQuantity;
    }

    public void setRecommendedQuantity(int recommendedQuantity) {
        this.recommendedQuantity = recommendedQuantity;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public double getLineCost() {
        return lineCost;
    }

    public void setLineCost(double lineCost) {
        this.lineCost = lineCost;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
