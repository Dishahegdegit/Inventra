package com.retail.inventra.config;

import com.retail.inventra.model.StoreSetting;
import com.retail.inventra.repository.StoreSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalStoreAdvice {

    @Autowired
    private StoreSettingRepository settingRepo;

    @ModelAttribute("store")
    public StoreSetting populateStore() {
        return settingRepo.findAll().stream().findFirst()
                .orElse(new StoreSetting("Inventra Supermarket", "Shop #4, Gandhi Main Road, Bengaluru", "+91 98765 43210", "contact@inventra.store", "29AABCU9603R1Z7", 5.0));
    }

    @ModelAttribute("storeName")
    public String populateStoreName() {
        StoreSetting s = populateStore();
        return (s != null && s.getStoreName() != null && !s.getStoreName().isBlank()) ? s.getStoreName() : "Inventra Supermarket";
    }
}
