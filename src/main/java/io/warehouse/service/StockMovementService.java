package io.warehouse.service;

import io.warehouse.alert.ReorderThresholdStrategy;
import io.warehouse.enums.MovementType;
import io.warehouse.exception.EntityNotFoundException;
import io.warehouse.exception.MovementValidationException;
import io.warehouse.model.Product;
import io.warehouse.model.StockMovement;
import io.warehouse.model.Zone;
import io.warehouse.repository.ProductRepository;
import io.warehouse.repository.StockMovementRepository;
import io.warehouse.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StockMovementService {
    @Autowired
    private StockMovementRepository stockMovementRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ZoneRepository zoneRepository;
    private final ReorderThresholdStrategy alertStrategy = new ReorderThresholdStrategy();

    public void processMovement(String sku, String fromZoneId, String toZoneId,  int quantity, MovementType movementType,String operatorNotes) {
        // 1. Load product and zone
        Zone target = zoneRepository.findById(toZoneId)
                .orElseThrow(() -> new EntityNotFoundException("Zone not found"));
        Product product = productRepository.findBySku(sku).stream()
                .findFirst().orElseThrow(() -> new EntityNotFoundException("Product not found: " + sku));

        // 2. Create StockMovement object
        StockMovement stockMovement = new StockMovement(null, product.getId(), fromZoneId, toZoneId, quantity, movementType, LocalDateTime.now(), operatorNotes);

        // 2. Check zone capacity
        if (!target.hasCapacity(stockMovement.quantity())) {
            throw new MovementValidationException("Zone at capacity");
        }

        // 3. Subtype enforces its own rules
        product.validateMovement(stockMovement, target);

        // 4. Update quantity on hand
        product.adjustQuantity(stockMovement.quantity(), stockMovement.movementType());
        productRepository.save(product);

        // 5. Persist the immutable movement record
        StockMovement saved = stockMovementRepository.save(stockMovement);

        // 6. Check low-stock alert after any outbound movement
        MovementType type = stockMovement.movementType();
        if (type == MovementType.DISPATCHED || type == MovementType.ADJUSTMENT) {
            System.out.println(alertStrategy.evaluate(product));
        }
    }

}