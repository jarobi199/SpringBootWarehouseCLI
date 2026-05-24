package io.warehouse.model;

import io.warehouse.enums.ProductType;
import io.warehouse.exception.MovementValidationException;

public class BulkProduct extends Product {

    private double weightPerUnit;

    public BulkProduct() {
        //No argument constructor
    }

    public BulkProduct(String sku, String name, String description, double price, int quantity, int reorderThreshold, ProductType type, String zoneId, double weightPerUnit) {
        super(sku, name, description, price, quantity, reorderThreshold, type, zoneId);
        this.weightPerUnit = weightPerUnit;
    }

    private static final int MINIMUM_TRANSFER_WEIGHT = 50;

    public double getWeightPerUnit() {
        return weightPerUnit;
    }

    public double totalWeight() {
        return quantity * weightPerUnit;
    }

    @Override
    public void validateMovement(StockMovement stockMovement, Zone targetZone) {
        //Check that transfer is at least the minimum transfer weight
        if(stockMovement.quantity() >= MINIMUM_TRANSFER_WEIGHT) {
            throw new MovementValidationException("Your transfer weight is too small!");
        }
    }

    @Override
    public double calculateValue() {
        return totalWeight() * unitPrice;
    }
}
