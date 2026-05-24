package io.warehouse.model;

import io.warehouse.enums.ProductType;

public class StandardProduct extends Product{

    public StandardProduct() {
        //No argument constructor
    }

    public StandardProduct(String sku, String name, String description, double unitPrice, int quantity, int reorderThreshold, ProductType type, String zoneId) {
        super(sku, name, description, unitPrice, quantity, reorderThreshold, type, zoneId);
    }

    @Override
    public void validateMovement(StockMovement stockMovement, Zone targetZone) {
        //No movement restrictions
    }

    @Override
    public double calculateValue() {
        return quantity * unitPrice;
    }
}
