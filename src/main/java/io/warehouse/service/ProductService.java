package io.warehouse.service;

import io.warehouse.alert.ExpiryWarningStrategy;
import io.warehouse.alert.ReorderThresholdStrategy;
import io.warehouse.enums.HazardClass;
import io.warehouse.enums.ProductType;
import io.warehouse.enums.ZoneType;
import io.warehouse.factory.ProductFactory;
import io.warehouse.model.*;
import io.warehouse.repository.ProductRepository;
import io.warehouse.repository.StockMovementRepository;
import io.warehouse.repository.ZoneRepository;
import io.warehouse.util.CommandLineTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private StockMovementRepository stockMovementRepository;
    @Autowired
    private ZoneRepository zoneRepository;

    public void createPerishable(String sku, String name, String description, double unitPrice, int quantity,
                                 int reorderThreshold, ProductType productType, String zoneId, LocalDate expiryDate) {
        Product perishableProduct = ProductFactory.createPerishable(sku, name, description, unitPrice, quantity, reorderThreshold, productType, zoneId, expiryDate);
        if(checkZoneCapacity(zoneId, quantity)) {
            productRepository.save(perishableProduct);
        }
        else
        {
            System.out.println("This product cannot be added! The zone does not have enough capacity.");
        }
    }

    public void createFragile(String sku, String name, String description, double unitPrice, int quantity, int reorderThreshold,
                              ProductType productType, String zoneId, String instructions, List<ZoneType> allowedZones) {
        Product fragileProduct = ProductFactory.createFragile(sku, name, description, unitPrice, quantity, reorderThreshold, productType, zoneId, instructions, allowedZones);
        if(checkZoneCapacity(zoneId, quantity)) {
            productRepository.save(fragileProduct);
        }
        else
        {
            System.out.println("This product cannot be added! The zone does not have enough capacity.");
        }
    }

    public void createHazardous(String sku, String name, String description, double unitPrice, int quantity, int reorderThreshold,
                              ProductType productType, String zoneId, boolean requiresVentilation, HazardClass hazardClass) {
        Product hazardousProduct = ProductFactory.createHazardous(sku, name, description, unitPrice, quantity, reorderThreshold, productType, zoneId, requiresVentilation, hazardClass);
        if(checkZoneCapacity(zoneId, quantity)) {
            productRepository.save(hazardousProduct);
        }
        else
        {
            System.out.println("This product cannot be added! The zone does not have enough capacity.");
        }
    }

    public void createStandard(String sku, String name, String description, double unitPrice, int quantity,
                               int reorderThreshold, ProductType productType, String zoneId) {
        Product standardProduct = ProductFactory.createStandard(sku, name, description, unitPrice, quantity, reorderThreshold, productType, zoneId);
        if(checkZoneCapacity(zoneId, quantity)) {
            productRepository.save(standardProduct);
        }
        else
        {
            System.out.println("This product cannot be added! The zone does not have enough capacity.");
        }
    }

    public void listProducts() {
        Zone zone;
        CommandLineTable table = new CommandLineTable();
        table.setShowVerticalLines(true);
        table.setHeaders("SKU", "NAME", "DESCRIPTION", "PRICE","QUANTITY","ZONE", "LOW STOCK");
        for(Product product : productRepository.findAll() ) {
            zone = zoneRepository.findById(product.getZoneId()).get();
            table.addRow(product.getSku(), product.getName(), product.getDescription(), "$" + product.getUnitPrice(),
                    String.valueOf(product.getQuantity()), zone.getName() + " (" + zone.getType().name() + ")", (product.isLowStock()) ? "⚠" : "");
        }
        table.print();
    }

    public void deleteProduct(String sku) {
        Product product = productRepository.findBySku(sku).getFirst();
        List<StockMovement> stockMovements = stockMovementRepository.findByProductId(product.getId());
        if(!stockMovements.isEmpty()) {
            System.out.println("This product cannot be deleted. There are stock movements");
        }
        else
        {
            productRepository.delete(product);
        }
    }

    public void listLowStockAlerts() {
        List<Product> lowStockProducts = productRepository.findAll().stream().filter(Product::isLowStock).toList();
        if(lowStockProducts.isEmpty()) {
            System.out.println("-There are no low stock alerts.");
        }
        else
        {
            CommandLineTable table = new CommandLineTable();
            table.setShowVerticalLines(true);
            table.setHeaders("SKU", "NAME", "QUANTITY","REORDER THRESHOLD" );
            for(Product product : lowStockProducts) {
                table.addRow(product.getSku(), product.getName(), String.valueOf(product.getQuantity()), String.valueOf(product.getReorderThreshold()));
            }
            table.print();
        }
        System.out.println();
    }

    public void displayProductDetails(String sku) {
        Product product = productRepository.findBySku(sku).getFirst();
        String requiresVentilation = "N/A";
        String hazardClass = "N/A";
        String handlingInstructions = "N/A";
        String allowedZones = "N/A";
        String expiryDate = "N/A";
        Zone zone = zoneRepository.findById(product.getZoneId()).get();
        String zoneDisplay = zone.getName() +  " (" + zone.getType().name() + ")";
        System.out.println("PRODUCT DETAILS");
        CommandLineTable table = new CommandLineTable();
        table.setShowVerticalLines(true);
        table.setHeaders("SKU", "NAME", "DESCRIPTION", "PRICE","QUANTITY","REORDER THRESHOLD","TYPE","ZONE","REQUIRES VENTILATION","HAZARD CLASS",
                "ALLOWED ZONES", "HANDLING INSTRUCTIONS", "EXPIRY DATE");
        switch (product) {
            case HazardousProduct hazardousProduct -> {
                requiresVentilation = String.valueOf(hazardousProduct.isRequiresVentilation());
                hazardClass = hazardousProduct.getHazardClass().name();
            }
            case FragileProduct fragileProduct -> {
                handlingInstructions = fragileProduct.getHandlingInstructions();
                allowedZones = fragileProduct.getAllowedZones().stream()
                            .map(ZoneType::name)
                            .collect(Collectors.joining(", "));
            }
            case PerishableProduct perishableProduct -> expiryDate = perishableProduct.getExpiryDate().toString();
            default -> {
            }
        }
        table.addRow(product.getSku(), product.getName(), product.getDescription(), "$" + product.getUnitPrice(),
                String.valueOf(product.getQuantity()), String.valueOf(product.getReorderThreshold()), product.getType().name(),
                zoneDisplay, requiresVentilation,  hazardClass, allowedZones, handlingInstructions, expiryDate);
        table.print();

        System.out.println();
        System.out.println("MOVEMENT SUMMARY");
        List<StockMovement> stockMovements = stockMovementRepository.findTop3ByProductIdOrderByTimestampDesc(product.getId());
        if(stockMovements.isEmpty()) {
            System.out.println("-The are no stock movements for this product.");
        }
        else
        {
            CommandLineTable stockMovementSummary = new CommandLineTable();
            stockMovementSummary.setShowVerticalLines(true);
            stockMovementSummary.setHeaders("SKU", "FROM ZONE","TO ZONE","MOVEMENT TYPE","TIMESTAMP" );
            stockMovements.forEach(stockMovement -> {
                Zone fromZone = zoneRepository.findById(zone.getId()).get();
                Zone toZone = zoneRepository.findById(product.getZoneId()).get();
                stockMovementSummary.addRow(product.getSku(),fromZone.getName(), toZone.getName(), stockMovement.movementType().name(), stockMovement.timestamp().toString());
            });
            stockMovementSummary.print();
        }

        System.out.println();
        ReorderThresholdStrategy reorderThresholdStrategy = new ReorderThresholdStrategy();
        ExpiryWarningStrategy expiryWarningStrategy = new ExpiryWarningStrategy(5);
        String lowStockMessage =  reorderThresholdStrategy.evaluate(product);
        String expiryMessage = null;
        if(product instanceof PerishableProduct perishableProduct) {
            expiryMessage= expiryWarningStrategy.evaluate(perishableProduct);
        }

        System.out.println("ACTIVE ALERTS");
        if(expiryMessage != null) {
            System.out.println("-" + expiryMessage);
        }
        if(lowStockMessage != null) {
            System.out.println("-" + lowStockMessage);
        }
        System.out.println();
    }

    public boolean checkZoneCapacity(String zoneId, int quantity) {
        boolean hasCapacity = false;
        Zone zone = zoneRepository.findById(zoneId).orElse(null);
        if (zone != null) {
            int currentOccupancy = productRepository.findByZoneId(zoneId)
                    .stream()
                    .mapToInt(Product::getQuantity)
                    .sum();
            hasCapacity = (currentOccupancy + quantity) <= zone.getCapacity();
        }

        return hasCapacity;
    }

}
