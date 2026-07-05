package com.ankit.ims.controller;

import com.ankit.ims.domain.InventoryItem;
import com.ankit.ims.dto.InventoryDtos.InventoryRequest;
import com.ankit.ims.dto.InventoryDtos.StockUpdateRequest;
import com.ankit.ims.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<InventoryItem> createInventory(@RequestBody InventoryRequest request) {
        InventoryItem created = inventoryService.createInventory(request.sku(), request.quantity(), request.productName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{sku}")
    public ResponseEntity<InventoryItem> getInventory(@PathVariable String sku) {
        return ResponseEntity.ok(inventoryService.getInventory(sku));
    }

    @PostMapping("/deduct")
    public ResponseEntity<InventoryItem> deductStock(@RequestBody StockUpdateRequest request) {
        InventoryItem updated = inventoryService.deductStock(request.sku(), request.quantity());
        return ResponseEntity.ok(updated);
    }
}
