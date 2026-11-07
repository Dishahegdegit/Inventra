package com.retail.inventra.controller;

import com.retail.inventra.model.Customer;
import com.retail.inventra.model.Product;
import com.retail.inventra.model.StoreSetting;
import com.retail.inventra.repository.CustomerRepository;
import com.retail.inventra.repository.ProductRepository;
import com.retail.inventra.repository.StoreSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class BillingController {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private StoreSettingRepository settingRepo;

    // Show the billing screen with list of products and customers
    @GetMapping("/pos")
    public String showBillingScreen(Model model) {
        model.addAttribute("products", productRepo.findAll());
        model.addAttribute("customers", customerRepo.findAll());
        return "pos"; // loads pos.html
    }

    // Process checkout
    @PostMapping("/pos/checkout")
    public String processCheckout(
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            @RequestParam String paymentType,
            @RequestParam(required = false) Long customerId,
            Model model) {

        Product product = productRepo.findById(productId).orElse(null);
        if (product == null || product.getStockQuantity() < quantity) {
            return "redirect:/pos?error=InsufficientStock";
        }

        // 1. Calculate bill total
        double totalAmount = product.getPrice() * quantity;

        // 2. Deduct stock from Member 2's Product
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepo.save(product);

        // 3. If Udhar, add debt to Member 3's Customer
        Customer customer = null;
        if ("UDHAR".equals(paymentType) && customerId != null) {
            customer = customerRepo.findById(customerId).orElse(null);
            if (customer != null) {
                customer.setCreditBalance(customer.getCreditBalance() + totalAmount);
                customerRepo.save(customer);
            }
        }

        // 4. Fetch store details from Member 1
        StoreSetting store = settingRepo.findAll().stream().findFirst()
                .orElse(new StoreSetting("Inventra Store", "12 Main Market Road", "+91 9876543210"));

        // 5. Send invoice data to receipt.html
        model.addAttribute("store", store);
        model.addAttribute("product", product);
        model.addAttribute("quantity", quantity);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("paymentType", paymentType);
        model.addAttribute("customer", customer);
        model.addAttribute("billTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a")));

        return "receipt"; // loads receipt.html
    }
}
