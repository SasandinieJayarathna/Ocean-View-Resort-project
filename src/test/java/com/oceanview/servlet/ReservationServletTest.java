package com.oceanview.servlet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ReservationServletTest — Tests validation paths in ReservationServlet.
 * Tests error responses that occur BEFORE any DAO/DB call.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReservationServletTest {
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;

    private StringWriter setupWriter() throws Exception {
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
        return sw;
    }

    @Test @DisplayName("TC-RS001: doPost returns 400 for invalid guest name")
    void doPostInvalidName() throws Exception {
        StringWriter sw = setupWriter();
        when(request.getParameter("guestName")).thenReturn("");

        ReservationServlet servlet = new ReservationServlet();
        servlet.doPost(request, response);

        verify(response).setStatus(400);
        assertTrue(sw.toString().contains("Invalid guest name"));
    }

    @Test @DisplayName("TC-RS002: doPost returns 400 for null guest name")
    void doPostNullName() throws Exception {
        StringWriter sw = setupWriter();
        when(request.getParameter("guestName")).thenReturn(null);

        ReservationServlet servlet = new ReservationServlet();
        servlet.doPost(request, response);

        verify(response).setStatus(400);
        assertTrue(sw.toString().contains("Invalid guest name"));
    }

    @Test @DisplayName("TC-RS003: doPost returns 400 for invalid phone")
    void doPostInvalidPhone() throws Exception {
        StringWriter sw = setupWriter();
        when(request.getParameter("guestName")).thenReturn("John Smith");
        when(request.getParameter("contactNumber")).thenReturn("invalid");

        ReservationServlet servlet = new ReservationServlet();
        servlet.doPost(request, response);

        verify(response).setStatus(400);
        assertTrue(sw.toString().contains("Invalid contact number"));
    }

    @Test @DisplayName("TC-RS004: doPost returns 400 for invalid email")
    void doPostInvalidEmail() throws Exception {
        StringWriter sw = setupWriter();
        when(request.getParameter("guestName")).thenReturn("John Smith");
        when(request.getParameter("contactNumber")).thenReturn("+94771234567");
        when(request.getParameter("guestEmail")).thenReturn("not-an-email");

        ReservationServlet servlet = new ReservationServlet();
        servlet.doPost(request, response);

        verify(response).setStatus(400);
        assertTrue(sw.toString().contains("Invalid email"));
    }

    @Test @DisplayName("TC-RS005: doPost returns 400 for invalid dates")
    void doPostInvalidDates() throws Exception {
        StringWriter sw = setupWriter();
        when(request.getParameter("guestName")).thenReturn("John Smith");
        when(request.getParameter("contactNumber")).thenReturn("+94771234567");
        when(request.getParameter("guestEmail")).thenReturn("john@test.com");
        when(request.getParameter("checkInDate")).thenReturn("2025-08-04");
        when(request.getParameter("checkOutDate")).thenReturn("2025-08-01");

        ReservationServlet servlet = new ReservationServlet();
        servlet.doPost(request, response);

        verify(response).setStatus(400);
        assertTrue(sw.toString().contains("Invalid date range"));
    }

    @Test @DisplayName("TC-RS006: doPost accepts empty email (optional)")
    void doPostEmptyEmailOk() throws Exception {
        StringWriter sw = setupWriter();
        when(request.getParameter("guestName")).thenReturn("John Smith");
        when(request.getParameter("contactNumber")).thenReturn("+94771234567");
        when(request.getParameter("guestEmail")).thenReturn("");
        when(request.getParameter("checkInDate")).thenReturn("2025-08-04");
        when(request.getParameter("checkOutDate")).thenReturn("2025-08-01");

        ReservationServlet servlet = new ReservationServlet();
        servlet.doPost(request, response);

        // Should pass email validation (empty is ok) but fail on dates
        verify(response).setStatus(400);
        assertTrue(sw.toString().contains("Invalid date range"));
    }

    @Test @DisplayName("TC-RS007: doPut returns 400 for missing reservation ID")
    void doPutMissingId() throws Exception {
        StringWriter sw = setupWriter();
        when(request.getParameter("reservationId")).thenReturn(null);

        ReservationServlet servlet = new ReservationServlet();
        servlet.doPut(request, response);

        verify(response).setStatus(400);
        assertTrue(sw.toString().contains("Reservation ID required"));
    }

    @Test @DisplayName("TC-RS008: doPut returns 400 for empty reservation ID")
    void doPutEmptyId() throws Exception {
        StringWriter sw = setupWriter();
        when(request.getParameter("reservationId")).thenReturn("");

        ReservationServlet servlet = new ReservationServlet();
        servlet.doPut(request, response);

        verify(response).setStatus(400);
        assertTrue(sw.toString().contains("Reservation ID required"));
    }

    @Test @DisplayName("TC-RS009: doDelete returns 400 for missing ID")
    void doDeleteMissingId() throws Exception {
        StringWriter sw = setupWriter();
        when(request.getParameter("id")).thenReturn(null);

        ReservationServlet servlet = new ReservationServlet();
        servlet.doDelete(request, response);

        verify(response).setStatus(400);
        assertTrue(sw.toString().contains("Reservation ID required"));
    }

    @Test @DisplayName("TC-RS010: doDelete returns 400 for empty ID")
    void doDeleteEmptyId() throws Exception {
        StringWriter sw = setupWriter();
        when(request.getParameter("id")).thenReturn("");

        ReservationServlet servlet = new ReservationServlet();
        servlet.doDelete(request, response);

        verify(response).setStatus(400);
        assertTrue(sw.toString().contains("Reservation ID required"));
    }

    @Test @DisplayName("TC-RS011: ReservationServlet has WebServlet annotation")
    void hasWebServletAnnotation() {
        javax.servlet.annotation.WebServlet annotation =
                ReservationServlet.class.getAnnotation(javax.servlet.annotation.WebServlet.class);
        assertNotNull(annotation);
        assertEquals("/api/reservations", annotation.value()[0]);
    }

    @Test @DisplayName("TC-RS012: ReservationServlet extends HttpServlet")
    void extendsHttpServlet() {
        assertTrue(new ReservationServlet() instanceof javax.servlet.http.HttpServlet);
    }
}
