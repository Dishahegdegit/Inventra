package com.retail.inventra.controller;

import com.retail.inventra.model.StoreSetting;
import com.retail.inventra.repository.StoreSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class StoreSettingController {

    @Autowired
    private StoreSettingRepository settingRepo;

    // Display Store Settings page (Admin Only)
    @GetMapping("/settings")
    public String showSettings(Model model) {
        StoreSetting store = settingRepo.findAll().stream().findFirst()
                .orElse(new StoreSetting(
                        "Inventra Supermarket",
                        "Shop #4, Gandhi Main Road, Bengaluru",
                        "+91 98765 43210",
                        "contact@inventra.store",
                        "29AABCU9603R1Z7",
                        5.0
                ));
        model.addAttribute("store", store);
        return "settings";
    }

    // Save updated Store Profile & GST Settings (Admin Only)
    @PostMapping({"/settings", "/settings/save"})
    public String updateSettings(@ModelAttribute StoreSetting updatedSetting, Model model) {
        // Validation: Store name
        if (updatedSetting.getStoreName() == null || updatedSetting.getStoreName().trim().isEmpty()) {
            model.addAttribute("store", updatedSetting);
            model.addAttribute("errorMessage", "Store Name is required and cannot be empty.");
            return "settings";
        }

        // Validation: Phone
        if (updatedSetting.getPhone() == null || updatedSetting.getPhone().trim().isEmpty()) {
            model.addAttribute("store", updatedSetting);
            model.addAttribute("errorMessage", "Contact Phone Number is required.");
            return "settings";
        }

        // Validation: Email
        if (updatedSetting.getEmail() == null || !updatedSetting.getEmail().contains("@")) {
            model.addAttribute("store", updatedSetting);
            model.addAttribute("errorMessage", "Please provide a valid Store Email Address.");
            return "settings";
        }

        // Validation: GSTIN
        if (updatedSetting.getGstin() == null || updatedSetting.getGstin().trim().isEmpty()) {
            model.addAttribute("store", updatedSetting);
            model.addAttribute("errorMessage", "Store GSTIN / Tax Identification Number is required.");
            return "settings";
        }

        // Validation: GST Rate (Only permitted slabs: 0%, 5%, 12%, and 18%)
        if (updatedSetting.getGstPercentage() == null) {
            model.addAttribute("store", updatedSetting);
            model.addAttribute("errorMessage", "GST Percentage is required.");
            return "settings";
        }
        double rate = Math.round(updatedSetting.getGstPercentage() * 10.0) / 10.0;
        if (rate != 0.0 && rate != 5.0 && rate != 12.0 && rate != 18.0) {
            model.addAttribute("store", updatedSetting);
            model.addAttribute("errorMessage", "GST percentage must be one of the permitted rates: 0%, 5%, 12%, or 18%.");
            return "settings";
        }

        // Persist update in DB
        StoreSetting store = settingRepo.findAll().stream().findFirst().orElse(new StoreSetting());
        store.setStoreName(updatedSetting.getStoreName().trim());
        store.setAddress(updatedSetting.getAddress() != null ? updatedSetting.getAddress().trim() : "");
        store.setPhone(updatedSetting.getPhone().trim());
        store.setEmail(updatedSetting.getEmail().trim());
        store.setGstin(updatedSetting.getGstin().trim().toUpperCase());
        store.setGstPercentage(Math.round(updatedSetting.getGstPercentage() * 100.0) / 100.0);

        settingRepo.save(store);
        return "redirect:/settings?success=true";
    }
}
