package com.oceanview.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/** Comprehensive tests for Room hierarchy — Standard, Deluxe, Suite. */
class RoomModelTest {
    @Test @DisplayName("TC-RM001: StandardRoom type is STANDARD")
    void standardRoomType() {
        StandardRoom r = new StandardRoom();
        assertEquals("STANDARD", r.getRoomType());
        assertTrue(r.isAvailable());
    }

    @Test @DisplayName("TC-RM002: DeluxeRoom type is DELUXE")
    void deluxeRoomType() {
        DeluxeRoom r = new DeluxeRoom();
        assertEquals("DELUXE", r.getRoomType());
    }

    @Test @DisplayName("TC-RM003: SuiteRoom type is SUITE")
    void suiteRoomType() {
        SuiteRoom r = new SuiteRoom();
        assertEquals("SUITE", r.getRoomType());
    }

    @Test @DisplayName("TC-RM004: Room setters and getters")
    void roomSettersGetters() {
        StandardRoom r = new StandardRoom();
        r.setRoomId(1);
        r.setRoomNumber("101");
        r.setPricePerNight(5000.0);
        r.setAvailable(false);
        r.setDescription("Garden view");
        r.setMaxOccupancy(3);

        assertEquals(1, r.getRoomId());
        assertEquals("101", r.getRoomNumber());
        assertEquals(5000.0, r.getPricePerNight());
        assertFalse(r.isAvailable());
        assertEquals("Garden view", r.getDescription());
        assertEquals(3, r.getMaxOccupancy());
    }

    @Test @DisplayName("TC-RM005: Room toString contains info")
    void roomToString() {
        StandardRoom r = new StandardRoom();
        r.setRoomNumber("101");
        r.setPricePerNight(5000);
        String str = r.toString();
        assertTrue(str.contains("101"));
        assertTrue(str.contains("5000"));
    }

    @Test @DisplayName("TC-RM006: Room constructor with params")
    void roomConstructorParams() {
        StandardRoom r = new StandardRoom();
        r.setRoomNumber("201");
        r.setRoomType("STANDARD");
        r.setPricePerNight(5000);
        r.setDescription("Test room");
        r.setMaxOccupancy(2);
        assertEquals("201", r.getRoomNumber());
        assertEquals(2, r.getMaxOccupancy());
    }

    @Test @DisplayName("TC-RM007: All room types are instances of Room")
    void allRoomTypesAreRoom() {
        assertTrue(new StandardRoom() instanceof Room);
        assertTrue(new DeluxeRoom() instanceof Room);
        assertTrue(new SuiteRoom() instanceof Room);
    }

    @Test @DisplayName("TC-RM008: Room toString contains class name and type")
    void roomToStringContainsClassName() {
        DeluxeRoom r = new DeluxeRoom();
        r.setRoomNumber("301");
        r.setPricePerNight(12000);
        String str = r.toString();
        assertTrue(str.contains("DeluxeRoom"));
        assertTrue(str.contains("DELUXE"));
        assertTrue(str.contains("301"));
    }

    @Test @DisplayName("TC-RM009: SuiteRoom toString")
    void suiteToString() {
        SuiteRoom r = new SuiteRoom();
        r.setRoomNumber("401");
        r.setPricePerNight(20000);
        String str = r.toString();
        assertTrue(str.contains("SuiteRoom"));
        assertTrue(str.contains("SUITE"));
    }
}
