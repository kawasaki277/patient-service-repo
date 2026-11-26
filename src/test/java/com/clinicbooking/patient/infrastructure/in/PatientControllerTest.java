package com.clinicbooking.patient.infrastructure.in;

import com.clinicbooking.patient.application.port.in.PatientUseCase;
import com.clinicbooking.patient.domain.AttendanceRecord;
import com.clinicbooking.patient.domain.AttendanceStatus;
import com.clinicbooking.patient.domain.AttendanceSummary;
import com.clinicbooking.patient.domain.InsuranceCoverage;
import com.clinicbooking.patient.domain.Patient;
import com.clinicbooking.patient.infrastructure.in.rest.PatientController;
import com.clinicbooking.patient.infrastructure.in.rest.dto.PatientDtos.AttendanceRequest;
import com.clinicbooking.patient.infrastructure.in.rest.dto.PatientDtos.CreatePatientRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PatientControllerTest {

    @Mock PatientUseCase useCase;

    @Test
    void createDefaultsInsuranceToActive() {
        when(useCase.create(any())).thenAnswer(i -> ((Patient) i.getArgument(0)).withId(3L));
        PatientController controller = new PatientController(useCase);
        Patient p = controller.create(new CreatePatientRequest("Ana", "ana@mail.com", "300", "Sanitas", "ABC12345", null));
        assertEquals(3L, p.id());
        assertTrue(p.insuranceActive());

        controller.create(new CreatePatientRequest("Ana", "ana@mail.com", "300", "Sanitas", "ABC12345", false));
        ArgumentCaptor<Patient> captor = ArgumentCaptor.forClass(Patient.class);
        verify(useCase, org.mockito.Mockito.times(2)).create(captor.capture());
        assertFalse(captor.getAllValues().get(1).insuranceActive());
    }

    @Test
    void businessEndpointsDelegate() {
        PatientController controller = new PatientController(useCase);
        Patient patient = new Patient(1L, "Ana", "ana@mail.com", null, null, null, true);
        when(useCase.get(1L)).thenReturn(patient);
        AttendanceSummary summary = new AttendanceSummary(1, 1, 0, 0, LocalDate.of(2026, 11, 1));
        when(useCase.attendanceSummary(1L)).thenReturn(summary);
        InsuranceCoverage coverage = new InsuranceCoverage(true, "ACTIVE", "ok");
        when(useCase.verifyInsurance(1L)).thenReturn(coverage);
        AttendanceRecord rec = new AttendanceRecord(1L, 1L, 9L, AttendanceStatus.NO_SHOW, LocalDate.of(2026, 11, 2));
        when(useCase.recordAttendance(1L, 9L, AttendanceStatus.NO_SHOW, LocalDate.of(2026, 11, 2))).thenReturn(rec);

        assertEquals(patient, controller.get(1L));
        assertEquals(summary, controller.attendanceSummary(1L));
        assertEquals(coverage, controller.verifyInsurance(1L));
        assertEquals(rec, controller.recordAttendance(1L, new AttendanceRequest(9L, AttendanceStatus.NO_SHOW, LocalDate.of(2026, 11, 2))));
    }
}
