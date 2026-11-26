package com.clinicbooking.patient.infrastructure.out.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pac_patients")
public class PatientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String fullName;
    public String email;
    public String phone;
    public String insuranceProvider;
    public String insuranceNumber;
    public boolean insuranceActive;
}
