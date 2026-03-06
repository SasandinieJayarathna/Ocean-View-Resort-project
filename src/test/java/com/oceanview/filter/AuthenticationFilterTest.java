package com.oceanview.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

/**
 * AuthenticationFilterTest — Tests the Intercepting Filter pattern.
 * Verifies public paths, authenticated access, and unauthenticated redirects.
 */
@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain chain;
    @Mock private HttpSession session;
    @Mock private FilterConfig filterConfig;

    private AuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new AuthenticationFilter();
        filter.init(filterConfig);
        lenient().when(request.getContextPath()).thenReturn("/app");
    }

    @Test @DisplayName("TC-AF001: Root path passes through without auth")
    void rootPathPublic() throws Exception {
        when(request.getRequestURI()).thenReturn("/app/");
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    @Test @DisplayName("TC-AF002: Login page passes through without auth")
    void loginPagePublic() throws Exception {
        when(request.getRequestURI()).thenReturn("/app/index.html");
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    @Test @DisplayName("TC-AF003: Login endpoint passes through")
    void loginEndpointPublic() throws Exception {
        when(request.getRequestURI()).thenReturn("/app/login");
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    @Test @DisplayName("TC-AF004: CSS files pass through")
    void cssPublic() throws Exception {
        when(request.getRequestURI()).thenReturn("/app/css/style.css");
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    @Test @DisplayName("TC-AF005: JS files pass through")
    void jsPublic() throws Exception {
        when(request.getRequestURI()).thenReturn("/app/js/app.js");
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    @Test @DisplayName("TC-AF006: Error pages pass through")
    void errorPagesPublic() throws Exception {
        when(request.getRequestURI()).thenReturn("/app/error/404.html");
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    @Test @DisplayName("TC-AF007: Authenticated user passes through to protected page")
    void authenticatedUserPasses() throws Exception {
        when(request.getRequestURI()).thenReturn("/app/dashboard.html");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(new Object());

        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    @Test @DisplayName("TC-AF008: Unauthenticated API call returns 401 JSON")
    void unauthenticatedApiReturns401() throws Exception {
        when(request.getRequestURI()).thenReturn("/app/api/reservations");
        when(request.getSession(false)).thenReturn(null);
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        filter.doFilter(request, response, chain);

        verify(response).setStatus(401);
        verify(chain, never()).doFilter(request, response);
    }

    @Test @DisplayName("TC-AF009: Unauthenticated page request redirects to login")
    void unauthenticatedPageRedirects() throws Exception {
        when(request.getRequestURI()).thenReturn("/app/dashboard.html");
        when(request.getSession(false)).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/app/index.html");
        verify(chain, never()).doFilter(request, response);
    }

    @Test @DisplayName("TC-AF010: Session exists but no user attribute redirects")
    void sessionWithoutUserRedirects() throws Exception {
        when(request.getRequestURI()).thenReturn("/app/dashboard.html");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/app/index.html");
    }

    @Test @DisplayName("TC-AF011: Images path passes through")
    void imagesPublic() throws Exception {
        when(request.getRequestURI()).thenReturn("/app/images/logo.png");
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    @Test @DisplayName("TC-AF012: Unauthenticated API with session but no user")
    void apiSessionNoUser() throws Exception {
        when(request.getRequestURI()).thenReturn("/app/api/dashboard");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        filter.doFilter(request, response, chain);
        verify(response).setStatus(401);
    }

    @Test @DisplayName("TC-AF013: Destroy does nothing (coverage)")
    void destroyDoesNothing() {
        filter.destroy();
        // No exception = pass
    }
}
