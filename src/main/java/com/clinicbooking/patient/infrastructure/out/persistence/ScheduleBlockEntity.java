package com.clinicbooking.patient.infrastructure.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "pac_schedule_blocks")
public class ScheduleBlockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public Long doctorId;
    public LocalDateTime startTime;
    public LocalDateTime endTime;
    public String kind;
    public String reason;
    public Long appointmentId;
}
