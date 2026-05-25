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

    public void receiveGoods(String sku, String toZoneId, int quantity, String operatorNotes) {
        Product product = productRepository.findBySku(sku).stream().findFirst().orElseThrow(() -> new EntityNotFoundException("Product not found: " + sku));
        StockMovement stockMovement = new StockMovement(null, product.getId(), null, toZoneId, quantity, MovementType.RECEIVED, LocalDateTime.now(), operatorNotes);
        processMovement(product, stockMovement);
    }

    private void processMovement(Product product, StockMovement movement) {
        // 1. Load zone
        Zone target = zoneRepository.findById(movement.toZoneId())
                .orElseThrow(() -> new EntityNotFoundException("Zone not found"));

        // 2. Check zone capacity
        if (!target.hasCapacity(movement.quantity())) {
            throw new MovementValidationException("Zone at capacity");
        }

        // 3. Subtype enforces its own rules
        product.validateMovement(movement, target);

        // 4. Update quantity on hand
        product.adjustQuantity(movement.quantity(), movement.movementType());
        productRepository.save(product);

        // 5. Persist the immutable movement record
        StockMovement saved = stockMovementRepository.save(movement);

        // 6. Check low-stock alert after any outbound movement
        MovementType type = movement.movementType();
        if (type == MovementType.DISPATCHED || type == MovementType.ADJUSTMENT) {
            System.out.println(alertStrategy.evaluate(product));
        }
    }

}