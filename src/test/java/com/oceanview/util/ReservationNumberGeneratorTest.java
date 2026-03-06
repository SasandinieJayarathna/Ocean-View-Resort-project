package com.oceanview.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ReservationNumberGeneratorTest — Tests thread-safe reservation number generation.
 * Verifies format RES-XXXXXX and sequential uniqueness.
 */
class ReservationNumberGeneratorTest {

    @Test @DisplayName("TC-RG001: Generated number starts with RES-")
    void generatedNumberFormat() {
        String number = ReservationNumberGenerator.generateNext();
        assertTrue(number.startsWith("RES-"));
        assertEquals(10, number.length()); // RES-XXXXXX
    }

    @Test @DisplayName("TC-RG002: Sequential numbers are unique")
    void sequentialUnique() {
        String first = ReservationNumberGenerator.generateNext();
        String second = ReservationNumberGenerator.generateNext();
        assertNotEquals(first, second);
    }

    @Test @DisplayName("TC-RG003: Generated number matches expected pattern")
    void matchesPattern() {
        String number = ReservationNumberGenerator.generateNext();
        assertTrue(number.matches("RES-\\d{6}"));
    }
}
