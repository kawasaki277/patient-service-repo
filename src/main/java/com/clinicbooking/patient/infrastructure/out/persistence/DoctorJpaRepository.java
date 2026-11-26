package com.clinicbooking.patient.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorJpaRepository extends JpaRepository<DoctorEntity, Long> {
    List<DoctorEntity> findBySpecialtyIgnoreCaseAndActiveTrue(String specialty);
}
