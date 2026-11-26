package com.clinicbooking.patient.infrastructure.in.rest;

import com.clinicbooking.patient.application.port.in.PatientUseCase;
import com.clinicbooking.patient.domain.AttendanceRecord;
import com.clinicbooking.patient.domain.AttendanceSummary;
import com.clinicbooking.patient.domain.InsuranceCoverage;
import com.clinicbooking.patient.domain.Patient;
import com.clinicbooking.patient.infrastructure.in.rest.dto.PatientDtos.AttendanceRequest;
import com.clinicbooking.patient.infrastructure.in.rest.dto.PatientDtos.CreatePatientRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {

    private final PatientUseCase useCase;

    public PatientController(PatientUseCase useCase) {
        this.useCase = useCase;
    }

    // ---- CRUD básico ----
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Patient create(@Valid @RequestBody CreatePatientRequest r) {
        boolean active = r.insuranceActive() == null || r.insuranceActive();
        return useCase.create(new Patient(null, r.fullName(), r.email(), r.phone(),
                r.insuranceProvider(), r.insuranceNumber(), active));
    }

    @GetMapping("/{id}")
    public Patient get(@PathVariable Long id) {
        return useCase.get(id);
    }

    // ---- APIs de negocio (no-CRUD) ----
    @GetMapping("/{id}/clinical-attendance-summary")
    public AttendanceSummary attendanceSummary(@PathVariable Long id) {
        return useCase.attendanceSummary(id);
    }

    @PostMapping("/{id}/verify-insurance")
    public InsuranceCoverage verifyInsurance(@PathVariable Long id) {
        return useCase.verifyInsurance(id);
    }

    // ---- Endpoint interno consumido por appointment-service ----
    @PostMapping("/{id}/attendance")
    @ResponseStatus(HttpStatus.CREATED)
    public AttendanceRecord recordAttendance(@PathVariable Long id, @Valid @RequestBody AttendanceRequest r) {
        return useCase.recordAttendance(id, r.appointmentId(), r.status(), r.date());
    }
}
