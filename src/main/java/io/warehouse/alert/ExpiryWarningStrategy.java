package io.warehouse.alert;

import io.warehouse.model.PerishableProduct;

public class ExpiryWarningStrategy implements IExpiryAlertable{

    private static final String MESSAGE = "This product will soon expire!";
    private final int warning_window;

    public ExpiryWarningStrategy(int warning_window) {
        this.warning_window = warning_window;
    }

    @Override
    public String evaluate(PerishableProduct perishableProduct) {
        return (perishableProduct.daysUntilExpiry() <= warning_window) ? MESSAGE : null;
    }

}
