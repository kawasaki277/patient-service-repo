package com.clinicbooking.patient.application.port.in;

import com.clinicbooking.patient.domain.Doctor;
import com.clinicbooking.patient.domain.DoctorAvailability;
import com.clinicbooking.patient.domain.ScheduleBlock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DoctorUseCase {
    Doctor create(Doctor doctor);

    Doctor get(Long id);

    List<LocalDateTime> availableSlots(Long doctorId, LocalDate date);

    ScheduleBlock blockSchedule(Long doctorId, LocalDateTime start, LocalDateTime end, String reason);

    List<DoctorAvailability> findBySpecialtyAndDate(String specialty, LocalDate from, LocalDate to);

    ScheduleBlock registerBooking(Long doctorId, Long appointmentId, LocalDateTime start, LocalDateTime end);

    void releaseBooking(Long appointmentId);
}
