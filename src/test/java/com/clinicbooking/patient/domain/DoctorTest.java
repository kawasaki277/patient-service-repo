package com.clinicbooking.patient.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DoctorTest {

    private static final LocalDate MONDAY = LocalDate.of(2026, 11, 23);
    private final Doctor doctor = new Doctor(1L, "Dra. Ruiz", "Cardiología", true,
            LocalTime.of(8, 0), LocalTime.of(10, 0), 30);

    @Test
    void generatesAllSlotsWhenNoBlocks() {
        List<LocalDateTime> slots = doctor.generateSlots(MONDAY, List.of());
        assertEquals(4, slots.size());
        assertEquals(MONDAY.atTime(8, 0), slots.get(0));
        assertEquals(MONDAY.atTime(9, 30), slots.get(3));
    }

    @Test
    void excludesSlotsOverlappingBlocks() {
        ScheduleBlock block = new ScheduleBlock(1L, 1L, MONDAY.atTime(8, 15), MONDAY.atTime(8, 45),
                BlockKind.BLOCKED, "Urgencia", null);
        List<LocalDateTime> slots = doctor.generateSlots(MONDAY, List.of(block));
        assertEquals(List.of(MONDAY.atTime(9, 0), MONDAY.atTime(9, 30)), slots);
    }

    @Test
    void noSlotsOnWeekends() {
        assertTrue(doctor.generateSlots(LocalDate.of(2026, 11, 22), List.of()).isEmpty());
        assertTrue(doctor.generateSlots(LocalDate.of(2026, 11, 28), List.of()).isEmpty());
    }

    @Test
    void noSlotsWhenInactive() {
        Doctor inactive = new Doctor(1L, "X", "Y", false, LocalTime.of(8, 0), LocalTime.of(10, 0), 30);
        assertTrue(inactive.generateSlots(MONDAY, List.of()).isEmpty());
    }

    @Test
    void withIdKeepsData() {
        Doctor withId = new Doctor(null, "A", "B", true, LocalTime.of(8, 0), LocalTime.of(9, 0), 20).withId(9L);
        assertEquals(9L, withId.id());
        assertEquals(20, withId.slotMinutes());
    }

    @Test
    void validatesInvariants() {
        LocalTime a = LocalTime.of(8, 0);
        LocalTime b = LocalTime.of(9, 0);
        assertThrows(DomainException.class, () -> new Doctor(null, " ", "E", true, a, b, 30));
        assertThrows(DomainException.class, () -> new Doctor(null, "N", null, true, a, b, 30));
        assertThrows(DomainException.class, () -> new Doctor(null, "N", "E", true, b, a, 30));
        assertThrows(DomainException.class, () -> new Doctor(null, "N", "E", true, a, b, 1));
        assertFalse(new Doctor(null, "N", "E", false, a, b, 30).active());
    }
}
