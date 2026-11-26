package com.clinicbooking.patient.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AttendanceSummaryTest {

    private AttendanceRecord rec(AttendanceStatus s, int day) {
        return new AttendanceRecord(null, 1L, (long) day, s, LocalDate.of(2026, 11, day));
    }

    @Test
    void summarizesRecords() {
        AttendanceSummary s = AttendanceSummary.from(List.of(
                rec(AttendanceStatus.ATTENDED, 2), rec(AttendanceStatus.ATTENDED, 10),
                rec(AttendanceStatus.NO_SHOW, 15), rec(AttendanceStatus.CANCELLED, 18)));
        assertEquals(4, s.total());
        assertEquals(2, s.attended());
        assertEquals(1, s.noShows());
        assertEquals(1, s.cancelled());
        assertEquals(LocalDate.of(2026, 11, 10), s.lastVisit());
    }

    @Test
    void emptyHistory() {
        AttendanceSummary s = AttendanceSummary.from(List.of());
        assertEquals(0, s.total());
        assertNull(s.lastVisit());
    }
}
