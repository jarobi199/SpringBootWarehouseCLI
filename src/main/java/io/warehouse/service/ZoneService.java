package io.warehouse.service;

import io.warehouse.enums.ZoneType;
import io.warehouse.model.Product;
import io.warehouse.model.Zone;
import io.warehouse.repository.ProductRepository;
import io.warehouse.repository.ZoneRepository;
import io.warehouse.util.CommandLineTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ZoneService {

    @Autowired
    private ZoneRepository zoneRepository;
    @Autowired
    private ProductRepository productRepository;

    public boolean hasAvailableZones() {
        return zoneRepository.count() > 0;
    }

    public void addZone(String name, ZoneType zoneType, int capacity) {
        Zone zone = new Zone(name, zoneType, capacity);
        zoneRepository.save(zone);
    }

    public List<Zone> getAllZones() {
        return zoneRepository.findAll();
    }

    public List<Zone> getZonesByZoneType(ZoneType zoneType) {
        return zoneRepository.findByType(zoneType);
    }

    public void listZones() {
        List<Zone> zones = zoneRepository.findAll();
        CommandLineTable table = new CommandLineTable();
        table.setShowVerticalLines(true);//if false (default) then no vertical lines are shown
        table.setHeaders("NAME", "TYPE", "CAPACITY","CURRENT OCCUPANCY","OCCUPANCY PERCENTAGE");//
        for (Zone zone : zones) {
            table.addRow(zone.getName(), zone.getType().name(), String.valueOf(zone.getCapacity()), String.valueOf(zone.getCurrentOccupancy()), zone.getOccupancyPercentage() + "%");
        }
        table.print();
    }

    public void deleteZone(String id) {
        Optional<Zone> optionalZone = zoneRepository.findById(id);
        if (optionalZone.isPresent() && optionalZone.get().getCurrentOccupancy() == 0) {
            zoneRepository.delete(optionalZone.get());
            System.out.println("Zone deleted successfully!");
        }
        else  {
            System.out.println("This zone cannot be deleted. It currently has products.");
        }
    }

    public void viewZone(String zoneId) {
        Zone zone = zoneRepository.findById(zoneId).orElse(null);
        if(zone != null) {
            System.out.println("ZONE: " + zone.getName() + " (" + zone.getType().name() + ")");
        }
        List<Product> products = productRepository.findByZoneId(zoneId);
        CommandLineTable table = new CommandLineTable();
        table.setShowVerticalLines(true);
        table.setHeaders("SKU", "NAME", "QUANTITY", "UNIT PRICE","TOTAL VALUE");
        products.forEach(product -> {
            table.addRow(product.getSku(), product.getName(), String.valueOf(product.getQuantity()),  String.valueOf(product.getUnitPrice()), "$" + product.calculateValue());
        });
        table.print();
        System.out.println("ZONE TOTAL VALUE: $" + calculateZoneValue(zoneId));
        System.out.println();
    }

    public double calculateZoneValue(String zoneId) {
        return productRepository.findByZoneId(zoneId)
                .stream()
                .mapToDouble(Product::calculateValue)
                .sum();
    }
}
