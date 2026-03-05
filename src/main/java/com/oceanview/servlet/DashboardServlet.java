package com.oceanview.servlet;

// Importing DAO classes that talk to the database for reservations and rooms
import com.oceanview.dao.ReservationDAOImpl;
import com.oceanview.dao.RoomDAOImpl;
// Importing model classes that represent reservations and rooms in our system
import com.oceanview.model.Reservation;
import com.oceanview.model.Room;
// Gson is a Google library that converts Java objects to JSON format
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
// JsonArray lets us build a JSON array (a list of items in JSON format)
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
// JsonObject lets us manually build a JSON object with key-value pairs
import com.google.gson.JsonObject;
// @WebServlet annotation tells Tomcat what URL this servlet responds to
import javax.servlet.annotation.WebServlet;
// Importing all the HTTP servlet classes we need (HttpServlet, HttpServletRequest, etc.)
import javax.servlet.http.*;
// Importing IOException which is required when we write data to the response
import java.io.IOException;
// Importing date types needed for custom Gson serializers
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    // Gson configured with custom serializers for Java 17 compatibility.
    // Java 17's module system blocks Gson's reflection on LocalDate/LocalDateTime fields,
    // so we register explicit serializers that convert them to ISO-8601 strings.
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
                @Override
                public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext ctx) {
                    return new JsonPrimitive(src.toString()); // e.g. "2025-06-15"
                }
            })
            .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                @Override
                public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext ctx) {
                    return new JsonPrimitive(src.toString()); // e.g. "2025-06-15T10:30:00"
                }
            })
            .create();

    /**
     * Handles GET requests - gathers all the dashboard statistics and returns them as JSON.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // We set the response type to JSON so the browser knows what format to expect
        resp.setContentType("application/json");
        // Set character encoding to UTF-8 so special characters display correctly
        resp.setCharacterEncoding("UTF-8");

        // Build the response object — session data is always included first so
        // role/fullName are returned even if the DB queries below fail.
        JsonObject dashboard = new JsonObject();

        // Session data (no DB call needed)
        HttpSession session = req.getSession(false);
        if (session != null) {
            dashboard.addProperty("fullName", (String) session.getAttribute("fullName"));
            dashboard.addProperty("role",     (String) session.getAttribute("role"));
        }

        // DB statistics — safe defaults applied if any query fails
        try {
            ReservationDAOImpl reservationDAO = new ReservationDAOImpl();
            RoomDAOImpl roomDAO = new RoomDAOImpl();

            int todayCount = reservationDAO.getTodayReservationCount();
            List<Room> allRooms = roomDAO.getAllRooms();
            long availableRooms = allRooms.stream().filter(Room::isAvailable).count();
            List<Reservation> allReservations = reservationDAO.getAllReservations();

            dashboard.addProperty("todayReservations", todayCount);
            dashboard.addProperty("totalRooms",        allRooms.size());
            dashboard.addProperty("availableRooms",    availableRooms);
            dashboard.addProperty("totalReservations", allReservations.size());

            JsonArray recent = new JsonArray();
            int limit = Math.min(5, allReservations.size());
            for (int i = 0; i < limit; i++) {
                recent.add(gson.toJsonTree(allReservations.get(i)));
            }
            dashboard.add("recentReservations", recent);

        } catch (Exception e) {
            dashboard.addProperty("todayReservations", 0);
            dashboard.addProperty("totalRooms",        0);
            dashboard.addProperty("availableRooms",    0);
            dashboard.addProperty("totalReservations", 0);
            dashboard.add("recentReservations", new JsonArray());
        }

        resp.getWriter().write(dashboard.toString());
    }
}
