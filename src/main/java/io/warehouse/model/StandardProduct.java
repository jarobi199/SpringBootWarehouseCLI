package io.warehouse.model;

public class StandardProduct extends Product{

    @Override
    public void validateMovement(StockMovement stockMovement, Zone targetZone) {
        //No movement restrictions
    }

    @Override
    public double calculateValue() {
        return quantity * unitPrice;
    }
}
