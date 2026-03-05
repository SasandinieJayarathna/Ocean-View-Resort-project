package com.oceanview.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * AuthenticationFilter — Intercepting Filter pattern.
 * Checks EVERY request for a valid session. Allows login page and static resources through.
 * PATTERN: Intercepting Filter — consistent auth without code duplication.
 * SOLID: Single Responsibility — only handles auth checking.
 */
@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getRequestURI().substring(req.getContextPath().length());

        // Allow these through WITHOUT authentication
        boolean isPublic = path.equals("/") || path.equals("/index.html")
                || path.equals("/login") || path.startsWith("/css/")
                || path.startsWith("/js/") || path.startsWith("/images/")
                || path.startsWith("/error/");

        if (isPublic) {
            chain.doFilter(request, response);
            return;
        }

        // Check for valid session
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            chain.doFilter(request, response);
        } else {
            // API calls get JSON error, pages get redirected
            if (path.startsWith("/api/")) {
                res.setContentType("application/json");
                res.setStatus(401);
                res.getWriter().write("{\"error\":\"Not authenticated\"}");
            } else {
                res.sendRedirect(req.getContextPath() + "/index.html");
            }
        }
    }

    @Override public void init(FilterConfig filterConfig) {}
    @Override public void destroy() {}
}
