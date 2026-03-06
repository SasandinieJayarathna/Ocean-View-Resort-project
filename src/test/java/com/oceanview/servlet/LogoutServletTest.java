package com.oceanview.servlet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * LogoutServletTest — Tests LogoutServlet session invalidation and redirect.
 */
@ExtendWith(MockitoExtension.class)
class LogoutServletTest {
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;

    @Test @DisplayName("TC-LO001: doGet invalidates session and redirects")
    void doGetWithSession() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(request.getContextPath()).thenReturn("/app");

        LogoutServlet servlet = new LogoutServlet();
        servlet.doGet(request, response);

        verify(session).invalidate();
        verify(response).sendRedirect("/app/index.html");
    }

    @Test @DisplayName("TC-LO002: doGet handles null session gracefully")
    void doGetWithoutSession() throws Exception {
        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("/app");

        LogoutServlet servlet = new LogoutServlet();
        servlet.doGet(request, response);

        verify(response).sendRedirect("/app/index.html");
    }

    @Test @DisplayName("TC-LO003: LogoutServlet has WebServlet annotation")
    void hasWebServletAnnotation() {
        javax.servlet.annotation.WebServlet annotation =
                LogoutServlet.class.getAnnotation(javax.servlet.annotation.WebServlet.class);
        assertNotNull(annotation);
        assertEquals("/logout", annotation.value()[0]);
    }

    @Test @DisplayName("TC-LO004: LogoutServlet extends HttpServlet")
    void extendsHttpServlet() {
        assertTrue(new LogoutServlet() instanceof javax.servlet.http.HttpServlet);
    }
}
