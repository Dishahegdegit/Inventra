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
                // 1. Amul Taaza Milk (Low Stock & Expiring soon)
                productRepo.save(new Product(
                        "Amul Taaza Homogenised Toned Milk 500ml",
                        32.0, 26.0, 4,
                        LocalDate.now().plusDays(5),
                        "Dairy", "SKU-MLK-0012",
                        "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=500&auto=format&fit=crop&q=80"
                ));

                // 2. Aashirvaad Chakki Atta
                productRepo.save(new Product(
                        "Aashirvaad Superior MP Whole Wheat Atta 5kg",
                        265.0, 215.0, 38,
                        LocalDate.now().plusMonths(6),
                        "Grains & Flour", "SKU-ATT-0451",
                        "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500&auto=format&fit=crop&q=80"
                ));

                // 3. Cadbury Silk
                productRepo.save(new Product(
                        "Cadbury Dairy Milk Silk Chocolate Bar 60g",
                        85.0, 68.0, 18,
                        LocalDate.now().plusMonths(3),
                        "Snacks", "SKU-CHO-0899",
                        "https://images.unsplash.com/photo-1549007994-cb92caebd54b?w=500&auto=format&fit=crop&q=80"
                ));

                // 4. Fortune Sunflower Oil
                productRepo.save(new Product(
                        "Fortune Sunlite Refined Sunflower Oil Pouch 1L",
                        145.0, 120.0, 12,
                        LocalDate.now().plusMonths(9),
                        "Cooking & Oils", "SKU-OIL-0210",
                        "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=500&auto=format&fit=crop&q=80"
                ));

                // 5. Maggi Noodles
                productRepo.save(new Product(
                        "Maggi 2-Minute Instant Masala Noodles 280g",
                        56.0, 44.0, 25,
                        LocalDate.now().plusMonths(5),
                        "Snacks", "SKU-NDL-0301",
                        "https://images.unsplash.com/photo-1612927601601-6638404737ce?w=500&auto=format&fit=crop&q=80"
                ));

                // 6. Dettol Handwash (Low Stock alert)
                productRepo.save(new Product(
                        "Dettol Original Germ Protection Liquid Handwash 200ml",
                        99.0, 78.0, 3,
                        LocalDate.now().plusYears(1),
                        "Household", "SKU-HND-0711",
                        "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=500&auto=format&fit=crop&q=80"
                ));

                // 7. Nescafe Classic Coffee
                productRepo.save(new Product(
                        "Nescafe Classic 100% Pure Instant Coffee Jar 50g",
                        185.0, 145.0, 14,
                        LocalDate.now().plusMonths(8),
                        "Beverages", "SKU-COF-0150",
                        "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=500&auto=format&fit=crop&q=80"
                ));

                // 8. Tata Salt
                productRepo.save(new Product(
                        "Tata Salt Vacuum Evaporated Iodised Salt 1kg",
                        28.0, 22.0, 52,
                        LocalDate.now().plusYears(2),
                        "Cooking & Oils", "SKU-SLT-0044",
                        "https://images.unsplash.com/photo-1518843875459-f738682238a6?w=500&auto=format&fit=crop&q=80"
                ));
            }

            if (customerRepo.count() == 0) {
                customerRepo.save(new Customer("Ramesh Kumar", "9876543210", 450.0));
                customerRepo.save(new Customer("Suresh Raina", "9812345678", 0.0));
                customerRepo.save(new Customer("Priya Sharma", "9900112233", 120.0));
            }

            if (settingRepo.count() == 0) {
                settingRepo.save(new StoreSetting("Inventra Store", "12 Main Market Road, City", "+91 9876543210"));
            }
        };
    }
}
