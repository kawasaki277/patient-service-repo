package com.clinicbooking.patient.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

/** Médico con sus slots libres agrupados por día. */
public record DoctorAvailability(Doctor doctor, Map<LocalDate, List<LocalDateTime>> slotsByDate) {
}
