package io.warehouse.factory;


import io.warehouse.enums.HazardClass;
import io.warehouse.enums.ProductType;
import io.warehouse.enums.ZoneType;
import io.warehouse.model.*;

import java.time.LocalDate;
import java.util.List;

public class ProductFactory {

    public static PerishableProduct createPerishable(String sku, String name, String description, double unitPrice,
                                                     int quantity, int reorderThreshold, ProductType productType, String zoneId, LocalDate expiryDate) {
        return new PerishableProduct(sku, name, description, unitPrice, quantity, reorderThreshold, productType, zoneId, expiryDate);
    }

    public static Product createFragile(String sku, String name, String description, double unitPrice,
                                        int quantity, int reorderThreshold, ProductType productType, String zoneId, String instructions, List<ZoneType> allowedZones) {
        return new FragileProduct(sku, name, description, unitPrice, quantity, reorderThreshold, productType, zoneId, instructions,  allowedZones);
    }

    public static HazardousProduct createHazardous(String sku, String name, String description, double unitPrice,
                                                 int quantity, int reorderThreshold, ProductType productType, String zoneId, boolean requiresVentilation, HazardClass hazardClass) {
        return new HazardousProduct(sku, name, description, unitPrice, quantity, reorderThreshold, productType, zoneId, requiresVentilation, hazardClass);
    }

    public static StandardProduct createStandard(String sku, String name, String description, double unitPrice,
                                                 int quantity, int reorderThreshold, ProductType productType, String zoneId) {
        return new StandardProduct(sku, name, description, unitPrice, quantity, reorderThreshold, productType, zoneId);
    }
}
