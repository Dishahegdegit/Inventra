package com.retail.inventra.config;

import com.retail.inventra.model.Customer;
import com.retail.inventra.model.Product;
import com.retail.inventra.model.StoreSetting;
import com.retail.inventra.repository.CustomerRepository;
import com.retail.inventra.repository.ProductRepository;
import com.retail.inventra.repository.StoreSettingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            ProductRepository productRepo,
            CustomerRepository customerRepo,
            StoreSettingRepository settingRepo) {
        return args -> {
            if (productRepo.count() == 0) {
                productRepo.save(new Product("Amul Fresh Milk", 30.0, 20, LocalDate.now().plusMonths(3)));
                productRepo.save(new Product("Aashirvaad Atta 5kg", 250.0, 15, LocalDate.now().plusMonths(6)));
                productRepo.save(new Product("Fortune Sunflower Oil 1L", 140.0, 10, LocalDate.now().plusMonths(9)));
            }

            if (customerRepo.count() == 0) {
                customerRepo.save(new Customer("Ramesh Kumar", "9876543210", 450.0));
                customerRepo.save(new Customer("Suresh Raina", "9812345678", 0.0));
                customerRepo.save(new Customer("Priya Sharma", "9900112233", 120.0));
            }

            if (settingRepo.count() == 0) {
                settingRepo.save(new StoreSetting("Inventra Store", "12 Main Market Road", "+91 9876543210"));
            }
        };
    }
}
