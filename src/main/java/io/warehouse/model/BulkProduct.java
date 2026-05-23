package io.warehouse.model;

public class BulkProduct extends Product {

    private double weightPerUnit;

    public double totalWeight() {
        return quantity * weightPerUnit;
    }

    @Override
    public void validateMovement(StockMovement stockMovement, Zone targetZone) {

    }

}
