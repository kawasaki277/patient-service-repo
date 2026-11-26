package com.clinicbooking.patient.application.service;

import com.clinicbooking.patient.application.port.in.PatientUseCase;
import com.clinicbooking.patient.application.port.out.AttendanceRepositoryPort;
import com.clinicbooking.patient.application.port.out.PatientRepositoryPort;
import com.clinicbooking.patient.domain.AttendanceRecord;
import com.clinicbooking.patient.domain.AttendanceStatus;
import com.clinicbooking.patient.domain.AttendanceSummary;
import com.clinicbooking.patient.domain.InsuranceCoverage;
import com.clinicbooking.patient.domain.NotFoundException;
import com.clinicbooking.patient.domain.Patient;

import java.time.LocalDate;

public class PatientService implements PatientUseCase {

    private final PatientRepositoryPort patients;
    private final AttendanceRepositoryPort attendance;

    public PatientService(PatientRepositoryPort patients, AttendanceRepositoryPort attendance) {
        this.patients = patients;
        this.attendance = attendance;
    }

    @Override
    public Patient create(Patient patient) {
        return patients.save(patient);
    }

    @Override
    public Patient get(Long id) {
        return patients.findById(id).orElseThrow(() -> new NotFoundException("Paciente no encontrado: " + id));
    }

    @Override
    public AttendanceSummary attendanceSummary(Long patientId) {
        get(patientId);
        return AttendanceSummary.from(attendance.findByPatientId(patientId));
    }

    @Override
    public InsuranceCoverage verifyInsurance(Long patientId) {
        return get(patientId).verifyCoverage();
    }

    @Override
    public AttendanceRecord recordAttendance(Long patientId, Long appointmentId, AttendanceStatus status, LocalDate date) {
        get(patientId);
        return attendance.save(new AttendanceRecord(null, patientId, appointmentId, status, date));
    }
}
