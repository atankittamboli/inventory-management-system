package com.ankit.ims.dto;

public final class InventoryDtos {

    private InventoryDtos() {
    }

    public record InventoryRequest(String sku, Integer quantity, String productName) {
    }

    public record StockUpdateRequest(String sku, Integer quantity) {
    }
}
