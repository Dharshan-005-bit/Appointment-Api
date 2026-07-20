package com.clinic.appointment.exception;

// Thrown when an appointment_id / doctor lookup doesn't resolve to anything
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
