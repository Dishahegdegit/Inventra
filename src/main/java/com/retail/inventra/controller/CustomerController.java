package com.retail.inventra.controller;

import com.retail.inventra.model.Customer;
import com.retail.inventra.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepo;

    // 1. Show the Customer Directory page
    @GetMapping("/customers")
    public String listCustomers(Model model) {
        model.addAttribute("customers", customerRepo.findAll());
        return "customers"; // Opens customers.html
    }

    // 2. Show the form to add a new customer
    @GetMapping("/customers/add")
    public String showAddCustomerForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "add-customer"; // Opens add-customer.html
    }

    // 3. Save newly registered customer to database
    @PostMapping("/customers/add")
    public String saveCustomer(@ModelAttribute Customer customer) {
        customerRepo.save(customer);
        return "redirect:/customers";
    }

    // 4. Repay Udhar (Subtracts amount paid from debt balance)
    @PostMapping("/customers/repay")
    public String repayDebt(@RequestParam Long customerId, @RequestParam Double amountPaid) {
        Customer customer = customerRepo.findById(customerId).orElse(null);
        if (customer != null && amountPaid > 0) {
            double currentDebt = customer.getCreditBalance();
            double updatedDebt = Math.max(0.0, currentDebt - amountPaid);
            customer.setCreditBalance(updatedDebt);
            customerRepo.save(customer);
        }
        return "redirect:/customers";
    }
}