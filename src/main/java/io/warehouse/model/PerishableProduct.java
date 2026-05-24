package io.warehouse.model;

import io.warehouse.enums.ProductType;
import io.warehouse.exception.MovementValidationException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PerishableProduct extends Product {

    private final LocalDate expiryDate;

    public PerishableProduct() {
        this.expiryDate = null;
    }

    public PerishableProduct(String sku, String name, String description, double unitPrice, int quantity, int reorderThreshold, ProductType type, String zoneId, LocalDate expiryDate) {
        super(sku, name, description, unitPrice, quantity, reorderThreshold, type, zoneId);
        this.expiryDate = expiryDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public long daysUntilExpiry() {
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    public boolean isNearingExpiry(int days) {
        return expiryDate.isBefore(LocalDate.now().plusDays(days));
    }

    @Override
    public void validateMovement(StockMovement stockMovement, Zone targetZone) {
        //Blocks dispatch of expired stock
        if(expiryDate.isBefore(LocalDate.now())) {
            throw new MovementValidationException("This product has expired!");
        }
    }

    @Override
   public double calculateValue() {
        return quantity * unitPrice;
    }

}
