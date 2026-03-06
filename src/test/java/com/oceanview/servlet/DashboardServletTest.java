package com.oceanview.servlet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DashboardServletTest — Tests DashboardServlet structure and annotations.
 * Direct doGet testing requires DB; these tests validate class structure.
 */
class DashboardServletTest {

    @Test @DisplayName("TC-DS001: DashboardServlet has WebServlet annotation")
    void hasWebServletAnnotation() {
        javax.servlet.annotation.WebServlet annotation =
                DashboardServlet.class.getAnnotation(javax.servlet.annotation.WebServlet.class);
        assertNotNull(annotation);
        assertEquals("/api/dashboard", annotation.value()[0]);
    }

    @Test @DisplayName("TC-DS002: DashboardServlet extends HttpServlet")
    void extendsHttpServlet() {
        assertTrue(new DashboardServlet() instanceof javax.servlet.http.HttpServlet);
    }

    @Test @DisplayName("TC-DS003: RoomServlet has WebServlet annotation")
    void roomServletAnnotation() {
        javax.servlet.annotation.WebServlet annotation =
                RoomServlet.class.getAnnotation(javax.servlet.annotation.WebServlet.class);
        assertNotNull(annotation);
        assertEquals("/api/rooms", annotation.value()[0]);
    }

    @Test @DisplayName("TC-DS004: RoomServlet extends HttpServlet")
    void roomServletExtendsHttpServlet() {
        assertTrue(new RoomServlet() instanceof javax.servlet.http.HttpServlet);
    }
}
