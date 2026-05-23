package io.warehouse.model;

import io.warehouse.enums.MovementType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "stock_movements")
public record StockMovement (@Id String id, String productId, String fromZoneId, String toZoneId, int quantity, MovementType movementType, LocalDateTime timestamp, String operatorNotes) {}
