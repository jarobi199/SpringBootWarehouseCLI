package io.warehouse.service;

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

    public void createPerishable(String sku, String name, String description, double unitPrice, int quantity,
                                 int reorderThreshold, ProductType productType, String zoneId, LocalDate expiryDate) {
        Product perishableProduct = ProductFactory.createPerishable(sku, name, description, unitPrice, quantity, reorderThreshold, productType, zoneId, expiryDate);
        productRepository.save(perishableProduct);
    }

    public void createBulk(String sku, String name, String description, double unitPrice, int quantity, int reorderThreshold,
                           ProductType productType, String zoneId, double weightPerUnit) {
        Product bulkProduct = ProductFactory.createBulk(sku, name, description, unitPrice, quantity, reorderThreshold, productType, zoneId, weightPerUnit);
        productRepository.save(bulkProduct);
    }

    public void createFragile(String sku, String name, String description, double unitPrice, int quantity, int reorderThreshold,
                              ProductType productType, String zoneId, String instructions, List<ZoneType> allowedZones) {
        Product fragileProduct = ProductFactory.createFragile(sku, name, description, unitPrice, quantity, reorderThreshold, productType, zoneId, instructions, allowedZones);
        productRepository.save(fragileProduct);
    }

    public void listProducts() {
        String totalWeight = "N/A";
        String handlingInstructions = "N/A";
        String allowedZones = "N/A";
        String expiryDate = "N/A";
        CommandLineTable table = new CommandLineTable();
        table.setShowVerticalLines(true);
        table.setHeaders("ID", "SKU", "NAME", "DESCRIPTION", "PRICE","QUANTITY","REORDER THRESHOLD","TYPE","ZONE ID","WEIGHT PER UNIT");
        for(Product product : productRepository.findAll() ) {
            switch (product) {
                case BulkProduct bulkProduct -> totalWeight = String.valueOf(bulkProduct.totalWeight());
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
            table.addRow(product.getId(), product.getSku(), product.getName(), product.getDescription(), "$" + product.getUnitPrice(),
            String.valueOf(product.getQuantity()), String.valueOf(product.getReorderThreshold()), product.getType().name(), product.getZoneId(),totalWeight, allowedZones, expiryDate);
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
        CommandLineTable table = new CommandLineTable();
        table.setShowVerticalLines(true);
        table.setHeaders("SKU", "NAME", "QUANTITY","REORDER THRESHOLD" );
        for(Product product : lowStockProducts) {
            table.addRow(product.getSku(), product.getName(), String.valueOf(product.getQuantity()), String.valueOf(product.getReorderThreshold()));
        }
        table.print();
    }
}
