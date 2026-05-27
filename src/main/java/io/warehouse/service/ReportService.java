package io.warehouse.service;

import io.warehouse.model.PerishableProduct;
import io.warehouse.model.Product;
import io.warehouse.model.StockMovement;
import io.warehouse.model.Zone;
import io.warehouse.repository.ProductRepository;
import io.warehouse.repository.StockMovementRepository;
import io.warehouse.repository.ZoneRepository;
import io.warehouse.util.CommandLineTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ZoneRepository zoneRepository;
    @Autowired
    private StockMovementRepository stockMovementRepository;

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
            int filled = Math.round(occupancyPercentage / 10.0f);
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

    public void getMovementHistoryReport(LocalDateTime from, LocalDateTime to) {
        List<StockMovement> stockMovements = stockMovementRepository.
                findByTimestampBetweenOrderByTimestampDesc(from, to);

        if (stockMovements.isEmpty()) {
            System.out.println("No movements found for the selected date range.");
            return;
        }

        CommandLineTable table = new CommandLineTable();
        table.setShowVerticalLines(true);
        table.setHeaders("TIMESTAMP", "SKU", "DESCRIPTION", "TYPE", "QTY", "FROM ZONE", "TO ZONE");
        stockMovements.forEach(movement -> {
            String sku = "N/A";
            String name = "N/A";
            Product product = productRepository.findById(movement.productId()).orElse(null);
            if (product != null) {
                sku  = product.getSku();
                name = product.getName();
            }

             String fromZone = zoneRepository.findById(movement.fromZoneId())
                     .map(Zone::getDisplayName)
                     .orElse("N/A");
             String toZone =  zoneRepository.findById(movement.toZoneId())
                     .map(Zone::getDisplayName)
                     .orElse("N/A");
            table.addRow(
                    movement.timestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    sku,
                    name,
                    movement.movementType().toString(),
                    String.valueOf(movement.quantity()),
                    fromZone,
                    toZone
            );
        });
        table.print();
    }

    private List<ZoneStockValue> getStockValueByZone() {
        return zoneRepository.findAll().stream()
                .map(zone -> {
                    List<Product> products = productRepository.findByZoneId(zone.getId());
                    double totalValue = products.stream()
                            .mapToDouble(Product::calculateValue)
                            .sum();
                    int occupancy = products.stream()
                            .mapToInt(Product::getQuantity)
                            .sum();
                    int occupancyPercent = zone.getCapacity() > 0
                            ? (occupancy * 100) / zone.getCapacity()
                            : 0;
                    return new ZoneStockValue(
                            zone.getDisplayName(),
                            products.size(),
                            totalValue,
                            occupancyPercent
                    );
                })
                .sorted(Comparator.comparingDouble(ZoneStockValue::totalValue).reversed())
                .toList();
    }

    public void generateStockValueByZoneReport() {
        List<ZoneStockValue> results = getStockValueByZone();

        if (results.isEmpty()) {
            System.out.println("No zones found.");
        }
        else {
            CommandLineTable table = new CommandLineTable();
            table.setShowVerticalLines(true);
            table.setHeaders("RANK", "ZONE", "PRODUCTS", "TOTAL VALUE", "OCCUPANCY PERCENTAGE", "ALERT");
            int rank = 1;
            for (ZoneStockValue zsv : results) {
                String alert = zsv.occupancyPercent() >= 80 ? "This zone is over 80% capacity! ⚠" : "N/A";
                table.addRow(
                        String.valueOf(rank++),
                        zsv.zoneName(),
                        String.valueOf(zsv.productCount()),
                        String.format("$%.2f", zsv.totalValue()),
                        zsv.occupancyPercent() + "%",
                        alert
                );
            }
            table.print();
        }
    }
    private record ZoneStockValue(String zoneName, int productCount, double totalValue, int occupancyPercent) {}
}
