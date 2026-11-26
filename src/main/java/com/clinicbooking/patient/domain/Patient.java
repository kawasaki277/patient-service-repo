package com.clinicbooking.patient.domain;

public record Patient(Long id, String fullName, String email, String phone,
                      String insuranceProvider, String insuranceNumber, boolean insuranceActive) {

    public Patient {
        if (fullName == null || fullName.isBlank()) {
            throw new DomainException("El nombre del paciente es obligatorio");
        }
        if (email == null || !email.contains("@")) {
            throw new DomainException("Correo electrónico inválido");
        }
    }

    public Patient withId(Long newId) {
        return new Patient(newId, fullName, email, phone, insuranceProvider, insuranceNumber, insuranceActive);
    }

    /** Regla local de verificación de afiliación (punto de extensión para una API real de EPS). */
    public InsuranceCoverage verifyCoverage() {
        if (insuranceProvider == null || insuranceProvider.isBlank()
                || insuranceNumber == null || insuranceNumber.isBlank()) {
            return new InsuranceCoverage(false, "NO_AFFILIATION", "El paciente no tiene aseguradora registrada");
        }
        if (!insuranceNumber.matches("[A-Za-z0-9]{6,20}")) {
            return new InsuranceCoverage(false, "INVALID_NUMBER", "El número de afiliación tiene un formato inválido");
        }
        if (!insuranceActive) {
            return new InsuranceCoverage(false, "INACTIVE", "La afiliación con " + insuranceProvider + " no está vigente");
        }
        return new InsuranceCoverage(true, "ACTIVE", "Afiliación vigente con " + insuranceProvider);
    }
}
