package com.clinicbooking.patient.domain;

public enum BlockKind {
    /** Bloqueo administrativo (urgencia, capacitación, ausencia). */
    BLOCKED,
    /** Cupo ocupado por una cita del appointment-service. */
    BOOKED
}
