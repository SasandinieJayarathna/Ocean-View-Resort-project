package com.oceanview.servlet;

// Importing DAO classes that talk to the database for reservations and rooms
import com.oceanview.dao.ReservationDAOImpl;
import com.oceanview.dao.RoomDAOImpl;
// Importing model classes that represent reservations and rooms in our system
import com.oceanview.model.Reservation;
import com.oceanview.model.Room;
// Gson is a Google library that converts Java objects to JSON format
import com.google.gson.Gson;
// JsonArray lets us build a JSON array (a list of items in JSON format)
import com.google.gson.JsonArray;
// JsonObject lets us manually build a JSON object with key-value pairs
import com.google.gson.JsonObject;
// @WebServlet annotation tells Tomcat what URL this servlet responds to
import javax.servlet.annotation.WebServlet;
// Importing all the HTTP servlet classes we need (HttpServlet, HttpServletRequest, etc.)
import javax.servlet.http.*;
// Importing IOException which is required when we write data to the response
import java.io.IOException;
// Importing List to hold collections of rooms and reservations
import java.util.List;

/**
 * DashboardServlet - This servlet provides the data for the main dashboard page.
 * When the user opens the dashboard, the browser sends a GET request here, and
 * this servlet returns statistics like:
 *   - How many reservations are for today
 *   - How many rooms the hotel has and how many are available
 *   - Total number of reservations
 *   - The 5 most recent reservations
 *   - The logged-in user's name and role
 */
// @WebServlet annotation maps this servlet to the "/api/dashboard" URL path
@WebServlet("/api/dashboard")
public class DashboardServlet extends HttpServlet {

    // Gson is a Google library that converts Java objects to JSON format
    private final Gson gson = new Gson();

    /**
     * Handles GET requests - gathers all the dashboard statistics and returns them as JSON.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // We set the response type to JSON so the browser knows what format to expect
        resp.setContentType("application/json");
        // Set character encoding to UTF-8 so special characters display correctly
        resp.setCharacterEncoding("UTF-8");

        // Create DAO objects to access reservation and room data from the database
        ReservationDAOImpl reservationDAO = new ReservationDAOImpl();
        RoomDAOImpl roomDAO = new RoomDAOImpl();

        // ---- Gather all the dashboard statistics from the database ----

        // Count how many reservations have check-in today
        int todayCount = reservationDAO.getTodayReservationCount();
        // Get the list of all rooms in the hotel
        List<Room> allRooms = roomDAO.getAllRooms();
        // Count the total number of rooms
        int totalRooms = allRooms.size();
        // Count how many rooms are currently available (using Java streams to filter)
        // The .stream().filter().count() pattern goes through each room and counts the available ones
        long availableRooms = allRooms.stream().filter(Room::isAvailable).count();
        // Get all reservations from the database
        List<Reservation> allReservations = reservationDAO.getAllReservations();

        // ---- Build the JSON response with all the statistics ----

        // Create a JSON object to hold all our dashboard data
        JsonObject dashboard = new JsonObject();
        // Add each statistic as a property in the JSON object
        dashboard.addProperty("todayReservations", todayCount);
        dashboard.addProperty("totalRooms", totalRooms);
        dashboard.addProperty("availableRooms", availableRooms);
        dashboard.addProperty("totalReservations", allReservations.size());

        // Get the logged-in user's info from the session to display on the dashboard
        HttpSession session = req.getSession(false);  // false = don't create a new session
        if (session != null) {
            // Add the user's full name and role to the response
            dashboard.addProperty("fullName", (String) session.getAttribute("fullName"));
            dashboard.addProperty("role", (String) session.getAttribute("role"));
        }

        // ---- Add the 5 most recent reservations to the response ----

        // Create a JSON array to hold the recent reservations
        JsonArray recent = new JsonArray();
        // We only want the first 5 reservations (or fewer if there are less than 5)
        int limit = Math.min(5, allReservations.size());
        // Loop through the first 5 reservations and add them to the array
        for (int i = 0; i < limit; i++) {
            // gson.toJsonTree() converts a Java object into a JSON element we can add to the array
            recent.add(gson.toJsonTree(allReservations.get(i)));
        }
        // Add the recent reservations array to the dashboard JSON object
        dashboard.add("recentReservations", recent);

        // Write the complete dashboard JSON response to the browser
        resp.getWriter().write(dashboard.toString());
    }
}
