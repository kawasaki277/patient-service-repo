package com.clinicbooking.patient.domain;

import java.time.LocalDate;

public record AttendanceRecord(Long id, Long patientId, Long appointmentId, AttendanceStatus status, LocalDate date) {

    public AttendanceRecord withId(Long newId) {
        return new AttendanceRecord(newId, patientId, appointmentId, status, date);
    }
}
