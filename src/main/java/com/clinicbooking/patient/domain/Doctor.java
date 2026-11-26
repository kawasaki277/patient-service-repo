package com.clinicbooking.patient.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public record Doctor(Long id, String fullName, String specialty, boolean active,
                     LocalTime workStart, LocalTime workEnd, int slotMinutes) {

    public Doctor {
        if (fullName == null || fullName.isBlank()) {
            throw new DomainException("El nombre del médico es obligatorio");
        }
        if (specialty == null || specialty.isBlank()) {
            throw new DomainException("La especialidad es obligatoria");
        }
        if (workStart == null || workEnd == null || !workStart.isBefore(workEnd)) {
            throw new DomainException("El horario de atención es inválido");
        }
        if (slotMinutes < 5 || slotMinutes > 240) {
            throw new DomainException("La duración del slot debe estar entre 5 y 240 minutos");
        }
    }

    public Doctor withId(Long newId) {
        return new Doctor(newId, fullName, specialty, active, workStart, workEnd, slotMinutes);
    }

    /** Slots libres del día: laborables, dentro del horario y sin cruce con bloqueos/citas. */
    public List<LocalDateTime> generateSlots(LocalDate date, List<ScheduleBlock> blocks) {
        DayOfWeek day = date.getDayOfWeek();
        if (!active || day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return List.of();
        }
        List<LocalDateTime> slots = new ArrayList<>();
        LocalDateTime cursor = date.atTime(workStart);
        LocalDateTime limit = date.atTime(workEnd);
        while (!cursor.plusMinutes(slotMinutes).isAfter(limit)) {
            LocalDateTime slotStart = cursor;
            LocalDateTime slotEnd = cursor.plusMinutes(slotMinutes);
            if (blocks.stream().noneMatch(b -> b.overlaps(slotStart, slotEnd))) {
                slots.add(slotStart);
            }
            cursor = slotEnd;
        }
        return slots;
    }
}
