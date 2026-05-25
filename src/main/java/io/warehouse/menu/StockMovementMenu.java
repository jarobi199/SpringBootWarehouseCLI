package io.warehouse.menu;

import io.warehouse.enums.MovementType;
import io.warehouse.exception.EntityNotFoundException;
import io.warehouse.exception.MovementValidationException;
import io.warehouse.model.Zone;
import io.warehouse.service.StockMovementService;
import io.warehouse.service.ZoneService;
import io.warehouse.util.InputHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
    }

    public void transferZones() {
    }

    public void dispatchGoods() {
    }

    public void receiveGoods() {
        System.out.println("Enter the product SKU:");
        String sku = InputHandler.getStringInput();
        System.out.println("Select the zone to receive the product:");
        String zoneId = getZoneSelection();
        System.out.println("Enter the quantity of the product:");
        int quantity = InputHandler.getIntegerInput();
        System.out.println("Enter operator notes:");
        String operatorNotes = InputHandler.getStringInput();

        try
        {
            stockMovementService.processMovement(sku, null, zoneId, quantity, MovementType.RECEIVED, operatorNotes);
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

    private String getZoneSelection() {
        List<Zone> zones = zoneService.getAllZones();
        for(int i = 0; i < zones.size(); i++) {
            Zone zone = zones.get(i);
            System.out.println(i + 1 + ") " + zone.getName() + "(" + zone.getType().name() + ")");
        }
        int zoneIndex =  InputHandler.getIntegerInput() - 1;
        return zones.get(zoneIndex).getId();
    }
}
