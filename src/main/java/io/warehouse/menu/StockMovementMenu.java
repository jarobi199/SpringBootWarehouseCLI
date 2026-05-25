package io.warehouse.menu;

import io.warehouse.exception.EntityNotFoundException;
import io.warehouse.exception.MovementValidationException;
import io.warehouse.service.StockMovementService;
import io.warehouse.service.ZoneService;
import io.warehouse.util.InputHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StockMovementMenu implements IMenu{
    @Autowired
    private StockMovementService stockMovementService;
    @Autowired
    private ZoneService zoneService;

    @Override
    public void show() {
            int choice;
            do {
                printOptions();
                choice = InputHandler.getIntegerInput();
                switch (choice) {
                    case 1 -> receiveGoods();
                    case 2 -> dispatchGoods();
                    case 3 -> transferZones();
                    case 4 -> postAdjustment();
                    case 5-> movementHistory();
                }
            }
            while (choice != 0);
    }

    public void movementHistory() {

    }

    public void postAdjustment() {
        System.out.println("Enter the product SKU:");
        String sku = InputHandler.getStringInput();
        System.out.println("Enter operator notes:");
        String operatorNotes = InputHandler.getStringInput();
        System.out.println("Enter the quantity of the product:");
        int quantity = InputHandler.getIntegerInput();

        try
        {
            stockMovementService.postAdjustment(sku, quantity, operatorNotes);
            System.out.println("Adjustment posted successfully!");
        }
        catch (MovementValidationException | EntityNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void transferZones() {
        System.out.println("Enter the product SKU:");
        String sku = InputHandler.getStringInput();
        System.out.println("Enter the zone to transfer from:");
        String fromZoneId = ZoneMenu.getZoneSelection(zoneService.getAllZones());
        System.out.println("Enter the zone to transfer to:");
        String toZoneId = ZoneMenu.getZoneSelection(zoneService.getAllZones());
        System.out.println("Enter operator notes:");
        String operatorNotes = InputHandler.getStringInput();

        try
        {
            stockMovementService.transferGoods(sku, fromZoneId, toZoneId, operatorNotes);
            System.out.println("Goods transferred successfully!");
        }
        catch (MovementValidationException | EntityNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void dispatchGoods() {
        System.out.println("Enter the product SKU:");
        String sku = InputHandler.getStringInput();
        System.out.println("Enter the quantity of the product:");
        int quantity = InputHandler.getIntegerInput();
        System.out.println("Enter operator notes:");
        String operatorNotes = InputHandler.getStringInput();

        try
        {
            stockMovementService.dispatchGoods(sku, quantity, operatorNotes);
            System.out.println("Goods dispatched successfully!");
        }
        catch (MovementValidationException | EntityNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void receiveGoods() {
        System.out.println("Enter the product SKU:");
        String sku = InputHandler.getStringInput();
        System.out.println("Enter the quantity of the product:");
        int quantity = InputHandler.getIntegerInput();
        System.out.println("Enter operator notes:");
        String operatorNotes = InputHandler.getStringInput();

        try
        {
            stockMovementService.receiveGoods(sku, quantity, operatorNotes);
            System.out.println("Goods received successfully!");
        }
        catch (MovementValidationException | EntityNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    @Override
    public void printOptions() {
        System.out.println("[1] Receive goods");
        System.out.println("[2] Dispatch goods");
        System.out.println("[3] Transfer between zones");
        System.out.println("[4] Post adjustment");
        System.out.println("[5] Movement history");
        System.out.println("[0] Back");
    }

}
