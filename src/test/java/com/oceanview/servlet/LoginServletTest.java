package com.oceanview.servlet;

import com.oceanview.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LoginServletTest — Verifies login logic indirectly via AuthService.
 * Direct servlet testing requires DB; these tests validate the auth layer
 * that the servlet delegates to (tested with mocks in AuthServiceTest).
 */
@ExtendWith(MockitoExtension.class)
class LoginServletTest {
    @Mock private com.oceanview.dao.UserDAO userDAO;

    @Test @DisplayName("TC-SL001: AuthService returns null for null credentials")
    void nullCredentials() {
        AuthService auth = new AuthService(userDAO);
        assertNull(auth.authenticate(null, null));
    }

    @Test @DisplayName("TC-SL002: AuthService returns null for empty username")
    void emptyUsername() {
        AuthService auth = new AuthService(userDAO);
        assertNull(auth.authenticate(null, "password"));
    }

    @Test @DisplayName("TC-SL003: AuthService returns null for null password")
    void nullPassword() {
        AuthService auth = new AuthService(userDAO);
        assertNull(auth.authenticate("admin", null));
    }

    @Test @DisplayName("TC-SL004: LoginServlet class exists and extends HttpServlet")
    void servletExists() {
        LoginServlet servlet = new LoginServlet();
        assertTrue(servlet instanceof javax.servlet.http.HttpServlet);
    }

    @Test @DisplayName("TC-SL005: LoginServlet has WebServlet annotation")
    void hasWebServletAnnotation() {
        javax.servlet.annotation.WebServlet annotation =
                LoginServlet.class.getAnnotation(javax.servlet.annotation.WebServlet.class);
        assertNotNull(annotation);
        assertEquals("/login", annotation.value()[0]);
    }
}
