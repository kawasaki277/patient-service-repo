package com.clinicbooking.patient.application.port.out;

import com.clinicbooking.patient.domain.Patient;

import java.util.Optional;

public interface PatientRepositoryPort {
    Patient save(Patient patient);

    Optional<Patient> findById(Long id);
}
