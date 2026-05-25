package io.warehouse.menu;

import io.warehouse.enums.ZoneType;
import io.warehouse.model.Zone;
import io.warehouse.service.ZoneService;
import io.warehouse.util.InputHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
                case 2 -> listZones();
                case 3 -> viewZone();
                case 4 -> deleteZone();
            }
        }
        while (choice != 0);
    }

    public void viewZone() {
        System.out.println("Enter the zone ID:");
        String zoneID = InputHandler.getStringInput();
        zoneService.viewZone(zoneID);
    }

    public void deleteZone() {
        System.out.println("Enter the ID of the zone to delete:");
        String id = InputHandler.getStringInput();
        zoneService.deleteZone(id);
    }

    public void listZones() {
        zoneService.listZones();
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

    public static String getZoneSelection(List<Zone> zones) {
        for(int i = 0; i < zones.size(); i++) {
            Zone zone = zones.get(i);
            System.out.println(i + 1 + ") " + zone.getName() + "(" + zone.getType().name() + ")");
        }
        int zoneIndex =  InputHandler.getIntegerInput() - 1;
        return zones.get(zoneIndex).getId();
    }
}
