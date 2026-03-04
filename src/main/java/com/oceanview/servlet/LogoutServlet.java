package com.oceanview.servlet;

// @WebServlet annotation tells Tomcat what URL this servlet responds to
import javax.servlet.annotation.WebServlet;
// Importing all the HTTP servlet classes we need (HttpServlet, HttpServletRequest, HttpSession, etc.)
import javax.servlet.http.*;
// Importing IOException which is required when we write data to the response
import java.io.IOException;

/**
 * LogoutServlet - This servlet handles logging the user out.
 * When the user clicks "Logout", the browser sends a GET request to "/logout".
 * This servlet destroys the user's session (so the server forgets they were logged in)
 * and then redirects them back to the login page.
 */
// @WebServlet annotation maps this servlet to the "/logout" URL path
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    /**
     * This method runs when the browser sends a GET request (i.e., when the user clicks the logout link).
     * It invalidates the session and redirects the user to the home/login page.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Try to get the existing session (false = don't create a new one if none exists)
        HttpSession session = req.getSession(false);

        // If a session exists, invalidate it (destroy it) so the user is fully logged out
        if (session != null) {
            session.invalidate();
        }

        // Redirect the user back to the login page (index.html)
        // getContextPath() gives us the base URL of our web app (e.g., "/oceanview")
        resp.sendRedirect(req.getContextPath() + "/index.html");
    }
}
