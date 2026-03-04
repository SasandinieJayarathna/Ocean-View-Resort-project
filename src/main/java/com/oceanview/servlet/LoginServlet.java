package com.oceanview.servlet;

// Importing the DAO class that talks to the database to find user records
import com.oceanview.dao.UserDAOImpl;
// Importing the User model class that represents a user in our system
import com.oceanview.model.User;
// Importing the AuthService which handles checking if a username/password is correct
import com.oceanview.service.AuthService;
// Importing Gson's JsonObject so we can build a JSON response to send back to the browser
import com.google.gson.JsonObject;
// @WebServlet annotation tells Tomcat what URL this servlet responds to
import javax.servlet.annotation.WebServlet;
// Importing all the HTTP servlet classes we need (HttpServlet, HttpServletRequest, etc.)
import javax.servlet.http.*;
// Importing IOException which is required when we write data to the response
import java.io.IOException;

/**
 * LoginServlet - This servlet handles the login form submission.
 * When a user types their username and password and clicks "Login",
 * the browser sends a POST request to "/login" and this servlet processes it.
 * It checks the credentials against the database and returns a JSON response
 * telling the browser if the login was successful or not.
 */
// @WebServlet annotation maps this servlet to the "/login" URL path
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    /**
     * This method runs when the browser sends a POST request (i.e., when the login form is submitted).
     * It reads the username and password, checks them, and sends back a JSON response.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // We set the response type to JSON so the browser knows what format to expect
        resp.setContentType("application/json");
        // Set character encoding to UTF-8 so special characters display correctly
        resp.setCharacterEncoding("UTF-8");

        // Get the username from the form data that was sent
        String username = req.getParameter("username");
        // Get the password from the form data that was sent
        String password = req.getParameter("password");

        // Create the AuthService and give it a UserDAOImpl so it can look up users in the database
        AuthService authService = new AuthService(new UserDAOImpl());
        // Try to authenticate the user - this returns a User object if successful, or null if not
        User user = authService.authenticate(username, password);

        // Create a JSON object to build our response
        JsonObject json = new JsonObject();

        // If the user is authenticated (not null), create a session to remember they're logged in
        if (user != null) {
            // Create a new HTTP session (true = create one if it doesn't exist yet)
            // A session is like a "memory" on the server that remembers this user is logged in
            HttpSession session = req.getSession(true);
            // Store the full User object in the session so we can access it later
            session.setAttribute("user", user);
            // Store the user's ID in the session for quick access
            session.setAttribute("userId", user.getUserId());
            // Store the username in the session
            session.setAttribute("username", user.getUsername());
            // Store the user's full name in the session (for display on the dashboard)
            session.setAttribute("fullName", user.getFullName());
            // Store the user's role (e.g., "ADMIN" or "STAFF") in the session
            session.setAttribute("role", user.getRole());

            // Return a JSON response telling the browser that login was successful
            json.addProperty("success", true);
            // Include the user's full name so the frontend can display a welcome message
            json.addProperty("fullName", user.getFullName());
            // Include the user's role so the frontend knows what pages to show
            json.addProperty("role", user.getRole());
        } else {
            // If authentication failed, set HTTP status 401 (Unauthorized)
            resp.setStatus(401);
            // Tell the browser that login failed
            json.addProperty("success", false);
            // Include an error message explaining what went wrong
            json.addProperty("error", "Invalid username or password");
        }

        // Write the JSON response to the output stream so the browser receives it
        resp.getWriter().write(json.toString());
    }
}
