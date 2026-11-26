package com.clinicbooking.patient.infrastructure.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalTime;

@Entity
@Table(name = "pac_doctors")
public class DoctorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String fullName;
    public String specialty;
    public boolean active;
    public LocalTime workStart;
    public LocalTime workEnd;
    public int slotMinutes;
}
