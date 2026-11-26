package com.clinicbooking.patient.application.port.in;

import com.clinicbooking.patient.domain.AttendanceRecord;
import com.clinicbooking.patient.domain.AttendanceStatus;
import com.clinicbooking.patient.domain.AttendanceSummary;
import com.clinicbooking.patient.domain.InsuranceCoverage;
import com.clinicbooking.patient.domain.Patient;

import java.time.LocalDate;

public interface PatientUseCase {
    Patient create(Patient patient);

    Patient get(Long id);

    AttendanceSummary attendanceSummary(Long patientId);

    InsuranceCoverage verifyInsurance(Long patientId);

    AttendanceRecord recordAttendance(Long patientId, Long appointmentId, AttendanceStatus status, LocalDate date);
}
