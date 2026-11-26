package com.clinicbooking.patient.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PatientTest {

    private Patient patient(String provider, String number, boolean active) {
        return new Patient(1L, "Ana", "ana@mail.com", "3001112233", provider, number, active);
    }

    @Test
    void activeCoverage() {
        InsuranceCoverage c = patient("Sanitas", "ABC12345", true).verifyCoverage();
        assertTrue(c.covered());
        assertEquals("ACTIVE", c.status());
    }

    @Test
    void noAffiliation() {
        assertEquals("NO_AFFILIATION", patient(null, null, true).verifyCoverage().status());
        assertEquals("NO_AFFILIATION", patient("Sanitas", " ", true).verifyCoverage().status());
    }

    @Test
    void invalidNumber() {
        assertEquals("INVALID_NUMBER", patient("Sanitas", "12", true).verifyCoverage().status());
    }

    @Test
    void inactiveCoverage() {
        InsuranceCoverage c = patient("Sanitas", "ABC12345", false).verifyCoverage();
        assertFalse(c.covered());
        assertEquals("INACTIVE", c.status());
    }

    @Test
    void validatesNameAndEmail() {
        assertThrows(DomainException.class, () -> new Patient(null, "", "a@b.com", null, null, null, true));
        assertThrows(DomainException.class, () -> new Patient(null, "Ana", "sin-arroba", null, null, null, true));
        assertEquals(7L, patient("S", "ABC12345", true).withId(7L).id());
    }
}
