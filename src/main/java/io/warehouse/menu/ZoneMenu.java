package io.warehouse.menu;

import io.warehouse.enums.ZoneType;
import io.warehouse.service.ZoneService;
import io.warehouse.util.InputHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ZoneMenu implements IMenu {

    @Autowired
    private ZoneService zoneService;

    @Override
    public void show() {
        int choice;
        do {
            printOptions();
            choice = InputHandler.getIntegerInput();
            switch (choice) {
                case 1 -> addZone();
            }
        }
        while (choice != 0);
    }

    public void addZone() {
        System.out.println("Enter zone name:");
        String name = InputHandler.getStringInput();
        System.out.println("Enter zone type (RECEIVING, STORAGE, DISPATCH, COLD_STORAGE, HAZMAT):");
        ZoneType zoneType = ZoneType.valueOf(InputHandler.getStringInput());
        System.out.println("Enter zone capacity:");
        int capacity = InputHandler.getIntegerInput();

        zoneService.addZone(name, zoneType, capacity);
        System.out.println("Zone added!");
    }

    @Override
    public void printOptions() {
        System.out.println("[1] Add zone");
        System.out.println("[2] List all zones");
        System.out.println("[3] View zone");
        System.out.println("[4] Delete zone");
        System.out.println("[0] Exit");
    }
}
