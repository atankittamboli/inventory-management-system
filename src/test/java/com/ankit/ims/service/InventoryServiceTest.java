package com.ankit.ims.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ankit.ims.domain.InventoryItem;
import com.ankit.ims.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "inventory.low-stock.threshold=3")
@ActiveProfiles("test")
class InventoryServiceTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @BeforeEach
    void setUp() {
        inventoryRepository.deleteAll();
    }

    @Test
    void shouldDeductStockAndFlagLowStockWhenThresholdIsReached() {
        InventoryItem created = inventoryService.createInventory("SKU-100", 5, "Widget");

        InventoryItem updated = inventoryService.deductStock("SKU-100", 3);

        assertThat(updated.getAvailableQuantity()).isEqualTo(2);
        assertThat(updated.isLowStock()).isTrue();
        assertThat(created.getSku()).isEqualTo("SKU-100");
    }
}
