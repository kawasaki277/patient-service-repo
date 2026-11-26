package com.clinicbooking.patient.application;

import com.clinicbooking.patient.application.port.out.DoctorRepositoryPort;
import com.clinicbooking.patient.application.port.out.ScheduleBlockRepositoryPort;
import com.clinicbooking.patient.application.service.DoctorService;
import com.clinicbooking.patient.domain.BlockKind;
import com.clinicbooking.patient.domain.Doctor;
import com.clinicbooking.patient.domain.DoctorAvailability;
import com.clinicbooking.patient.domain.DomainException;
import com.clinicbooking.patient.domain.NotFoundException;
import com.clinicbooking.patient.domain.ScheduleBlock;
import org.junit.jupiter.api.BeforeEach;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DoctorServiceTest {

    private static final LocalDate MONDAY = LocalDate.of(2026, 11, 23);

    @Mock DoctorRepositoryPort doctors;
    @Mock ScheduleBlockRepositoryPort blocks;

    private final Doctor doctor = new Doctor(1L, "Dra. Ruiz", "Cardiología", true,
            LocalTime.of(8, 0), LocalTime.of(10, 0), 30);
    private DoctorService service;

    @BeforeEach
    void setUp() {
        service = new DoctorService(doctors, blocks);
        when(doctors.findById(1L)).thenReturn(Optional.of(doctor));
        when(blocks.findOverlapping(any(), any(), any())).thenReturn(List.of());
        when(blocks.save(any(ScheduleBlock.class))).thenAnswer(i -> ((ScheduleBlock) i.getArgument(0)).withId(50L));
    }

    @Test
    void createAndGet() {
        when(doctors.save(doctor)).thenReturn(doctor);
        assertEquals(doctor, service.create(doctor));
        assertEquals(doctor, service.get(1L));
        assertThrows(NotFoundException.class, () -> service.get(99L));
    }

    @Test
    void availableSlotsWhenFree() {
        assertEquals(4, service.availableSlots(1L, MONDAY).size());
    }

    @Test
    void availableSlotsDiscountsBookedAppointments() {
        ScheduleBlock booked = new ScheduleBlock(9L, 1L, MONDAY.atTime(8, 0), MONDAY.atTime(8, 30),
                BlockKind.BOOKED, "APPOINTMENT", 5L);
        when(blocks.findOverlapping(eq(1L), any(), any())).thenReturn(List.of(booked));
        List<LocalDateTime> slots = service.availableSlots(1L, MONDAY);
        assertEquals(3, slots.size());
        assertFalse(slots.contains(MONDAY.atTime(8, 0)));
    }

    @Test
    void blockScheduleSavesBlockedRange() {
        ScheduleBlock b = service.blockSchedule(1L, MONDAY.atTime(8, 0), MONDAY.atTime(9, 0), "Capacitación");
        assertEquals(BlockKind.BLOCKED, b.kind());
        assertEquals(50L, b.id());
    }

    @Test
    void blockScheduleRejectsInvalidRangeAndUnknownDoctor() {
        assertThrows(DomainException.class,
                () -> service.blockSchedule(1L, MONDAY.atTime(9, 0), MONDAY.atTime(8, 0), "x"));
        assertThrows(NotFoundException.class,
                () -> service.blockSchedule(99L, MONDAY.atTime(8, 0), MONDAY.atTime(9, 0), "x"));
    }

    @Test
    void findBySpecialtyReturnsDoctorsWithRealAvailability() {
        when(doctors.findActiveBySpecialty("Cardiología")).thenReturn(List.of(doctor));
        List<DoctorAvailability> result = service.findBySpecialtyAndDate("Cardiología", MONDAY, MONDAY.plusDays(6));
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).slotsByDate().size()); // lunes a viernes
    }

    @Test
    void findBySpecialtyOmitsFullyBlockedDoctors() {
        when(doctors.findActiveBySpecialty("Cardiología")).thenReturn(List.of(doctor));
        ScheduleBlock all = new ScheduleBlock(1L, 1L, MONDAY.atTime(0, 0), MONDAY.plusDays(1).atTime(0, 0),
                BlockKind.BLOCKED, "Ausencia", null);
        when(blocks.findOverlapping(eq(1L), any(), any())).thenReturn(List.of(all));
        assertTrue(service.findBySpecialtyAndDate("Cardiología", MONDAY, MONDAY).isEmpty());
    }

    @Test
    void findBySpecialtyValidatesRange() {
        assertThrows(DomainException.class, () -> service.findBySpecialtyAndDate("X", MONDAY.plusDays(1), MONDAY));
        assertThrows(DomainException.class, () -> service.findBySpecialtyAndDate("X", MONDAY, MONDAY.plusDays(60)));
    }

    @Test
    void registerBookingSavesBookedBlock() {
        ScheduleBlock b = service.registerBooking(1L, 5L, MONDAY.atTime(8, 0), MONDAY.atTime(8, 30));
        assertEquals(BlockKind.BOOKED, b.kind());
        assertEquals(5L, b.appointmentId());
    }

    @Test
    void registerBookingFailsOnConflict() {
        ScheduleBlock existing = new ScheduleBlock(2L, 1L, MONDAY.atTime(8, 0), MONDAY.atTime(8, 30),
                BlockKind.BOOKED, "APPOINTMENT", 3L);
        when(blocks.findOverlapping(eq(1L), any(), any())).thenReturn(List.of(existing));
        assertThrows(DomainException.class,
                () -> service.registerBooking(1L, 5L, MONDAY.atTime(8, 0), MONDAY.atTime(8, 30)));
    }

    @Test
    void releaseBookingDelegates() {
        service.releaseBooking(5L);
        verify(blocks).deleteByAppointmentId(5L);
    }
}
