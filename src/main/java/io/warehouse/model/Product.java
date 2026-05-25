package io.warehouse.model;

import io.warehouse.enums.MovementType;
import io.warehouse.enums.ProductType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public abstract class Product {
    @Id
    protected String id;
    protected String sku;
    protected String name;
    protected String description;
    protected double unitPrice;
    protected int quantity;
    protected int reorderThreshold;
    protected ProductType type;
    protected String zoneId;

    public Product() {
        //No argument constructor
    }

    public Product(String sku, String name, String description, double unitPrice, int quantity, int reorderThreshold, ProductType type, String zoneId) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.reorderThreshold = reorderThreshold;
        this.type = type;
        this.zoneId = zoneId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getReorderThreshold() {
        return reorderThreshold;
    }

    public void setReorderThreshold(int reorderThreshold) {
        this.reorderThreshold = reorderThreshold;
    }

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public boolean isLowStock() {
        return quantity < reorderThreshold;
    }

    public double calculateValue() {
        return quantity * unitPrice;
    }

    public void adjustQuantity(int quantity, MovementType type) {
        switch (type) {
            case RECEIVED, ADJUSTMENT -> this.quantity += quantity;
            case DISPATCHED  -> this.quantity -= quantity;
            case TRANSFERRED -> {} // quantity unchanged, zoneId updated separately
        }
    }

    public abstract void validateMovement(StockMovement stockMovement, Zone targetZone);

}
