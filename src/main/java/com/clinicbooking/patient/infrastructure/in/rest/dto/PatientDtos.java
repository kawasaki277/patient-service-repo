package com.clinicbooking.patient.infrastructure.in.rest.dto;

import com.clinicbooking.patient.domain.AttendanceStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public final class PatientDtos {
    private PatientDtos() {
    }

    public record CreatePatientRequest(@NotBlank String fullName, @NotBlank @Email String email, String phone,
                                       String insuranceProvider, String insuranceNumber, Boolean insuranceActive) {
    }

    public record AttendanceRequest(@NotNull Long appointmentId, @NotNull AttendanceStatus status, @NotNull LocalDate date) {
    }
}
