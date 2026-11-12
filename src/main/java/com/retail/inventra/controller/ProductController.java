package com.retail.inventra.controller;

import com.retail.inventra.model.Product;
import com.retail.inventra.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepo;

    // Display list of all products
    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("products", productRepo.findAll());
        return "products"; // loads products.html
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
