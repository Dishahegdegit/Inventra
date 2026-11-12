package com.retail.inventra.controller;

import com.retail.inventra.model.Product;
import com.retail.inventra.repository.ProductRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepo;

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
