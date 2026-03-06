package com.oceanview.servlet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * BillingServletTest — Tests BillingServlet validation paths.
 * Tests error responses that occur before any DAO/DB call.
 */
@ExtendWith(MockitoExtension.class)
class BillingServletTest {
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;

    @Test @DisplayName("TC-BV001: doPost returns 400 for null reservationId")
    void doPostNullReservationId() throws Exception {
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
        when(request.getParameter("reservationId")).thenReturn(null);

        BillingServlet servlet = new BillingServlet();
        servlet.doPost(request, response);

        verify(response).setStatus(400);
        assertTrue(sw.toString().contains("Reservation ID required"));
    }

    @Test @DisplayName("TC-BV002: doPost returns 400 for empty reservationId")
    void doPostEmptyReservationId() throws Exception {
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
        when(request.getParameter("reservationId")).thenReturn("");

        BillingServlet servlet = new BillingServlet();
        servlet.doPost(request, response);

        verify(response).setStatus(400);
        assertTrue(sw.toString().contains("Reservation ID required"));
    }

    @Test @DisplayName("TC-BV003: doGet returns 400 for null reservationId")
    void doGetNullReservationId() throws Exception {
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
        when(request.getParameter("reservationId")).thenReturn(null);

        BillingServlet servlet = new BillingServlet();
        servlet.doGet(request, response);

        verify(response).setStatus(400);
        assertTrue(sw.toString().contains("Reservation ID required"));
    }

    @Test @DisplayName("TC-BV004: doGet returns 400 for empty reservationId")
    void doGetEmptyReservationId() throws Exception {
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
        when(request.getParameter("reservationId")).thenReturn("");

        BillingServlet servlet = new BillingServlet();
        servlet.doGet(request, response);

        verify(response).setStatus(400);
        assertTrue(sw.toString().contains("Reservation ID required"));
    }

    @Test @DisplayName("TC-BV005: BillingServlet has WebServlet annotation")
    void hasWebServletAnnotation() {
        javax.servlet.annotation.WebServlet annotation =
                BillingServlet.class.getAnnotation(javax.servlet.annotation.WebServlet.class);
        assertNotNull(annotation);
        assertEquals("/api/billing", annotation.value()[0]);
    }

    @Test @DisplayName("TC-BV006: BillingServlet extends HttpServlet")
    void extendsHttpServlet() {
        assertTrue(new BillingServlet() instanceof javax.servlet.http.HttpServlet);
    }
}
