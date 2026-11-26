package com.clinicbooking.patient.infrastructure.out;

import com.clinicbooking.patient.domain.AttendanceRecord;
import com.clinicbooking.patient.domain.AttendanceStatus;
import com.clinicbooking.patient.domain.BlockKind;
import com.clinicbooking.patient.domain.Doctor;
import com.clinicbooking.patient.domain.Patient;
import com.clinicbooking.patient.domain.ScheduleBlock;
import com.clinicbooking.patient.infrastructure.out.persistence.AttendanceEntity;
import com.clinicbooking.patient.infrastructure.out.persistence.AttendanceJpaRepository;
import com.clinicbooking.patient.infrastructure.out.persistence.DoctorEntity;
import com.clinicbooking.patient.infrastructure.out.persistence.DoctorJpaRepository;
import com.clinicbooking.patient.infrastructure.out.persistence.DoctorPersistenceAdapter;
import com.clinicbooking.patient.infrastructure.out.persistence.PatientEntity;
import com.clinicbooking.patient.infrastructure.out.persistence.PatientJpaRepository;
import com.clinicbooking.patient.infrastructure.out.persistence.PatientPersistenceAdapter;
import com.clinicbooking.patient.infrastructure.out.persistence.ScheduleBlockEntity;
import com.clinicbooking.patient.infrastructure.out.persistence.ScheduleBlockJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PersistenceAdaptersTest {

    @Mock PatientJpaRepository patientRepo;
    @Mock AttendanceJpaRepository attendanceRepo;
    @Mock DoctorJpaRepository doctorRepo;
    @Mock ScheduleBlockJpaRepository blockRepo;

    @Test
    void patientRoundTrip() {
        when(patientRepo.save(any(PatientEntity.class))).thenAnswer(i -> {
            PatientEntity e = i.getArgument(0);
            e.id = 1L;
            return e;
        });
        PatientPersistenceAdapter adapter = new PatientPersistenceAdapter(patientRepo, attendanceRepo);
        Patient saved = adapter.save(new Patient(null, "Ana", "ana@mail.com", "300", "Sanitas", "ABC12345", true));
        assertEquals(1L, saved.id());
        assertEquals("Sanitas", saved.insuranceProvider());

        PatientEntity stored = new PatientEntity();
        stored.id = 1L;
        stored.fullName = "Ana";
        stored.email = "ana@mail.com";
        when(patientRepo.findById(1L)).thenReturn(Optional.of(stored));
        assertTrue(adapter.findById(1L).isPresent());
        assertTrue(adapter.findById(2L).isEmpty());
    }

    @Test
    void attendanceRoundTrip() {
        when(attendanceRepo.save(any(AttendanceEntity.class))).thenAnswer(i -> {
            AttendanceEntity e = i.getArgument(0);
            e.id = 8L;
            return e;
        });
        AttendanceEntity e = new AttendanceEntity();
        e.id = 8L;
        e.patientId = 1L;
        e.appointmentId = 3L;
        e.status = "NO_SHOW";
        e.attendanceDate = LocalDate.of(2026, 11, 1);
        when(attendanceRepo.findByPatientId(1L)).thenReturn(List.of(e));

        PatientPersistenceAdapter adapter = new PatientPersistenceAdapter(patientRepo, attendanceRepo);
        AttendanceRecord saved = adapter.save(new AttendanceRecord(null, 1L, 3L, AttendanceStatus.NO_SHOW, LocalDate.of(2026, 11, 1)));
        assertEquals(8L, saved.id());
        assertEquals(AttendanceStatus.NO_SHOW, adapter.findByPatientId(1L).get(0).status());
    }

    @Test
    void doctorAndBlocks() {
        when(doctorRepo.save(any(DoctorEntity.class))).thenAnswer(i -> {
            DoctorEntity e = i.getArgument(0);
            e.id = 2L;
            return e;
        });
        DoctorPersistenceAdapter adapter = new DoctorPersistenceAdapter(doctorRepo, blockRepo);
        Doctor saved = adapter.save(new Doctor(null, "Dr. A", "Pediatría", true, LocalTime.of(8, 0), LocalTime.of(12, 0), 30));
        assertEquals(2L, saved.id());

        DoctorEntity de = new DoctorEntity();
        de.id = 2L;
        de.fullName = "Dr. A";
        de.specialty = "Pediatría";
        de.active = true;
        de.workStart = LocalTime.of(8, 0);
        de.workEnd = LocalTime.of(12, 0);
        de.slotMinutes = 30;
        when(doctorRepo.findById(2L)).thenReturn(Optional.of(de));
        when(doctorRepo.findBySpecialtyIgnoreCaseAndActiveTrue("Pediatría")).thenReturn(List.of(de));
        assertTrue(adapter.findById(2L).isPresent());
        assertEquals(1, adapter.findActiveBySpecialty("Pediatría").size());

        LocalDateTime start = LocalDateTime.of(2026, 11, 23, 8, 0);
        when(blockRepo.save(any(ScheduleBlockEntity.class))).thenAnswer(i -> {
            ScheduleBlockEntity e = i.getArgument(0);
            e.id = 4L;
            return e;
        });
        ScheduleBlock block = adapter.save(new ScheduleBlock(null, 2L, start, start.plusMinutes(30), BlockKind.BOOKED, "APPOINTMENT", 9L));
        assertEquals(4L, block.id());

        ScheduleBlockEntity be = new ScheduleBlockEntity();
        be.id = 4L;
        be.doctorId = 2L;
        be.startTime = start;
        be.endTime = start.plusMinutes(30);
        be.kind = "BOOKED";
        when(blockRepo.findByDoctorIdAndStartTimeLessThanAndEndTimeGreaterThan(2L, start.plusHours(1), start)).thenReturn(List.of(be));
        assertEquals(BlockKind.BOOKED, adapter.findOverlapping(2L, start, start.plusHours(1)).get(0).kind());

        adapter.deleteByAppointmentId(9L);
        verify(blockRepo).deleteByAppointmentId(9L);
    }
}
