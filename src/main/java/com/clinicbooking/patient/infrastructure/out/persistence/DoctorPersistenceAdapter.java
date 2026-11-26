package com.clinicbooking.patient.infrastructure.out.persistence;

import com.clinicbooking.patient.application.port.out.DoctorRepositoryPort;
import com.clinicbooking.patient.application.port.out.ScheduleBlockRepositoryPort;
import com.clinicbooking.patient.domain.BlockKind;
import com.clinicbooking.patient.domain.Doctor;
import com.clinicbooking.patient.domain.ScheduleBlock;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class DoctorPersistenceAdapter implements DoctorRepositoryPort, ScheduleBlockRepositoryPort {

    private final DoctorJpaRepository doctors;
    private final ScheduleBlockJpaRepository blocks;

    public DoctorPersistenceAdapter(DoctorJpaRepository doctors, ScheduleBlockJpaRepository blocks) {
        this.doctors = doctors;
        this.blocks = blocks;
    }

    @Override
    public Doctor save(Doctor d) {
        DoctorEntity e = new DoctorEntity();
        e.id = d.id();
        e.fullName = d.fullName();
        e.specialty = d.specialty();
        e.active = d.active();
        e.workStart = d.workStart();
        e.workEnd = d.workEnd();
        e.slotMinutes = d.slotMinutes();
        return toDoctor(doctors.save(e));
    }

    @Override
    public Optional<Doctor> findById(Long id) {
        return doctors.findById(id).map(this::toDoctor);
    }

    @Override
    public List<Doctor> findActiveBySpecialty(String specialty) {
        return doctors.findBySpecialtyIgnoreCaseAndActiveTrue(specialty).stream().map(this::toDoctor).toList();
    }

    @Override
    public ScheduleBlock save(ScheduleBlock b) {
        ScheduleBlockEntity e = new ScheduleBlockEntity();
        e.id = b.id();
        e.doctorId = b.doctorId();
        e.startTime = b.start();
        e.endTime = b.end();
        e.kind = b.kind().name();
        e.reason = b.reason();
        e.appointmentId = b.appointmentId();
        return toBlock(blocks.save(e));
    }

    @Override
    public List<ScheduleBlock> findOverlapping(Long doctorId, LocalDateTime from, LocalDateTime to) {
        return blocks.findByDoctorIdAndStartTimeLessThanAndEndTimeGreaterThan(doctorId, to, from)
                .stream().map(this::toBlock).toList();
    }

    @Override
    @Transactional
    public void deleteByAppointmentId(Long appointmentId) {
        blocks.deleteByAppointmentId(appointmentId);
    }

    private Doctor toDoctor(DoctorEntity e) {
        return new Doctor(e.id, e.fullName, e.specialty, e.active, e.workStart, e.workEnd, e.slotMinutes);
    }

    private ScheduleBlock toBlock(ScheduleBlockEntity e) {
        return new ScheduleBlock(e.id, e.doctorId, e.startTime, e.endTime, BlockKind.valueOf(e.kind), e.reason, e.appointmentId);
    }
}
