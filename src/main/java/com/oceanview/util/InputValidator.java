package com.oceanview.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * InputValidator — Centralized input validation utility.
 * SOLID: Single Responsibility — only validates input.
 * DRY: All validation in one place. No duplication across servlets.
 */
public class InputValidator {
    private static final Pattern PHONE  = Pattern.compile("^\\+?[0-9]{7,15}$");
    private static final Pattern EMAIL  = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern NAME   = Pattern.compile("^[A-Za-z\\s'\\-]{2,100}$");
    private static final Pattern RESNUM = Pattern.compile("^RES-\\d{6}$");

    public static boolean isNullOrEmpty(String v) { return v == null || v.trim().isEmpty(); }
    public static boolean isValidPhone(String p) { return !isNullOrEmpty(p) && PHONE.matcher(p.trim()).matches(); }
    public static boolean isValidEmail(String e) { return !isNullOrEmpty(e) && EMAIL.matcher(e.trim()).matches(); }
    public static boolean isValidName(String n) { return !isNullOrEmpty(n) && NAME.matcher(n.trim()).matches(); }

    public static boolean isValidFutureDate(String d) {
        try { return !LocalDate.parse(d).isBefore(LocalDate.now()); }
        catch (DateTimeParseException e) { return false; }
    }

    public static boolean isValidDateRange(String in, String out) {
        try { return LocalDate.parse(out).isAfter(LocalDate.parse(in)); }
        catch (DateTimeParseException e) { return false; }
    }

    public static boolean isValidReservationNumber(String r) {
        return !isNullOrEmpty(r) && RESNUM.matcher(r.trim()).matches();
    }
}
