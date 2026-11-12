package com.retail.inventra.config;

import com.retail.inventra.model.Customer;
import com.retail.inventra.model.Product;
import com.retail.inventra.model.Sale;
import com.retail.inventra.model.StoreSetting;
import com.retail.inventra.repository.CustomerRepository;
import com.retail.inventra.repository.ProductRepository;
import com.retail.inventra.repository.SaleRepository;
import com.retail.inventra.repository.StoreSettingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            ProductRepository productRepo,
            CustomerRepository customerRepo,
            StoreSettingRepository settingRepo,
            SaleRepository saleRepo) {
        return args -> {
            if (productRepo.count() == 0) {
                // 1. Amul Taaza Milk (Low Stock & Expiring soon)
                productRepo.save(new Product(
                        "Amul Taaza Homogenised Toned Milk 500ml",
                        32.0, 26.0, 4,
                        LocalDate.now().plusDays(5),
                        "Dairy & Bakery", "SKU-MLK-0012",
                        "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=500&auto=format&fit=crop&q=80"
                ));

                // 2. Aashirvaad Chakki Atta
                productRepo.save(new Product(
                        "Aashirvaad Superior MP Whole Wheat Atta 5kg",
                        265.0, 215.0, 38,
                        LocalDate.now().plusMonths(6),
                        "Staples", "SKU-ATT-0451",
                        "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500&auto=format&fit=crop&q=80"
                ));

                // 3. Cadbury Silk
                productRepo.save(new Product(
                        "Cadbury Dairy Milk Silk Chocolate Bar 60g",
                        85.0, 68.0, 18,
                        LocalDate.now().plusMonths(3),
                        "Snacks & Confectionery", "SKU-CHO-0899",
                        "https://images.unsplash.com/photo-1549007994-cb92caebd54b?w=500&auto=format&fit=crop&q=80"
                ));

                // 4. Fortune Sunflower Oil
                productRepo.save(new Product(
                        "Fortune Sunlite Refined Sunflower Oil Pouch 1L",
                        145.0, 120.0, 12,
                        LocalDate.now().plusMonths(9),
                        "Groceries", "SKU-OIL-0210",
                        "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=500&auto=format&fit=crop&q=80"
                ));

                // 5. Maggi Noodles
                productRepo.save(new Product(
                        "Maggi 2-Minute Instant Masala Noodles 280g",
                        56.0, 44.0, 25,
                        LocalDate.now().plusMonths(5),
                        "Snacks & Confectionery", "SKU-NDL-0301",
                        "https://images.unsplash.com/photo-1612927601601-6638404737ce?w=500&auto=format&fit=crop&q=80"
                ));

                // 6. Dettol Handwash (Low Stock alert)
                productRepo.save(new Product(
                        "Dettol Original Germ Protection Liquid Handwash 200ml",
                        99.0, 78.0, 3,
                        LocalDate.now().plusYears(1),
                        "Groceries", "SKU-HND-0711",
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
                        "Staples", "SKU-SLT-0044",
                        "https://images.unsplash.com/photo-1518843875459-f738682238a6?w=500&auto=format&fit=crop&q=80"
                ));
            }

            if (customerRepo.count() == 0) {
                customerRepo.save(new Customer("Ramesh Kumar", "9876543210", 1450.0));
                customerRepo.save(new Customer("Suresh Raina", "9812345678", 0.0));
                customerRepo.save(new Customer("Priya Sharma", "9900112233", 320.0));
            }

            if (settingRepo.count() == 0) {
                settingRepo.save(new StoreSetting("Inventra Supermarket", "Shop #4, Gandhi Main Road, Bengaluru", "+91 98765 43210", "contact@inventra.store", "29AABCU9603R1Z7", 5.0));
            }

            // Seed Realistic Past 7 Days Sales for Executive Analytics Charts
            if (saleRepo.count() == 0) {
                LocalDateTime now = LocalDateTime.now();
                saleRepo.save(new Sale("INV-910241", 1845.0, "CASH", 8, now.minusDays(6)));
                saleRepo.save(new Sale("INV-910242", 2120.0, "CASH", 12, now.minusDays(5)));
                saleRepo.save(new Sale("INV-910243", 1980.0, "UDHAR", 6, now.minusDays(4)));
                saleRepo.save(new Sale("INV-910244", 2489.0, "CASH", 14, now.minusDays(3)));
                saleRepo.save(new Sale("INV-910245", 2850.0, "CASH", 16, now.minusDays(2)));
                saleRepo.save(new Sale("INV-910246", 3420.0, "CASH", 19, now.minusDays(1)));
                saleRepo.save(new Sale("INV-910247", 1450.0, "UDHAR", 5, now.minusHours(4)));
                saleRepo.save(new Sale("INV-910248", 1730.0, "CASH", 9, now.minusHours(1)));
            }
        };
    }
}
