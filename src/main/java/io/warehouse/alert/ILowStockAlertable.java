package io.warehouse.alert;

import io.warehouse.model.Product;

public interface ILowStockAlertable {
    String evaluate(Product p);
}
