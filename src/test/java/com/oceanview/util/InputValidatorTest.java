package com.oceanview.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/** TDD tests for InputValidator — happy paths AND corner cases. */
class InputValidatorTest {
    @Test @DisplayName("TC-V001: Valid intl phone") void validIntlPhone() { assertTrue(InputValidator.isValidPhone("+94771234567")); }
    @Test @DisplayName("TC-V002: Valid local phone") void validLocalPhone() { assertTrue(InputValidator.isValidPhone("0771234567")); }
    @Test @DisplayName("TC-V003: Valid email") void validEmail() { assertTrue(InputValidator.isValidEmail("guest@hotel.com")); }
    @Test @DisplayName("TC-V004: Valid name") void validName() { assertTrue(InputValidator.isValidName("John Smith")); }
    @Test @DisplayName("TC-V005: Valid date range") void validDates() { assertTrue(InputValidator.isValidDateRange("2025-08-01", "2025-08-05")); }
    @Test @DisplayName("TC-V006: Valid reservation number") void validRes() { assertTrue(InputValidator.isValidReservationNumber("RES-100001")); }
    @Test @DisplayName("TC-V007: Null is empty") void nullEmpty() { assertTrue(InputValidator.isNullOrEmpty(null)); }
    @Test @DisplayName("TC-V008: Whitespace is empty") void wsEmpty() { assertTrue(InputValidator.isNullOrEmpty("   ")); }
    @Test @DisplayName("TC-V009: Short phone rejected") void shortPhone() { assertFalse(InputValidator.isValidPhone("123")); }
    @Test @DisplayName("TC-V010: Letters in phone rejected") void lettersPhone() { assertFalse(InputValidator.isValidPhone("077abc1234")); }
    @Test @DisplayName("TC-V011: Email without @ rejected") void noAtEmail() { assertFalse(InputValidator.isValidEmail("abc.com")); }
    @Test @DisplayName("TC-V012: Null email rejected") void nullEmail() { assertFalse(InputValidator.isValidEmail(null)); }
    @Test @DisplayName("TC-V013: Reversed dates rejected") void reversedDates() { assertFalse(InputValidator.isValidDateRange("2025-08-05", "2025-08-01")); }
    @Test @DisplayName("TC-V014: Same-day checkout rejected") void sameDay() { assertFalse(InputValidator.isValidDateRange("2025-08-01", "2025-08-01")); }
    @Test @DisplayName("TC-V015: Bad reservation format") void badRes() { assertFalse(InputValidator.isValidReservationNumber("BOOK-001")); }
    @Test @DisplayName("TC-V016: Hyphenated name valid") void hyphenName() { assertTrue(InputValidator.isValidName("Anne-Marie")); }
    @Test @DisplayName("TC-V017: Apostrophe name valid") void apoName() { assertTrue(InputValidator.isValidName("O'Brien")); }
}
