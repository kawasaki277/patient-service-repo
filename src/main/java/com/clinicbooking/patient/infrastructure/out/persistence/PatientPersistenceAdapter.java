package com.clinicbooking.patient.infrastructure.out.persistence;

import com.clinicbooking.patient.application.port.out.AttendanceRepositoryPort;
import com.clinicbooking.patient.application.port.out.PatientRepositoryPort;
import com.clinicbooking.patient.domain.AttendanceRecord;
import com.clinicbooking.patient.domain.AttendanceStatus;
import com.clinicbooking.patient.domain.Patient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PatientPersistenceAdapter implements PatientRepositoryPort, AttendanceRepositoryPort {

    private final PatientJpaRepository patients;
    private final AttendanceJpaRepository attendance;

    public PatientPersistenceAdapter(PatientJpaRepository patients, AttendanceJpaRepository attendance) {
        this.patients = patients;
        this.attendance = attendance;
    }

    @Override
    public Patient save(Patient p) {
        PatientEntity e = new PatientEntity();
        e.id = p.id();
        e.fullName = p.fullName();
        e.email = p.email();
        e.phone = p.phone();
        e.insuranceProvider = p.insuranceProvider();
        e.insuranceNumber = p.insuranceNumber();
        e.insuranceActive = p.insuranceActive();
        return toPatient(patients.save(e));
    }

    @Override
    public Optional<Patient> findById(Long id) {
        return patients.findById(id).map(this::toPatient);
    }

    @Override
    public AttendanceRecord save(AttendanceRecord r) {
        AttendanceEntity e = new AttendanceEntity();
        e.id = r.id();
        e.patientId = r.patientId();
        e.appointmentId = r.appointmentId();
        e.status = r.status().name();
        e.attendanceDate = r.date();
        return toRecord(attendance.save(e));
    }

    @Override
    public List<AttendanceRecord> findByPatientId(Long patientId) {
        return attendance.findByPatientId(patientId).stream().map(this::toRecord).toList();
    }

    private Patient toPatient(PatientEntity e) {
        return new Patient(e.id, e.fullName, e.email, e.phone, e.insuranceProvider, e.insuranceNumber, e.insuranceActive);
    }

    private AttendanceRecord toRecord(AttendanceEntity e) {
        return new AttendanceRecord(e.id, e.patientId, e.appointmentId, AttendanceStatus.valueOf(e.status), e.attendanceDate);
    }
}
