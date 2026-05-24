package io.warehouse.model;

import io.warehouse.enums.ProductType;
import io.warehouse.enums.ZoneType;
import io.warehouse.exception.MovementValidationException;

import java.util.List;

public class FragileProduct extends Product {
    private String handlingInstructions;
    private List<ZoneType> allowedZones;

    public FragileProduct() {
        //No argument constructor
    }

    public FragileProduct(String sku, String name, String description, double unitPrice, int quantity, int reorderThreshold, ProductType type, String zoneId, String handlingInstructions, List<ZoneType> allowedZones) {
        super(sku, name, description, unitPrice, quantity, reorderThreshold, type, zoneId);
        this.handlingInstructions = handlingInstructions;
        this.allowedZones = allowedZones;
    }

    public String getHandlingInstructions() {
        return handlingInstructions;
    }

    public List<ZoneType> getAllowedZones() {
        return allowedZones;
    }

    @Override
    public void validateMovement(StockMovement stockMovement, Zone targetZone) {
        //Rejects transfer to disallowed zone types
        //Cannot be stored in DISPATCH zone
        if((!allowedZones.contains(targetZone.getType())) || (ZoneType.DISPATCH.equals(targetZone.getType()))) {
            throw new MovementValidationException("This product cannot be transferred to the zone: " + targetZone.getType());
        }
    }

    @Override
    public double calculateValue() {
        return quantity * unitPrice;
    }

}

