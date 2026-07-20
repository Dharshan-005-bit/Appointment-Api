package com.clinic.appointment.exception;

// Thrown for double-booking: doctor already taken at that slot, or patient already booked that day
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
