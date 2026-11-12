package com.retail.inventra.controller;

import com.retail.inventra.model.CartItem;
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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BillingController {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private StoreSettingRepository settingRepo;

    @Autowired
    private com.retail.inventra.repository.SaleRepository saleRepo;

    // Show the billing screen with list of products and customers
    @GetMapping("/pos")
    public String showBillingScreen(Model model) {
        model.addAttribute("products", productRepo.findAll());
        model.addAttribute("customers", customerRepo.findAll());
        return "pos";
    }

    // Process checkout (supports multi-item cart as well as single-item fallback)
    @PostMapping("/pos/checkout")
    public String processCheckout(
            @RequestParam(required = false) List<Long> itemIds,
            @RequestParam(required = false) List<Integer> itemQtys,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false, defaultValue = "1") Integer quantity,
            @RequestParam(defaultValue = "CASH") String paymentType,
            @RequestParam(required = false) Long customerId,
            Model model) {

        List<CartItem> cartItems = new ArrayList<>();

        // 1. Process multi-item list from cart
        if (itemIds != null && !itemIds.isEmpty() && itemQtys != null) {
            for (int i = 0; i < itemIds.size(); i++) {
                Long id = itemIds.get(i);
                Integer qty = (i < itemQtys.size()) ? itemQtys.get(i) : 1;
                if (id != null && qty != null && qty > 0) {
                    Product p = productRepo.findById(id).orElse(null);
                    if (p != null) {
                        if (p.getStockQuantity() < qty) {
                            return "redirect:/pos?error=InsufficientStock";
                        }
                        cartItems.add(new CartItem(p.getId(), p.getName(), p.getPrice(), qty, p.getPrice() * qty));
                    }
                }
            }
        } else if (productId != null) {
            // Fallback for single item checkout
            Product p = productRepo.findById(productId).orElse(null);
            if (p != null) {
                if (p.getStockQuantity() < quantity) {
                    return "redirect:/pos?error=InsufficientStock";
                }
                cartItems.add(new CartItem(p.getId(), p.getName(), p.getPrice(), quantity, p.getPrice() * quantity));
            }
        }

        if (cartItems.isEmpty()) {
            return "redirect:/pos?error=EmptyCart";
        }

        // 2. Deduct stock for each cart item
        double subTotal = 0.0;
        for (CartItem item : cartItems) {
            Product product = productRepo.findById(item.getProductId()).orElse(null);
            if (product != null) {
                product.setStockQuantity(Math.max(0, product.getStockQuantity() - item.getQuantity()));
                productRepo.save(product);
                subTotal += item.getLineTotal();
            }
        }

        // 3. Tax calculation: 5% GST (2.5% CGST + 2.5% SGST)
        double taxAmount = Math.round((subTotal * 0.05) * 100.0) / 100.0;
        double grandTotal = Math.round((subTotal + taxAmount) * 100.0) / 100.0;

        // 4. If payment is UDHAR, record debt on Customer Khata
        Customer customer = null;
        if ("UDHAR".equalsIgnoreCase(paymentType) && customerId != null) {
            customer = customerRepo.findById(customerId).orElse(null);
            if (customer != null) {
                double newDebt = (customer.getCreditBalance() != null ? customer.getCreditBalance() : 0.0) + grandTotal;
                customer.setCreditBalance(Math.round(newDebt * 100.0) / 100.0);
                customerRepo.save(customer);
            }
        }

        // 5. Store info
        StoreSetting store = settingRepo.findAll().stream().findFirst()
                .orElse(new StoreSetting("Inventra Kirana & Supermarket", "Shop #4, Gandhi Main Road, Bengaluru", "+91 98765 43210"));

        // 6. Generate Invoice Number & Bill Timestamp
        String invoiceNumber = "INV-" + (System.currentTimeMillis() % 1000000);
        String billTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));

        // 6b. Record Real Sale Transaction in Database for Analytics
        int totalItemsInBill = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
        com.retail.inventra.model.Sale realSale = new com.retail.inventra.model.Sale(
            invoiceNumber, grandTotal, paymentType, totalItemsInBill, LocalDateTime.now()
        );
        saleRepo.save(realSale);

        // 7. Generate UPI QR Code URL for instant scanning
        String upiPayee = (store.getStoreName() != null ? store.getStoreName() : "Inventra Store").replaceAll("[^a-zA-Z0-9 ]", "");
        String upiUri = String.format("upi://pay?pa=inventra.store@okhdfcbank&pn=%s&am=%.2f&cu=INR&tn=Invoice-%s",
                upiPayee.replace(" ", "%20"), grandTotal, invoiceNumber);
        String upiQrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=180x180&margin=4&data=" +
                URLEncoder.encode(upiUri, StandardCharsets.UTF_8);

        // 8. Populate model for receipt.html
        model.addAttribute("store", store);
        model.addAttribute("invoiceNumber", invoiceNumber);
        model.addAttribute("items", cartItems);
        model.addAttribute("itemCount", cartItems.stream().mapToInt(CartItem::getQuantity).sum());
        model.addAttribute("subTotal", subTotal);
        model.addAttribute("taxAmount", taxAmount);
        model.addAttribute("grandTotal", grandTotal);
        model.addAttribute("paymentType", paymentType);
        model.addAttribute("customer", customer);
        model.addAttribute("billTime", billTime);
        model.addAttribute("upiQrUrl", upiQrUrl);
        model.addAttribute("upiUri", upiUri);

        // Legacy compatibility attributes for older templates
        if (!cartItems.isEmpty()) {
            model.addAttribute("product", productRepo.findById(cartItems.get(0).getProductId()).orElse(null));
            model.addAttribute("quantity", cartItems.get(0).getQuantity());
            model.addAttribute("totalAmount", grandTotal);
        }

        return "receipt";
    }
}
