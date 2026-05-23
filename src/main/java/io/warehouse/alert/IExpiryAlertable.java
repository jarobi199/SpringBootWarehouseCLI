package io.warehouse.alert;

import io.warehouse.model.PerishableProduct;

public interface IExpiryAlertable {
    String evaluate(PerishableProduct perishableProduct);
}
