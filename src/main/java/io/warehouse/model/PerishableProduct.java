package io.warehouse.model;

import io.warehouse.exception.MovementValidationException;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Document(collection = "products")
public class PerishableProduct extends Product {

    private final LocalDate expiryDate;

    public PerishableProduct(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isNearingExpiry(int days) {
        return expiryDate.isBefore(LocalDate.now().plusDays(days));
    }

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

}
