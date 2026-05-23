package io.warehouse.model;

import io.warehouse.enums.ZoneType;

import java.util.List;

public class FragileProduct extends Product {
    private String handlingInstructions;
    List<ZoneType> allowedZones;

    @Override
    public void validateMovement(StockMovement stockMovement, Zone targetZone) {
        //Rejects transfer to disallowed zone types
        //Cannot be stored in DISPATCH zone
    }
}

