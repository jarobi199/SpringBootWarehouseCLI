package io.warehouse.menu;

import io.warehouse.enums.ProductType;
import io.warehouse.enums.ZoneType;
import io.warehouse.model.Zone;
import io.warehouse.service.ProductService;
import io.warehouse.service.ZoneService;
import io.warehouse.util.InputHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;


@Component
public class ProductMenu implements IMenu{

    @Autowired
    private ProductService productService;
    @Autowired
    private ZoneService zoneService;

    @Override
    public void show() {
        int choice;
        do {
            printOptions();
            choice = InputHandler.getIntegerInput();
            switch (choice) {
                case 1 -> addProduct();
                case 2 -> listProducts();
                case 3 -> viewProductDetail();
                case 4 -> deleteProduct();
                case 5 -> lowStockAlerts();
            }
        }
        while (choice != 0);
    }

    public void viewProductDetail() {
        System.out.println("Enter the SKU of the product that you want to view:");
        String sku = InputHandler.getStringInput();
        productService.displayProductDetails(sku);
    }

    public void lowStockAlerts() {
        productService.listLowStockAlerts();
    }

    public void deleteProduct() {
        System.out.println("Enter the SKU of the product you want to delete:");
        String sku = InputHandler.getStringInput();
        productService.deleteProduct(sku);
    }

    public void listProducts() {
       productService.listProducts();
    }

    public void addProduct() {

        if(zoneService.hasAvailableZones()) {
            System.out.println("Enter the product type (STANDARD, PERISHABLE, BULK, FRAGILE):");
            ProductType productType = ProductType.valueOf(InputHandler.getStringInput());
            System.out.println("Enter the sku:");
            String sku = InputHandler.getStringInput();
            System.out.println("Enter the name:");
            String name = InputHandler.getStringInput();
            System.out.println("Enter the description:");
            String description = InputHandler.getStringInput();
            System.out.println("Enter the unit price:");
            double unitPrice = InputHandler.getDoubleInput();
            System.out.println("Enter the reorder threshold:");
            int reorderThreshold = InputHandler.getIntegerInput();
            System.out.println("Enter the quantity:");
            int quantity = InputHandler.getIntegerInput();
            System.out.println("Select the zone:");
            String zoneId = getZoneSelection();

            if(ProductType.PERISHABLE.equals(productType)) {
                System.out.println("Enter the expiration date (yyyy-MM-dd):");
                LocalDate expiryDate = InputHandler.getDateInput();

                productService.createPerishable(sku, name, description, unitPrice, quantity, reorderThreshold, productType, zoneId, expiryDate);
            }
            else if(ProductType.BULK.equals(productType)) {
                System.out.println("Enter the weight per unit:");
                double weightPerUnit = InputHandler.getDoubleInput();

                productService.createBulk(sku, name, description, unitPrice, quantity, reorderThreshold, productType, zoneId, weightPerUnit);
            }
            else if(ProductType.FRAGILE.equals(productType)) {
                System.out.println("Enter the handling instructions:");
                String instructions = InputHandler.getStringInput();
                System.out.println("Enter the list of allowed zones, separated by a comma (ie. STORAGE, DISPATCH):");
                List<ZoneType> allowedZones =  Stream.of(InputHandler.getStringInput().split(",")).map(ZoneType::valueOf).toList();

                productService.createFragile(sku, name, description, unitPrice, quantity, reorderThreshold, productType, zoneId, instructions, allowedZones);
            }
        }
        else {
            System.out.println("No available zones! You need to create a zone in order to add a product. \n");
        }
        System.out.println("Product added!");
    }

    private String getZoneSelection() {
        List<Zone> zones = zoneService.getAllZones();
        for(int i = 0; i < zones.size(); i++) {
            Zone zone = zones.get(i);
            System.out.println(i + 1 + ") " + zone.getName() + "(" + zone.getType().name() + ")");
        }
        int zoneIndex =  InputHandler.getIntegerInput() - 1;
        return zones.get(zoneIndex).getId();
    }

    @Override
    public void printOptions() {
        System.out.println("[1] Add product");
        System.out.println("[2] List all products");
        System.out.println("[3] View product detail");
        System.out.println("[4] Delete product");
        System.out.println("[5] Low stock alerts");
        System.out.println("[0] Back");

    }
}
