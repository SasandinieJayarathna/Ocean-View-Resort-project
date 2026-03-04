package com.oceanview.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

// This utility class handles all input validation in one place
// Instead of checking if an email is valid in every servlet, we just call InputValidator.isValidEmail()
// All methods are static so we don't need to create an object - just call them directly
public class InputValidator {

    // These are regex (Regular Expression) patterns - they define what valid input looks like
    // Pattern.compile() creates a reusable pattern object so we don't re-compile it every time
    private static final Pattern PHONE  = Pattern.compile("^\\+?[0-9]{7,15}$");         // Phone: optional + then 7-15 digits
    private static final Pattern EMAIL  = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");  // Email: something@something
    private static final Pattern NAME   = Pattern.compile("^[A-Za-z\\s'\\-]{2,100}$");   // Name: 2-100 letters, spaces, apostrophes, hyphens
    private static final Pattern RESNUM = Pattern.compile("^RES-\\d{6}$");               // Reservation number: RES- followed by 6 digits

    // Check if a string is null or empty (after trimming whitespace)
    public static boolean isNullOrEmpty(String v) { return v == null || v.trim().isEmpty(); }

    // Check if a phone number matches our expected format
    public static boolean isValidPhone(String p) { return !isNullOrEmpty(p) && PHONE.matcher(p.trim()).matches(); }

    // Check if an email address matches our expected format
    public static boolean isValidEmail(String e) { return !isNullOrEmpty(e) && EMAIL.matcher(e.trim()).matches(); }

    // Check if a name only contains allowed characters and is the right length
    public static boolean isValidName(String n) { return !isNullOrEmpty(n) && NAME.matcher(n.trim()).matches(); }

    // Check if a date string is valid and is today or in the future (not in the past)
    // We use try-catch because LocalDate.parse() throws an exception if the date format is wrong
    public static boolean isValidFutureDate(String d) {
        try { return !LocalDate.parse(d).isBefore(LocalDate.now()); }
        catch (DateTimeParseException e) { return false; }  // If the date can't be parsed, it's not valid
    }

    // Check if check-out date is after check-in date (you can't check out before you check in!)
    public static boolean isValidDateRange(String in, String out) {
        try { return LocalDate.parse(out).isAfter(LocalDate.parse(in)); }
        catch (DateTimeParseException e) { return false; }  // If either date is invalid, return false
    }

    // Check if a reservation number matches our format: RES- followed by exactly 6 digits
    public static boolean isValidReservationNumber(String r) {
        return !isNullOrEmpty(r) && RESNUM.matcher(r.trim()).matches();
    }
}
