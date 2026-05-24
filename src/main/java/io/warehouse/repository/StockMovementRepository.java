package io.warehouse.repository;

import io.warehouse.model.StockMovement;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StockMovementRepository extends MongoRepository<StockMovement, String> {
    List<StockMovement> findByProductId(String productId);
}
