package com.clinicbooking.patient.infrastructure.in.rest.dto;

import com.clinicbooking.patient.domain.DoctorAvailability;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public final class DoctorDtos {
    private DoctorDtos() {
    }

    public record CreateDoctorRequest(@NotBlank String fullName, @NotBlank String specialty,
                                      @NotNull LocalTime workStart, @NotNull LocalTime workEnd,
                                      @Min(5) @Max(240) int slotMinutes) {
    }

    public record BlockScheduleRequest(@NotNull LocalDateTime start, @NotNull LocalDateTime end, @NotBlank String reason) {
    }

    public record BookingRequest(@NotNull Long appointmentId, @NotNull LocalDateTime start, @NotNull LocalDateTime end) {
    }

    public record SlotsResponse(Long doctorId, LocalDate date, List<LocalDateTime> slots) {
    }

    public record AvailabilityResponse(List<DoctorAvailability> doctors) {
    }
}
