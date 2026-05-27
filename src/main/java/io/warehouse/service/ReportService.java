package io.warehouse.service;

import io.warehouse.model.PerishableProduct;
import io.warehouse.model.Product;
import io.warehouse.model.Zone;
import io.warehouse.repository.ProductRepository;
import io.warehouse.repository.ZoneRepository;
import io.warehouse.util.CommandLineTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ZoneRepository zoneRepository;

    public void generateReportSummary() {

        int totalProducts = productRepository.findAll().size();
        int totalUnits = productRepository.findAll().stream().mapToInt(Product::getQuantity).sum();
        double totalStockValue = productRepository.findAll().stream().mapToDouble(Product::calculateValue).sum();
        long lowStockAlerts = productRepository.findAll().stream().filter(Product::isLowStock).count();

        System.out.println("INVENTORY SUMMARY");
        CommandLineTable inventorySummaryTable = new CommandLineTable();
        inventorySummaryTable.setShowVerticalLines(true);
        inventorySummaryTable.setHeaders("TOTAL PRODUCTS", "TOTAL UNITS", "TOTAL STOCK VALUE","LOW STOCK ALERTS");
        inventorySummaryTable.addRow(String.valueOf(totalProducts), String.valueOf(totalUnits),  "$" + totalStockValue, String.valueOf(lowStockAlerts));
        inventorySummaryTable.print();
        System.out.println();

        System.out.println("ZONE UTILIZATION");
        CommandLineTable zoneUtilizationTable = new CommandLineTable();
        zoneUtilizationTable.setShowVerticalLines(true);
        zoneUtilizationTable.setHeaders("ZONE", "GRAPHICAL", "FRACTION","PERCENTAGE");
        zoneRepository.findAll().forEach(zone -> {
            int currentOccupancy = productRepository.findByZoneId(zone.getId()).stream().mapToInt(Product::getQuantity).sum();
            int occupancyPercentage = (currentOccupancy * 100) / zone.getCapacity();
            int filled = Math.round(occupancyPercentage * 10 / 100) ;
            String bar = "▓".repeat(filled) + "░".repeat(10 - filled);
            String fraction = currentOccupancy + "/" + zone.getCapacity();
            zoneUtilizationTable.addRow(zone.getDisplayName(), bar,  fraction, occupancyPercentage + "%");
        });
        zoneUtilizationTable.print();

    }

    public void generateExpiryReport() {
        List<PerishableProduct> expiredProducts = productRepository.findAll().stream()
                .filter(product -> (product instanceof PerishableProduct p) && (p.isNearingExpiry(7))).map(PerishableProduct.class::cast).toList();
        System.out.println("EXPIRY REPORT");
        CommandLineTable expiryReportTable = new CommandLineTable();
        expiryReportTable.setShowVerticalLines(true);
        expiryReportTable.setHeaders("SKU", "NAME", "ZONE","QUANTITY","EXPIRY DATE");
        expiredProducts.forEach(product -> {
            Zone zone = zoneRepository.findById(product.getZoneId()).orElse(null);
            expiryReportTable.addRow(product.getSku(), product.getName(), zone.getDisplayName(), String.valueOf(product.getQuantity()), product.getExpiryDate().toString());
        });
        expiryReportTable.print();
        System.out.println();
    }
}
