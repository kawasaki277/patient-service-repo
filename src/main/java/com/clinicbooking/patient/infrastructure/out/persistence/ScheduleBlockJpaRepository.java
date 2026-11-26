package com.clinicbooking.patient.infrastructure.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleBlockJpaRepository extends JpaRepository<ScheduleBlockEntity, Long> {

    /** Bloques que se cruzan con [start, end): startTime &lt; end y endTime &gt; start. */
    List<ScheduleBlockEntity> findByDoctorIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long doctorId, LocalDateTime end, LocalDateTime start);

    void deleteByAppointmentId(Long appointmentId);
}
