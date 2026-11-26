package com.clinicbooking.patient.application.port.out;

import com.clinicbooking.patient.domain.AttendanceRecord;

import java.util.List;

public interface AttendanceRepositoryPort {
    AttendanceRecord save(AttendanceRecord record);

    List<AttendanceRecord> findByPatientId(Long patientId);
}
