package com.retail.inventra.controller;

import com.retail.inventra.model.Product;
import com.retail.inventra.model.ReorderItem;
import com.retail.inventra.model.StoreSetting;
import com.retail.inventra.repository.ProductRepository;
import com.retail.inventra.repository.StoreSettingRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private StoreSettingRepository settingRepo;

    // Printable Purchase Order page for products with stockQuantity <= 5 OR expiring within 15 days
    @GetMapping({"/inventory/reorder", "/products/reorder"})
    public String showSupplierPurchaseOrder(Model model) {
        StoreSetting store = settingRepo.findAll().stream().findFirst()
                .orElse(new StoreSetting("Inventra Supermarket", "Shop #4, Gandhi Main Road, Bengaluru", "+91 98765 43210", "contact@inventra.store", "29AABCU9603R1Z7", 5.0));

        LocalDate today = LocalDate.now();
        List<Product> products = productRepo.findAll();
        List<ReorderItem> reorderItems = new ArrayList<>();
        int totalUnits = 0;
        double subTotal = 0.0;

        for (Product p : products) {
            int currentStock = p.getStockQuantity() != null ? p.getStockQuantity() : 0;
            boolean lowStock = currentStock <= 5;
            boolean expiringSoon = p.isExpiringWithin15Days();

            if (lowStock || expiringSoon) {
                int recommendedQty = Math.max(0, 50 - currentStock);
                double unitCost = p.getPurchasePrice() != null ? p.getPurchasePrice() : (p.getPrice() != null ? Math.round(p.getPrice() * 0.8 * 100.0) / 100.0 : 0.0);
                double lineCost = Math.round((unitCost * recommendedQty) * 100.0) / 100.0;

                String priority;
                String reason;
                if (currentStock <= 0) {
                    priority = "CRITICAL - OUT OF STOCK";
                    reason = "Out of Stock (0 units remaining)";
                } else if (lowStock && expiringSoon) {
                    priority = "URGENT - LOW & EXPIRING";
                    reason = "Low Stock (" + currentStock + " units) & Expires within 15 days";
                } else if (lowStock) {
                    priority = currentStock <= 2 ? "URGENT - CRITICAL LOW" : "HIGH - REORDER NEEDED";
                    reason = "Low Stock Level (" + currentStock + " units left <= 5)";
                } else {
                    priority = "HIGH - EXPIRING SOON";
                    reason = "Expires soon (" + (p.getExpiryDate() != null ? p.getExpiryDate().toString() : "Near Expiry") + ")";
                }

                reorderItems.add(new ReorderItem(p, p.getName(), p.getDisplaySku(), p.getCategory(), currentStock, recommendedQty, unitCost, lineCost, priority, reason));
                totalUnits += recommendedQty;
                subTotal += lineCost;
            }
        }

        reorderItems.sort(Comparator.comparingInt(ReorderItem::getCurrentStock));

        String poNumber = "PO-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + (System.currentTimeMillis() % 10000);
        String poDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm a"));
        double gstRate = (store.getGstPercentage() != null && store.getGstPercentage() >= 0) ? store.getGstPercentage() : 5.0;
        double estimatedTax = Math.round((subTotal * (gstRate / 100.0)) * 100.0) / 100.0;
        double grandTotal = Math.round((subTotal + estimatedTax) * 100.0) / 100.0;

        model.addAttribute("store", store);
        model.addAttribute("poNumber", poNumber);
        model.addAttribute("poDate", poDate);
        model.addAttribute("reorderItems", reorderItems);
        model.addAttribute("totalItemsCount", reorderItems.size());
        model.addAttribute("totalUnitsCount", totalUnits);
        model.addAttribute("subTotalCost", subTotal);
        model.addAttribute("gstRate", gstRate);
        model.addAttribute("estimatedTax", estimatedTax);
        model.addAttribute("grandTotalCost", grandTotal);

        return "reorder";
    }

    // Display list of all products in Obsidian & Neon Mint grid view
    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("products", productRepo.findAll());
        return "products"; // loads products.html
    }

    // Export all products as downloadable CSV report
    @GetMapping("/products/export")
    public void exportProductsCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"Inventra_Stock_Audit_Report.csv\"");

        PrintWriter writer = response.getWriter();
        // UTF-8 BOM so spreadsheet apps (Excel, Numbers, Sheets) parse UTF-8 characters cleanly
        writer.write('\ufeff');

        // Header
        writer.println("ID,Name,SKU,Category,Stock Level,Cost Price,Selling Price,Expiry Date,Status");

        List<Product> products = productRepo.findAll();
        for (Product p : products) {
            String id = p.getId() != null ? p.getId().toString() : "";
            String name = escapeCsv(p.getName());
            String sku = escapeCsv(p.getDisplaySku());
            String category = escapeCsv(p.getCategory());
            String stock = p.getStockQuantity() != null ? p.getStockQuantity().toString() : "0";
            String costPrice = p.getPurchasePrice() != null ? String.format("%.2f", p.getPurchasePrice()) : "0.00";
            String sellingPrice = p.getPrice() != null ? String.format("%.2f", p.getPrice()) : "0.00";
            String expiryDate = p.getExpiryDate() != null ? p.getExpiryDate().toString() : "N/A";

            // Determine status using existing helper methods
            String status;
            if (p.isExpired()) {
                status = "EXPIRED";
            } else if (p.isExpiringSoon()) {
                status = "EXPIRING SOON";
            } else if (p.getStockQuantity() != null && p.getStockQuantity() <= 5) {
                status = "LOW STOCK";
            } else {
                status = "GOOD";
            }

            writer.println(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s",
                    id, name, sku, category, stock, costPrice, sellingPrice, expiryDate, escapeCsv(status)));
        }
        writer.flush();
    }

    // Export Supplier Reorder Sheet for Low Stock (<= 5) OR Expiring Soon (within 15 days)
    @GetMapping("/products/reorder-export")
    public void exportSupplierReorderSheet(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"Inventra_Supplier_Reorder_Sheet.csv\"");

        PrintWriter writer = response.getWriter();
        // UTF-8 BOM so spreadsheet applications open symbols/characters cleanly
        writer.write('\ufeff');

        // Header: Product Name, SKU, Category, Current Stock, Suggested Reorder Quantity, Unit Cost Price (₹), Estimated Order Cost (₹), Priority Status
        writer.println("Product Name,SKU,Category,Current Stock,Suggested Reorder Quantity,Unit Cost Price (₹),Estimated Order Cost (₹),Priority Status");

        // Filter products satisfying either condition:
        // 1) stockQuantity <= 5
        // 2) expiryDate is within the next 15 days
        List<Product> reorderProducts = productRepo.findAll().stream()
                .filter(p -> {
                    int stock = p.getStockQuantity() != null ? p.getStockQuantity() : 0;
                    boolean lowStock = stock <= 5;
                    boolean expiringSoon = p.isExpiringWithin15Days();
                    return lowStock || expiringSoon;
                })
                .sorted((a, b) -> Integer.compare(
                        a.getStockQuantity() != null ? a.getStockQuantity() : 0,
                        b.getStockQuantity() != null ? b.getStockQuantity() : 0))
                .toList();

        for (Product p : reorderProducts) {
            String name = escapeCsv(p.getName());
            String sku = escapeCsv(p.getDisplaySku());
            String category = escapeCsv(p.getCategory());
            int currentStock = p.getStockQuantity() != null ? p.getStockQuantity() : 0;

            // Recommended reorder quantity: Math.max(0, 50 - currentStock)
            int suggestedReorder = Math.max(0, 50 - currentStock);

            double unitCost = p.getPurchasePrice() != null ? p.getPurchasePrice() : (p.getPrice() != null ? p.getPrice() * 0.8 : 0.0);
            double estimatedCost = Math.round((unitCost * suggestedReorder) * 100.0) / 100.0;

            String priority;
            if (currentStock <= 0) {
                priority = "CRITICAL - OUT OF STOCK";
            } else if (p.isExpiringWithin15Days()) {
                priority = currentStock <= 5 ? "URGENT - LOW STOCK & EXPIRING" : "HIGH - EXPIRING SOON";
            } else if (currentStock <= 2) {
                priority = "URGENT - CRITICAL LOW";
            } else {
                priority = "HIGH - REORDER NEEDED";
            }

            writer.println(String.format("%s,%s,%s,%d,%d,%.2f,%.2f,%s",
                    name, sku, category, currentStock, suggestedReorder, unitCost, estimatedCost, escapeCsv(priority)));
        }
        writer.flush();
    }

    // Helper: RFC 4180 standard escaping + Formula Injection Protection (CWE-1236)
    private String escapeCsv(String value) {
        if (value == null) return "";

        String cleaned = value;
        // Formula injection protection: if string starts with dangerous characters, prefix with single quote
        if (!cleaned.isEmpty()) {
            char firstChar = cleaned.charAt(0);
            if (firstChar == '=' || firstChar == '+' || firstChar == '-' || firstChar == '@' || firstChar == '\t' || firstChar == '\r') {
                cleaned = "'" + cleaned;
            }
        }

        boolean containsSpecial = cleaned.contains(",") || cleaned.contains("\"") || cleaned.contains("\n") || cleaned.contains("\r");
        String escaped = cleaned.replace("\"", "\"\"");
        return containsSpecial ? "\"" + escaped + "\"" : escaped;
    }

    // Display the form to add a product
    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "add-product"; // loads add-product.html
    }

    // Save a new product to database
    @PostMapping("/products/add")
    public String saveProduct(@ModelAttribute Product product) {
        if (product.getPurchasePrice() == null && product.getPrice() != null) {
            product.setPurchasePrice(Math.round(product.getPrice() * 0.8 * 100.0) / 100.0);
        }
        productRepo.save(product);
        return "redirect:/products";
    }

    // Delete a product
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productRepo.deleteById(id);
        return "redirect:/products";
    }
}
