package com.oceanview.util;

import java.util.concurrent.atomic.AtomicInteger;

// This class generates unique reservation numbers like RES-100001, RES-100002, etc.
// It is thread-safe, meaning even if multiple users make reservations at the same time,
// each one will get a different number - no duplicates
public class ReservationNumberGenerator {

    // AtomicInteger is a special integer that is safe to use from multiple threads at once
    // We start the counter at 100000 so the first reservation number will be RES-100001
    private static final AtomicInteger counter = new AtomicInteger(100000);

    // Generate the next reservation number
    // incrementAndGet() adds 1 to the counter and returns the new value, all in one safe step
    // String.format("RES-%06d", ...) formats the number with leading zeros to always have 6 digits
    public static String generateNext() {
        return String.format("RES-%06d", counter.incrementAndGet());
    }
}
