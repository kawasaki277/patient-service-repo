package com.clinicbooking.patient.application.port.out;

import com.clinicbooking.patient.domain.Doctor;

import java.util.List;
import java.util.Optional;

public interface DoctorRepositoryPort {
    Doctor save(Doctor doctor);

    Optional<Doctor> findById(Long id);

    List<Doctor> findActiveBySpecialty(String specialty);
}
