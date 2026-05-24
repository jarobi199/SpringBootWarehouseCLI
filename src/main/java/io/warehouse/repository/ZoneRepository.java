package io.warehouse.repository;

import io.warehouse.enums.ZoneType;
import io.warehouse.model.Zone;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZoneRepository extends MongoRepository<Zone, String> {
    List<Zone> findByType(ZoneType zoneType);
}
