package com.clinicbooking.patient.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduleBlockTest {

    private final LocalDateTime nine = LocalDateTime.of(2026, 11, 23, 9, 0);

    @Test
    void overlapsAndAdjacency() {
        ScheduleBlock block = new ScheduleBlock(null, 1L, nine, nine.plusHours(1), BlockKind.BLOCKED, "x", null);
        assertTrue(block.overlaps(nine.plusMinutes(30), nine.plusHours(2)));
        assertFalse(block.overlaps(nine.plusHours(1), nine.plusHours(2)));
        assertFalse(block.overlaps(nine.minusHours(1), nine));
        assertEquals(4L, block.withId(4L).id());
    }

    @Test
    void rejectsInvertedRange() {
        assertThrows(DomainException.class,
                () -> new ScheduleBlock(null, 1L, nine, nine.minusMinutes(1), BlockKind.BLOCKED, "x", null));
    }
}
