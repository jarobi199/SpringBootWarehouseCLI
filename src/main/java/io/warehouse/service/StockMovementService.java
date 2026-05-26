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
import io.warehouse.util.CommandLineTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    public void transferGoods(String sku, String toZoneId, String operatorNotes) {
        Product product = productRepository.findBySku(sku).stream()
                .findFirst().orElseThrow(() -> new EntityNotFoundException("Product not found: " + sku));

        StockMovement stockMovement = new StockMovement(null, product.getId(), product.getZoneId(), toZoneId, product.getQuantity(), MovementType.TRANSFERRED, LocalDateTime.now(), operatorNotes);
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

    public void displayMovementHistory(String sku) {
        String originStr = "N/A";
        String destinationStr = "N/A";
        Product product = productRepository.findBySku(sku).stream()
                .findFirst().orElseThrow(() -> new EntityNotFoundException("Product not found: " + sku));
        List<StockMovement> stockMovements = stockMovementRepository.findByProductId(product.getId());
        CommandLineTable table = new CommandLineTable();
        table.setShowVerticalLines(true);
        table.setHeaders("ID", "TYPE", "QUANTITY","ORIGIN", "DESTINATION", "TIMESTAMP", "OPERATOR NOTES");
        for (StockMovement stockMovement : stockMovements) {
            if(stockMovement.fromZoneId() != null) {
                Zone origin = zoneRepository.findById(stockMovement.fromZoneId()).orElseThrow(() -> new EntityNotFoundException("Zone not found"));
                originStr = origin.getDisplayName();
            }
            if(stockMovement.toZoneId() != null) {
                Zone origin = zoneRepository.findById(stockMovement.toZoneId()).orElseThrow(() -> new EntityNotFoundException("Zone not found"));
                destinationStr = origin.getDisplayName();
            }

            Zone destination = zoneRepository.findById(stockMovement.toZoneId()).orElseThrow(() -> new EntityNotFoundException("Zone not found"));
            table.addRow(stockMovement.id(), stockMovement.movementType().name(), String.valueOf(stockMovement.quantity()),
                    originStr, destinationStr, stockMovement.timestamp().toString(),stockMovement.operatorNotes());
        }
        table.print();
    }
}