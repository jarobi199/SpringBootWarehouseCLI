package io.warehouse.service;

import io.warehouse.alert.ReorderThresholdStrategy;
import io.warehouse.enums.MovementType;
import io.warehouse.enums.ZoneType;
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

    public void receiveGoods(String sku, int quantity, String operatorNotes) {
        Product product = productRepository.findBySku(sku).stream()
                .findFirst().orElseThrow(() -> new EntityNotFoundException("Product not found: " + sku));

        StockMovement stockMovement = new StockMovement(null, product.getId(), null, product.getZoneId(), quantity, MovementType.RECEIVED, LocalDateTime.now(), operatorNotes);
        processMovement(product, stockMovement);
    }

    public void dispatchGoods(String sku, int quantity, String operatorNotes) {
        Product product = productRepository.findBySku(sku).stream()
                .findFirst().orElseThrow(() -> new EntityNotFoundException("Product not found: " + sku));

        StockMovement stockMovement = new StockMovement(null, product.getId(), product.getZoneId(), null, quantity, MovementType.DISPATCHED, LocalDateTime.now(), operatorNotes);
        processMovement(product, stockMovement);
    }

    public void transferGoods(String sku, String fromZoneId, String toZoneId, String operatorNotes) {
        Product product = productRepository.findBySku(sku).stream()
                .findFirst().orElseThrow(() -> new EntityNotFoundException("Product not found: " + sku));

        StockMovement stockMovement = new StockMovement(null, product.getId(), fromZoneId, toZoneId, product.getQuantity(), MovementType.TRANSFERRED, LocalDateTime.now(), operatorNotes);
        processMovement(product, stockMovement);
    }
    public void postAdjustment(String sku, int quantity, String operatorNotes) {
        Product product = productRepository.findBySku(sku).stream()
                .findFirst().orElseThrow(() -> new EntityNotFoundException("Product not found: " + sku));

        StockMovement stockMovement = new StockMovement(null, product.getId(), product.getId(), product.getId(), quantity, MovementType.ADJUSTMENT, LocalDateTime.now(), operatorNotes);
        processMovement(product, stockMovement);
    }
    private void processMovement(Product product, StockMovement stockMovement) {
        //1. Get zone
        Zone target = zoneRepository.findById(product.getZoneId())
                .orElseThrow(() -> new EntityNotFoundException("Zone not found"));

        //2. Validation
        if((MovementType.RECEIVED.equals(stockMovement.movementType())) && (!ZoneType.RECEIVING.equals(target.getType()))) {
            throw new MovementValidationException("Zone is not receiving.");
        }
        else if((MovementType.DISPATCHED.equals(stockMovement.movementType())) && (!ZoneType.DISPATCH.equals(target.getType()))) {
            throw new MovementValidationException("Zone is not dispatching.");
        }
        else if ((MovementType.ADJUSTMENT.equals(stockMovement.movementType())) && (stockMovement.operatorNotes() == null) || (stockMovement.operatorNotes().isEmpty())) {
            throw new MovementValidationException("Operator notes cannot be null for an adjustment.");
        }

        //3. Check zone capacity
        if (!target.hasCapacity(stockMovement.quantity())) {
            throw new MovementValidationException("Zone at capacity");
        }

        // 4. Subtype enforces its own rules
        product.validateMovement(stockMovement, target);

        // 5. Update quantity on hand
        product.adjustQuantity(stockMovement.quantity(), stockMovement.movementType());
        productRepository.save(product);

        // 6. Persist the immutable movement record
        StockMovement saved = stockMovementRepository.save(stockMovement);

        // 7. Check low-stock alert after any outbound movement
        MovementType type = stockMovement.movementType();
        if (type == MovementType.DISPATCHED || type == MovementType.ADJUSTMENT) {
            System.out.println(alertStrategy.evaluate(product));
        }
        else if (type == MovementType.TRANSFERRED) {
            product.setZoneId(stockMovement.toZoneId());
            productRepository.save(product);
        }
    }

}