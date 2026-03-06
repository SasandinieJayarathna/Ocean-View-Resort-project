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
 * HelpServletTest — Tests HelpServlet doGet.
 * HelpServlet has no DB dependency so it can be fully tested.
 */
@ExtendWith(MockitoExtension.class)
class HelpServletTest {
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;

    @Test @DisplayName("TC-HS001: doGet returns JSON array of help sections")
    void doGetReturnsHelpSections() throws Exception {
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        HelpServlet servlet = new HelpServlet();
        servlet.doGet(request, response);

        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");

        String json = sw.toString();
        assertTrue(json.contains("How to Login"));
        assertTrue(json.contains("How to Add a Reservation"));
        assertTrue(json.contains("How to View Reservations"));
        assertTrue(json.contains("How to Generate a Bill"));
        assertTrue(json.contains("How to View Reports"));
        assertTrue(json.contains("System FAQ"));
    }

    @Test @DisplayName("TC-HS002: Help sections contain expected content")
    void helpSectionsHaveContent() throws Exception {
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        HelpServlet servlet = new HelpServlet();
        servlet.doGet(request, response);

        String json = sw.toString();
        assertTrue(json.contains("username and password"));
        assertTrue(json.contains("billing strategy"));
        assertTrue(json.contains("Admin users only"));
    }

    @Test @DisplayName("TC-HS003: HelpServlet has WebServlet annotation")
    void hasWebServletAnnotation() {
        javax.servlet.annotation.WebServlet annotation =
                HelpServlet.class.getAnnotation(javax.servlet.annotation.WebServlet.class);
        assertNotNull(annotation);
        assertEquals("/api/help", annotation.value()[0]);
    }

    @Test @DisplayName("TC-HS004: HelpServlet extends HttpServlet")
    void extendsHttpServlet() {
        HelpServlet servlet = new HelpServlet();
        assertTrue(servlet instanceof javax.servlet.http.HttpServlet);
    }
}
