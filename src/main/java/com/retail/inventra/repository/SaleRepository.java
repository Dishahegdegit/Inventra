package com.retail.inventra.repository;

import com.retail.inventra.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findBySaleTimeAfterOrderBySaleTimeAsc(LocalDateTime after);
}
