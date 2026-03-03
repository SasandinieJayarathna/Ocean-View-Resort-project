package com.oceanview.util;

import java.util.concurrent.atomic.AtomicInteger;

/** Thread-safe generator for unique reservation numbers. Format: RES-XXXXXX. */
public class ReservationNumberGenerator {
    private static final AtomicInteger counter = new AtomicInteger(100000);

    public static String generateNext() {
        return String.format("RES-%06d", counter.incrementAndGet());
    }
}
