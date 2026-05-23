package io.warehouse.service;

import io.warehouse.enums.ProductType;
import io.warehouse.enums.ZoneType;
import io.warehouse.factory.ProductFactory;
import io.warehouse.model.Product;
import io.warehouse.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

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
}
