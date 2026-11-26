package com.clinicbooking.patient.application;

import com.clinicbooking.patient.application.port.out.AttendanceRepositoryPort;
import com.clinicbooking.patient.application.port.out.PatientRepositoryPort;
import com.clinicbooking.patient.application.service.PatientService;
import com.clinicbooking.patient.domain.AttendanceRecord;
import com.clinicbooking.patient.domain.AttendanceStatus;
import com.clinicbooking.patient.domain.AttendanceSummary;
import com.clinicbooking.patient.domain.NotFoundException;
import com.clinicbooking.patient.domain.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PatientServiceTest {

    @Mock PatientRepositoryPort patients;
    @Mock AttendanceRepositoryPort attendance;

    private final Patient patient = new Patient(1L, "Ana", "ana@mail.com", "300", "Sanitas", "ABC12345", true);
    private PatientService service;

    @BeforeEach
    void setUp() {
        service = new PatientService(patients, attendance);
        when(patients.findById(1L)).thenReturn(Optional.of(patient));
    }

    @Test
    void createAndGet() {
        when(patients.save(patient)).thenReturn(patient);
        assertEquals(patient, service.create(patient));
        assertEquals(patient, service.get(1L));
        assertThrows(NotFoundException.class, () -> service.get(2L));
    }

    @Test
    void attendanceSummaryAggregatesHistory() {
        when(attendance.findByPatientId(1L)).thenReturn(List.of(
                new AttendanceRecord(1L, 1L, 10L, AttendanceStatus.ATTENDED, LocalDate.of(2026, 10, 1)),
                new AttendanceRecord(2L, 1L, 11L, AttendanceStatus.NO_SHOW, LocalDate.of(2026, 10, 20))));
        AttendanceSummary s = service.attendanceSummary(1L);
        assertEquals(2, s.total());
        assertEquals(1, s.noShows());
        assertEquals(LocalDate.of(2026, 10, 1), s.lastVisit());
    }

    @Test
    void attendanceSummaryFailsForUnknownPatient() {
        assertThrows(NotFoundException.class, () -> service.attendanceSummary(2L));
    }

    @Test
    void verifyInsuranceUsesDomainRule() {
        assertTrue(service.verifyInsurance(1L).covered());
    }

    @Test
    void recordAttendancePersists() {
        when(attendance.save(any(AttendanceRecord.class))).thenAnswer(i -> i.getArgument(0));
        AttendanceRecord r = service.recordAttendance(1L, 10L, AttendanceStatus.NO_SHOW, LocalDate.of(2026, 11, 1));
        assertEquals(AttendanceStatus.NO_SHOW, r.status());
        verify(attendance).save(any(AttendanceRecord.class));
    }

    @Test
    void recordAttendanceRequiresExistingPatient() {
        assertThrows(NotFoundException.class,
                () -> service.recordAttendance(2L, 10L, AttendanceStatus.ATTENDED, LocalDate.now()));
        verify(attendance, never()).save(any());
    }
}
