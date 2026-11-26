package com.clinicbooking.patient.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientJpaRepository extends JpaRepository<PatientEntity, Long> {
}
