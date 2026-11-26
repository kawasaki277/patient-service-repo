package com.clinicbooking.patient.infrastructure.in.rest;

import com.clinicbooking.patient.application.port.in.DoctorUseCase;
import com.clinicbooking.patient.domain.Doctor;
import com.clinicbooking.patient.domain.DoctorAvailability;
import com.clinicbooking.patient.domain.ScheduleBlock;
import com.clinicbooking.patient.infrastructure.in.rest.dto.DoctorDtos.BlockScheduleRequest;
import com.clinicbooking.patient.infrastructure.in.rest.dto.DoctorDtos.BookingRequest;
import com.clinicbooking.patient.infrastructure.in.rest.dto.DoctorDtos.CreateDoctorRequest;
import com.clinicbooking.patient.infrastructure.in.rest.dto.DoctorDtos.SlotsResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors")
public class DoctorController {

    private final DoctorUseCase useCase;

    public DoctorController(DoctorUseCase useCase) {
        this.useCase = useCase;
    }

    // ---- CRUD básico ----
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Doctor create(@Valid @RequestBody CreateDoctorRequest r) {
        return useCase.create(new Doctor(null, r.fullName(), r.specialty(), true, r.workStart(), r.workEnd(), r.slotMinutes()));
    }

    @GetMapping("/{id}")
    public Doctor get(@PathVariable Long id) {
        return useCase.get(id);
    }

    // ---- APIs de negocio (no-CRUD) ----
    @GetMapping("/{id}/available-slots")
    public SlotsResponse availableSlots(@PathVariable Long id,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return new SlotsResponse(id, date, useCase.availableSlots(id, date));
    }

    @PostMapping("/{id}/block-schedule")
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleBlock blockSchedule(@PathVariable Long id, @Valid @RequestBody BlockScheduleRequest r) {
        return useCase.blockSchedule(id, r.start(), r.end(), r.reason());
    }

    @GetMapping("/by-specialty-and-date")
    public List<DoctorAvailability> bySpecialtyAndDate(
            @RequestParam String specialty,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return useCase.findBySpecialtyAndDate(specialty, from, to);
    }

    // ---- Endpoints internos consumidos por appointment-service ----
    @PostMapping("/{id}/booked-slots")
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleBlock registerBooking(@PathVariable Long id, @Valid @RequestBody BookingRequest r) {
        return useCase.registerBooking(id, r.appointmentId(), r.start(), r.end());
    }

    @DeleteMapping("/booked-slots/{appointmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void releaseBooking(@PathVariable Long appointmentId) {
        useCase.releaseBooking(appointmentId);
    }
}
