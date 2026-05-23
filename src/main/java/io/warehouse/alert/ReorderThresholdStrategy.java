package io.warehouse.alert;

import io.warehouse.model.Product;

public class ReorderThresholdStrategy implements ILowStockAlertable {

    private static final String MESSAGE = "Your stock is low! It is time to reorder.";

    @Override
    public String evaluate(Product product) {
      return (product.isLowStock() ? MESSAGE : null);
    }
}
