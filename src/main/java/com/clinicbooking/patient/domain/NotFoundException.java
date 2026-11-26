package com.clinicbooking.patient.domain;

public class NotFoundException extends DomainException {
    public NotFoundException(String message) {
        super(message);
    }
}
