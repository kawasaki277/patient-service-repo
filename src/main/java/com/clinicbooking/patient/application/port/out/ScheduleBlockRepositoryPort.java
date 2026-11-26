package com.clinicbooking.patient.application.port.out;

import com.clinicbooking.patient.domain.ScheduleBlock;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleBlockRepositoryPort {
    ScheduleBlock save(ScheduleBlock block);

    List<ScheduleBlock> findOverlapping(Long doctorId, LocalDateTime from, LocalDateTime to);

    void deleteByAppointmentId(Long appointmentId);
}
