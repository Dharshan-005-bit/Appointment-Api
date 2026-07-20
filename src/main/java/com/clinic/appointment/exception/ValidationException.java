package com.clinic.appointment.exception;

// Thrown for anything client-fixable: missing fields, bad date format, invalid time slot, past dates
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
