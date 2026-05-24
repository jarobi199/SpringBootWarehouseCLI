package io.warehouse.service;

import io.warehouse.enums.ZoneType;
import io.warehouse.model.Zone;
import io.warehouse.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZoneService {

    @Autowired
    private ZoneRepository zoneRepository;

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
}
