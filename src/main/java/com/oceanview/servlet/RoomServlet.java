package com.oceanview.servlet;

// Importing the DAO class that talks to the database to find room records
import com.oceanview.dao.RoomDAOImpl;
// Importing the Room model class that represents a hotel room in our system
import com.oceanview.model.Room;
// Gson is a Google library that converts Java objects to JSON format
import com.google.gson.Gson;
// @WebServlet annotation tells Tomcat what URL this servlet responds to
import javax.servlet.annotation.WebServlet;
// Importing all the HTTP servlet classes we need (HttpServlet, HttpServletRequest, etc.)
import javax.servlet.http.*;
// Importing IOException which is required when we write data to the response
import java.io.IOException;
// Importing LocalDate for handling check-in and check-out date parameters
import java.time.LocalDate;
// Importing List to hold collections of rooms
import java.util.List;

/**
 * RoomServlet - This servlet handles room-related requests.
 * It responds to GET requests on "/api/rooms" and can do two things:
 *   1. If check-in and check-out dates are provided, return only the rooms
 *      that are available for that date range.
 *   2. If no dates are provided, return all rooms in the hotel.
 */
// @WebServlet annotation maps this servlet to the "/api/rooms" URL path
@WebServlet("/api/rooms")
public class RoomServlet extends HttpServlet {

    // Gson is a Google library that converts Java objects to JSON format
    private final Gson gson = new Gson();

    /**
     * Handles GET requests - used to retrieve room data.
     * The browser can send optional parameters (checkIn, checkOut, type) to filter results.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // We set the response type to JSON so the browser knows what format to expect
        resp.setContentType("application/json");
        // Set character encoding to UTF-8 so special characters display correctly
        resp.setCharacterEncoding("UTF-8");

        try {
            // Create a RoomDAO to access room data from the database
            RoomDAOImpl roomDAO = new RoomDAOImpl();

            // Get the optional date and type parameters from the URL query string
            String checkIn = req.getParameter("checkIn");     // e.g., "2025-06-15"
            String checkOut = req.getParameter("checkOut");    // e.g., "2025-06-20"
            String type = req.getParameter("type");            // e.g., "DELUXE" or "STANDARD"

            List<Room> rooms;

            if (checkIn != null && checkOut != null && !checkIn.isEmpty() && !checkOut.isEmpty()) {
                rooms = roomDAO.getAvailableRooms(
                        LocalDate.parse(checkIn),
                        LocalDate.parse(checkOut),
                        type
                );
            } else {
                rooms = roomDAO.getAllRooms();
            }

            resp.getWriter().write(gson.toJson(rooms));

        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"Server error: " + e.getMessage().replace("\"", "'") + "\"}");
        }
    }
}
