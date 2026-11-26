package com.clinicbooking.patient.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public record ScheduleBlock(Long id, Long doctorId, LocalDateTime start, LocalDateTime end,
                            BlockKind kind, String reason, Long appointmentId) {

    public ScheduleBlock {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(end, "end");
        if (!end.isAfter(start)) {
            throw new DomainException("El fin del bloqueo debe ser posterior a su inicio");
        }
    }

    public boolean overlaps(LocalDateTime otherStart, LocalDateTime otherEnd) {
        return start.isBefore(otherEnd) && end.isAfter(otherStart);
    }

    public ScheduleBlock withId(Long newId) {
        return new ScheduleBlock(newId, doctorId, start, end, kind, reason, appointmentId);
    }
}
