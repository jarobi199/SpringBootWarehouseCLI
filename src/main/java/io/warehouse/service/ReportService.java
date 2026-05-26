package io.warehouse.service;

import io.warehouse.model.Product;
import io.warehouse.repository.ProductRepository;
import io.warehouse.repository.ZoneRepository;
import io.warehouse.util.CommandLineTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            int occupancyPercentage = zone.getOccupancyPercentage();
            int filled = (int) (occupancyPercentage / 10);
            String bar = "▓".repeat(filled) + "░".repeat(10 - filled);
            String fraction = zone.getCurrentOccupancy() + "/" + zone.getCapacity();
            zoneUtilizationTable.addRow(zone.getDisplayName(), bar,  fraction, occupancyPercentage + "%");
        });
        zoneUtilizationTable.print();

    }
}
