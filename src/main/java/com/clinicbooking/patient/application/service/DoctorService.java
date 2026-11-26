package com.clinicbooking.patient.application.service;

import com.clinicbooking.patient.application.port.in.DoctorUseCase;
import com.clinicbooking.patient.application.port.out.DoctorRepositoryPort;
import com.clinicbooking.patient.application.port.out.ScheduleBlockRepositoryPort;
import com.clinicbooking.patient.domain.BlockKind;
import com.clinicbooking.patient.domain.Doctor;
import com.clinicbooking.patient.domain.DoctorAvailability;
import com.clinicbooking.patient.domain.DomainException;
import com.clinicbooking.patient.domain.NotFoundException;
import com.clinicbooking.patient.domain.ScheduleBlock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DoctorService implements DoctorUseCase {

    static final int MAX_RANGE_DAYS = 31;

    private final DoctorRepositoryPort doctors;
    private final ScheduleBlockRepositoryPort blocks;

    public DoctorService(DoctorRepositoryPort doctors, ScheduleBlockRepositoryPort blocks) {
        this.doctors = doctors;
        this.blocks = blocks;
    }

    @Override
    public Doctor create(Doctor doctor) {
        return doctors.save(doctor);
    }

    @Override
    public Doctor get(Long id) {
        return doctors.findById(id).orElseThrow(() -> new NotFoundException("Médico no encontrado: " + id));
    }

    @Override
    public List<LocalDateTime> availableSlots(Long doctorId, LocalDate date) {
        return slotsFor(get(doctorId), date);
    }

    @Override
    public ScheduleBlock blockSchedule(Long doctorId, LocalDateTime start, LocalDateTime end, String reason) {
        get(doctorId);
        return blocks.save(new ScheduleBlock(null, doctorId, start, end, BlockKind.BLOCKED, reason, null));
    }

    @Override
    public List<DoctorAvailability> findBySpecialtyAndDate(String specialty, LocalDate from, LocalDate to) {
        if (to.isBefore(from)) {
            throw new DomainException("El rango de fechas es inválido");
        }
        if (ChronoUnit.DAYS.between(from, to) > MAX_RANGE_DAYS) {
            throw new DomainException("El rango máximo de búsqueda es de " + MAX_RANGE_DAYS + " días");
        }
        List<DoctorAvailability> result = new ArrayList<>();
        for (Doctor doctor : doctors.findActiveBySpecialty(specialty)) {
            Map<LocalDate, List<LocalDateTime>> byDate = new LinkedHashMap<>();
            for (LocalDate day = from; !day.isAfter(to); day = day.plusDays(1)) {
                List<LocalDateTime> slots = slotsFor(doctor, day);
                if (!slots.isEmpty()) {
                    byDate.put(day, slots);
                }
            }
            if (!byDate.isEmpty()) {
                result.add(new DoctorAvailability(doctor, byDate));
            }
        }
        return result;
    }

    @Override
    public ScheduleBlock registerBooking(Long doctorId, Long appointmentId, LocalDateTime start, LocalDateTime end) {
        get(doctorId);
        if (!blocks.findOverlapping(doctorId, start, end).isEmpty()) {
            throw new DomainException("El horario solicitado ya no está disponible");
        }
        return blocks.save(new ScheduleBlock(null, doctorId, start, end, BlockKind.BOOKED, "APPOINTMENT", appointmentId));
    }

    @Override
    public void releaseBooking(Long appointmentId) {
        blocks.deleteByAppointmentId(appointmentId);
    }

    private List<LocalDateTime> slotsFor(Doctor doctor, LocalDate date) {
        List<ScheduleBlock> dayBlocks =
                blocks.findOverlapping(doctor.id(), date.atStartOfDay(), date.plusDays(1).atStartOfDay());
        return doctor.generateSlots(date, dayBlocks);
    }
}
