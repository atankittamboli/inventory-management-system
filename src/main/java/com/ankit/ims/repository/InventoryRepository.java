package com.ankit.ims.repository;

import com.ankit.ims.domain.InventoryItem;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<InventoryItem> findBySku(String sku);

    boolean existsBySku(String sku);
}
