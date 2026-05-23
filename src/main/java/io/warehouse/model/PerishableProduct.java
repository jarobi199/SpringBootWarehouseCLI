package io.warehouse.model;

import io.warehouse.exception.MovementValidationException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PerishableProduct extends Product {

    private LocalDate expiryDate;

    public long daysUntilExpiry() {
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    @Override
    public void validateMovement(StockMovement stockMovement, Zone targetZone) {
        //Blocks dispatch of expired stock
        if(expiryDate.isBefore(LocalDate.now())) {
            throw new MovementValidationException("This product has expired!");
        }
    }

    public boolean isNearingExpiry(int days) {
        return expiryDate.isBefore(LocalDate.now().plusDays(days));
    }
}
