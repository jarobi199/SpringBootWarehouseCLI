package io.warehouse.factory;


import io.warehouse.enums.ProductType;
import io.warehouse.enums.ZoneType;
import io.warehouse.model.BulkProduct;
import io.warehouse.model.FragileProduct;
import io.warehouse.model.PerishableProduct;
import io.warehouse.model.Product;

import java.time.LocalDate;
import java.util.List;

public class ProductFactory {

    public static PerishableProduct createPerishable(String sku, String name, String description, double unitPrice,
                                                     int quantity, int reorderThreshold, ProductType productType, String zoneId, LocalDate expiryDate) {
        return new PerishableProduct(sku, name, description, unitPrice, quantity, reorderThreshold, productType, zoneId, expiryDate);
    }

    public static Product createBulk(String sku, String name, String description, double unitPrice,
                                     int quantity, int reorderThreshold, ProductType productType, String zoneId, double weightPerUnit) {
        return new BulkProduct(sku, name, description, unitPrice, quantity, reorderThreshold, productType, zoneId, weightPerUnit);
    }

    public static Product createFragile(String sku, String name, String description, double unitPrice,
                                        int quantity, int reorderThreshold, ProductType productType, String zoneId, String instructions, List<ZoneType> allowedZones) {
        return new FragileProduct(sku, name, description, unitPrice, quantity, reorderThreshold, productType, zoneId, instructions,  allowedZones);
    }
}
