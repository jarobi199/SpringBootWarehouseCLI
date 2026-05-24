package io.warehouse.model;

import io.warehouse.enums.HazardClass;
import io.warehouse.enums.ProductType;
import io.warehouse.enums.ZoneType;
import io.warehouse.exception.MovementValidationException;

public class HazardousProduct extends Product {
    private boolean requiresVentilation;
    private HazardClass hazardClass;

    public HazardousProduct() {
        //No argument constructor
    }

    public HazardousProduct(String sku, String name, String description, double unitPrice, int quantity, int reorderThreshold, ProductType type, String zoneId, boolean requiresVentilation, HazardClass hazardClass) {
        super(sku, name, description, unitPrice, quantity, reorderThreshold, type, zoneId);
        this.requiresVentilation = requiresVentilation;
        this.hazardClass = hazardClass;
    }

    public boolean isRequiresVentilation() {
        return requiresVentilation;
    }

    public HazardClass getHazardClass() {
        return hazardClass;
    }

    @Override
    public void validateMovement(StockMovement stockMovement, Zone targetZone) {
        //Restrict storage to HAZMAT zones only
        //Reject non-ventilated zones
        if(!ZoneType.HAZMAT.equals(targetZone.getType())) {
            throw new MovementValidationException("This product can only be stored in the HAZMAT zone!");
        }
    }

}

