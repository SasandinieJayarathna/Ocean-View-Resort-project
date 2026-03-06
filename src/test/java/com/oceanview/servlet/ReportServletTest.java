package com.oceanview.servlet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ReportServletTest — Tests ReportServlet access control and validation.
 * Tests paths that occur before any DB/service call.
 */
@ExtendWith(MockitoExtension.class)
class ReportServletTest {
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;

    @Test @DisplayName("TC-RP001: doGet returns 403 when no session")
    void doGetNoSession() throws Exception {
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
        when(request.getSession(false)).thenReturn(null);

        ReportServlet servlet = new ReportServlet();
        servlet.doGet(request, response);

        verify(response).setStatus(403);
        assertTrue(sw.toString().contains("Admin access required"));
    }

    @Test @DisplayName("TC-RP002: doGet returns 403 for non-admin user")
    void doGetNonAdmin() throws Exception {
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("role")).thenReturn("STAFF");

        ReportServlet servlet = new ReportServlet();
        servlet.doGet(request, response);

        verify(response).setStatus(403);
        assertTrue(sw.toString().contains("Admin access required"));
    }

    @Test @DisplayName("TC-RP003: doGet returns 400 for missing parameters")
    void doGetMissingParams() throws Exception {
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("role")).thenReturn("ADMIN");
        when(request.getParameter("type")).thenReturn(null);
        when(request.getParameter("startDate")).thenReturn(null);
        when(request.getParameter("endDate")).thenReturn(null);

        ReportServlet servlet = new ReportServlet();
        servlet.doGet(request, response);

        verify(response).setStatus(400);
        assertTrue(sw.toString().contains("Missing required parameters"));
    }

    @Test @DisplayName("TC-RP004: ReportServlet has WebServlet annotation")
    void hasWebServletAnnotation() {
        javax.servlet.annotation.WebServlet annotation =
                ReportServlet.class.getAnnotation(javax.servlet.annotation.WebServlet.class);
        assertNotNull(annotation);
        assertEquals("/api/reports", annotation.value()[0]);
    }

    @Test @DisplayName("TC-RP005: ReportServlet extends HttpServlet")
    void extendsHttpServlet() {
        assertTrue(new ReportServlet() instanceof javax.servlet.http.HttpServlet);
    }
}
