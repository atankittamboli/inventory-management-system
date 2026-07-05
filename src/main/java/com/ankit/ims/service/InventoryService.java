package com.ankit.ims.service;

import com.ankit.ims.domain.InventoryItem;
import com.ankit.ims.repository.InventoryRepository;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventoryRepository;

    @Value("${inventory.low-stock.threshold:3}")
    private int lowStockThreshold;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public InventoryItem createInventory(String sku, int quantity, String productName) {
        InventoryItem inventoryItem = inventoryRepository.findBySku(sku).orElseGet(InventoryItem::new);
        inventoryItem.setSku(sku);
        inventoryItem.setProductName(productName);
        inventoryItem.setAvailableQuantity(quantity);
        inventoryItem.setUpdatedAt(Instant.now());
        applyLowStockFlag(inventoryItem);
        return inventoryRepository.save(inventoryItem);
    }

    @Transactional(readOnly = true)
    public InventoryItem getInventory(String sku) {
        return inventoryRepository.findBySku(sku)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found for SKU: " + sku));
    }

    @Transactional
    public InventoryItem deductStock(String sku, int quantityToDeduct) {
        if (quantityToDeduct <= 0) {
            throw new IllegalArgumentException("Quantity to deduct must be positive");
        }

        InventoryItem inventoryItem = inventoryRepository.findBySku(sku)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found for SKU: " + sku));

        int availableQuantity = inventoryItem.getAvailableQuantity();
        if (availableQuantity < quantityToDeduct) {
            throw new IllegalStateException("Insufficient inventory for SKU: " + sku);
        }

        inventoryItem.setAvailableQuantity(availableQuantity - quantityToDeduct);
        inventoryItem.setUpdatedAt(Instant.now());
        applyLowStockFlag(inventoryItem);
        return inventoryRepository.save(inventoryItem);
    }

    private void applyLowStockFlag(InventoryItem inventoryItem) {
        boolean wasLowStock = inventoryItem.isLowStock();
        inventoryItem.setLowStock(inventoryItem.getAvailableQuantity() <= lowStockThreshold);
        if (!wasLowStock && inventoryItem.isLowStock()) {
            LOGGER.warn("Low stock warning for SKU {}. Available quantity: {}", inventoryItem.getSku(), inventoryItem.getAvailableQuantity());
        }
    }
}
