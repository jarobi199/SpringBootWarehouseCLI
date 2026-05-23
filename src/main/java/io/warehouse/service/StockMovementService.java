package io.warehouse.service;

import io.warehouse.model.StockMovement;

public class StockMovementService {

    /*public StockMovement processMovement(StockMovement movement) {
        // 1. Load product and zone(s)
        Product product = productRepo.findById(movement.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        Zone target = zoneRepo.findById(movement.getToZoneId())
                .orElseThrow(() -> new EntityNotFoundException("Zone not found"));

        // 2. Check zone capacity
        if (!target.hasCapacity(movement.getQuantity())) {
            throw new MovementValidationException("Zone at capacity");
        }

        // 3. Subtype enforces its own rules
        product.validateMovement(movement, target);

        // 4. Update quantity on hand
        product.adjustQuantity(movement.getQuantity(), movement.getType());
        productRepo.save(product);

        // 5. Persist the immutable movement record
        StockMovement saved = movementRepo.save(movement);

        // 6. Check low-stock alert after any outbound movement
        MovementType type = movement.getType();
        if (type == MovementType.DISPATCHED || type == MovementType.ADJUSTMENT) {
            alertStrategy.evaluate(product, product.getQuantityOnHand());
        }

        return saved;
    }*/
}