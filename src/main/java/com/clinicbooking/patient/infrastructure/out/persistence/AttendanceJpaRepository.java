package com.clinicbooking.patient.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceJpaRepository extends JpaRepository<AttendanceEntity, Long> {
    List<AttendanceEntity> findByPatientId(Long patientId);
}
