package com.oceanview.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

// This is the Intercepting Filter pattern - it checks EVERY web request before it reaches our servlets
// Think of it like a security guard at the door - it checks if you are logged in before letting you through
// @WebFilter("/*") means this filter runs for ALL URLs in our application
@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    // This method is called automatically for every incoming web request
    // "chain.doFilter()" means "let the request continue to the next step (the servlet)"
    // If we don't call chain.doFilter(), the request is blocked and never reaches the servlet
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // We cast to Http versions so we can access HTTP-specific features like sessions and URLs
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Get just the path part of the URL (without the application name)
        // For example, if the full URL is "/oceanview/login", this gives us "/login"
        String path = req.getRequestURI().substring(req.getContextPath().length());

        // Some pages should be accessible WITHOUT logging in (public pages)
        // These include: the home page, login page, CSS/JS files, images, and error pages
        boolean isPublic = path.equals("/") || path.equals("/index.html")
                || path.equals("/login") || path.startsWith("/css/")
                || path.startsWith("/js/") || path.startsWith("/images/")
                || path.startsWith("/error/");

        // If the page is public, let the request through without checking authentication
        if (isPublic) {
            chain.doFilter(request, response);
            return;  // Stop here - no need to check login for public pages
        }

        // For non-public pages, check if the user has a valid session with a "user" attribute
        // getSession(false) means "get the existing session, but don't create a new one"
        HttpSession session = req.getSession(false);

        if (session != null && session.getAttribute("user") != null) {
            // User is logged in - let the request continue to the servlet
            chain.doFilter(request, response);
        } else {
            // User is NOT logged in - block the request
            if (path.startsWith("/api/")) {
                // For API calls (from JavaScript), send back a JSON error with 401 status
                res.setContentType("application/json");
                res.setStatus(401);  // 401 means "Unauthorized"
                res.getWriter().write("{\"error\":\"Not authenticated\"}");
            } else {
                // For regular page requests, redirect the user to the login page
                res.sendRedirect(req.getContextPath() + "/index.html");
            }
        }
    }

    // init() is called once when the filter is first loaded - we don't need any setup here
    @Override public void init(FilterConfig filterConfig) {}

    // destroy() is called when the filter is being removed - we don't need any cleanup here
    @Override public void destroy() {}
}
