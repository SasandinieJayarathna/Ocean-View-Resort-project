package com.oceanview.pattern;

import com.oceanview.model.*;
import com.oceanview.pattern.factory.RoomFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/** TDD tests for Factory pattern. */
class RoomFactoryTest {
    @Test @DisplayName("TC-F001: Creates StandardRoom") void std() { assertTrue(RoomFactory.createRoom("STANDARD") instanceof StandardRoom); }
    @Test @DisplayName("TC-F002: Creates DeluxeRoom") void dlx() { assertTrue(RoomFactory.createRoom("DELUXE") instanceof DeluxeRoom); }
    @Test @DisplayName("TC-F003: Creates SuiteRoom") void ste() { assertTrue(RoomFactory.createRoom("SUITE") instanceof SuiteRoom); }
    @Test @DisplayName("TC-F004: Null throws exception") void nul() { assertThrows(IllegalArgumentException.class, () -> RoomFactory.createRoom(null)); }
    @Test @DisplayName("TC-F005: Unknown type throws") void unk() { assertThrows(IllegalArgumentException.class, () -> RoomFactory.createRoom("TENT")); }
    @Test @DisplayName("TC-F006: Handles lowercase") void lower() { assertTrue(RoomFactory.createRoom("deluxe") instanceof DeluxeRoom); }
}
