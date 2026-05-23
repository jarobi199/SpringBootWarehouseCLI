package io.warehouse.exception;

public class MovementValidationException extends RuntimeException {
    public  MovementValidationException(String message) {
        super(message);
    }
}