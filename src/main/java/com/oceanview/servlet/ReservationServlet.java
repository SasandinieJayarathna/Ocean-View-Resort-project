package com.oceanview.servlet;

// Importing DAO classes that talk directly to the database for reservations and rooms
import com.oceanview.dao.ReservationDAOImpl;
import com.oceanview.dao.RoomDAOImpl;
// Importing the Reservation model class that represents a booking in our system
import com.oceanview.model.Reservation;
// Importing Observer pattern classes - these automatically send notifications when reservations change
import com.oceanview.pattern.observer.EmailNotificationObserver;
import com.oceanview.pattern.observer.LoggingObserver;
import com.oceanview.pattern.observer.ReservationNotifier;
// Importing the ReservationService which contains the business logic for managing reservations
import com.oceanview.service.ReservationService;
// We use InputValidator to check the data before saving to the database
import com.oceanview.util.InputValidator;
// Gson is a Google library that converts Java objects to JSON format
import com.google.gson.Gson;
// GsonBuilder lets us customize how Gson converts objects (e.g., setting date format)
import com.google.gson.GsonBuilder;
// JsonElement, JsonPrimitive, JsonSerializer are used for custom type adapters
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
// JsonObject lets us manually build JSON responses piece by piece
import com.google.gson.JsonObject;
// @WebServlet annotation tells Tomcat what URL this servlet responds to
import javax.servlet.annotation.WebServlet;
// Importing all the HTTP servlet classes we need (HttpServlet, HttpServletRequest, etc.)
import javax.servlet.http.*;
// Importing IOException which is required when we write data to the response
import java.io.IOException;
// Importing LocalDate and LocalDateTime for handling reservation dates
import java.time.LocalDate;
import java.time.LocalDateTime;
// Type is needed for the custom serializer method signature
import java.lang.reflect.Type;
// Importing List to hold collections of reservations
import java.util.List;

/**
 * ReservationServlet - This is a REST-like servlet that handles all reservation operations.
 * It supports four HTTP methods, each for a different operation:
 *   GET    = Read data (list all reservations, search, or get one by number)
 *   POST   = Create a new reservation
 *   PUT    = Update an existing reservation
 *   DELETE = Cancel (remove) a reservation
 *
 * This is sometimes called CRUD: Create, Read, Update, Delete.
 */
// @WebServlet annotation maps this servlet to the "/api/reservations" URL path
@WebServlet("/api/reservations")
public class ReservationServlet extends HttpServlet {

    // Gson configured with custom serializers for Java 8 date types.
    // Without these, Gson tries to reflect on LocalDate/LocalDateTime private fields
    // which is blocked by the Java 17 module system, causing a 500 error.
    private final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
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
     * Helper method that creates a ReservationService with all its dependencies.
     * It sets up the Observer pattern so that when a reservation is created/updated,
     * the system automatically logs the event and sends an email notification.
     */
    private ReservationService createService() {
        // Create a notifier that will tell observers when something happens to a reservation
        ReservationNotifier notifier = new ReservationNotifier();
        // Add a logging observer - it writes reservation events to the log
        notifier.addObserver(new LoggingObserver());
        // Add an email observer - it sends email notifications about reservation events
        notifier.addObserver(new EmailNotificationObserver());
        // Create and return the service with the DAO (database access) and the notifier
        return new ReservationService(new ReservationDAOImpl(), new RoomDAOImpl(), notifier);
    }

    // ==================== GET = Read data ====================

    /**
     * Handles GET requests - used to retrieve reservation data.
     * It can do three things depending on what parameters are sent:
     *   1. If a "number" parameter is given, fetch that one reservation
     *   2. If a "search" parameter is given, search for matching reservations
     *   3. If no parameters are given, return all reservations
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            ReservationService service = createService();
            String number = req.getParameter("number");
            String search = req.getParameter("search");

            if (number != null && !number.isEmpty()) {
                Reservation r = service.getReservationByNumber(number);
                if (r != null) {
                    resp.getWriter().write(gson.toJson(r));
                } else {
                    resp.setStatus(404);
                    resp.getWriter().write("{\"error\":\"Reservation not found\"}");
                }
            } else if (search != null && !search.isEmpty()) {
                List<Reservation> results = service.searchReservations(search);
                resp.getWriter().write(gson.toJson(results));
            } else {
                List<Reservation> all = service.getAllReservations();
                resp.getWriter().write(gson.toJson(all));
            }
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"Server error: " + e.getMessage().replace("\"", "'") + "\"}");
        }
    }

    // ==================== POST = Create new reservation ====================

    /**
     * Handles POST requests - used to create a new reservation.
     * It reads all the guest/room details from the form, validates them,
     * builds a Reservation object, and saves it to the database.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // We set the response type to JSON so the browser knows what format to expect
        resp.setContentType("application/json");
        // Set character encoding to UTF-8 so special characters display correctly
        resp.setCharacterEncoding("UTF-8");
        // Create a JSON object to build our response
        JsonObject json = new JsonObject();

        // Read all the form parameters sent by the browser
        String guestName = req.getParameter("guestName");
        String guestAddress = req.getParameter("guestAddress");
        String contactNumber = req.getParameter("contactNumber");
        String guestEmail = req.getParameter("guestEmail");
        String roomIdStr = req.getParameter("roomId");
        String checkInStr = req.getParameter("checkInDate");
        String checkOutStr = req.getParameter("checkOutDate");
        String specialRequests = req.getParameter("specialRequests");
        // mealPlan: RO (Room Only), BB (Bed & Breakfast), HB (Half Board), or FB (Full Board)
        String mealPlan = req.getParameter("mealPlan");

        // ---- Validation: We use InputValidator to check the data before saving to the database ----

        // Check if the guest name is valid (not empty, no weird characters)
        if (!InputValidator.isValidName(guestName)) {
            resp.setStatus(400);  // 400 = Bad Request
            json.addProperty("error", "Invalid guest name");
            resp.getWriter().write(json.toString());
            return;  // Stop here, don't continue with invalid data
        }

        // Check if the contact number is a valid phone number
        if (!InputValidator.isValidPhone(contactNumber)) {
            resp.setStatus(400);
            json.addProperty("error", "Invalid contact number");
            resp.getWriter().write(json.toString());
            return;
        }

        // Check if the email is valid (only if one was provided)
        if (guestEmail != null && !guestEmail.isEmpty() && !InputValidator.isValidEmail(guestEmail)) {
            resp.setStatus(400);
            json.addProperty("error", "Invalid email address");
            resp.getWriter().write(json.toString());
            return;
        }

        // Check if the date range makes sense (check-in before check-out, not in the past, etc.)
        if (!InputValidator.isValidDateRange(checkInStr, checkOutStr)) {
            resp.setStatus(400);
            json.addProperty("error", "Invalid date range");
            resp.getWriter().write(json.toString());
            return;
        }

        // ---- Build the Reservation object with all the validated data ----

        // Create a new empty Reservation object
        Reservation reservation = new Reservation();
        // Set each field on the reservation, trimming whitespace from text inputs
        reservation.setGuestName(guestName.trim());
        reservation.setGuestAddress(guestAddress != null ? guestAddress.trim() : "");
        reservation.setContactNumber(contactNumber.trim());
        reservation.setGuestEmail(guestEmail != null ? guestEmail.trim() : "");
        // Parse the room ID from a String to an int
        reservation.setRoomId(Integer.parseInt(roomIdStr));
        // Parse the date strings into LocalDate objects (e.g., "2025-06-15" becomes a date)
        reservation.setCheckInDate(LocalDate.parse(checkInStr));
        reservation.setCheckOutDate(LocalDate.parse(checkOutStr));
        // Prepend the selected meal plan to specialRequests so it is stored with the reservation
        String plan = (mealPlan != null && !mealPlan.isEmpty()) ? mealPlan.trim() : "RO";
        String requests = (specialRequests != null) ? specialRequests.trim() : "";
        reservation.setSpecialRequests("Meal Plan: " + plan + (requests.isEmpty() ? "" : " | " + requests));

        // Get the logged-in user's ID from the session so we know who created this reservation
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            // Store which staff member created this reservation
            reservation.setCreatedBy((Integer) session.getAttribute("userId"));
        }

        try {
            // Use the service to save the reservation to the database
            ReservationService service = createService();
            boolean created = service.createReservation(reservation);

            if (created) {
                json.addProperty("success", true);
                json.addProperty("reservationNumber", reservation.getReservationNumber());
                json.addProperty("message", "Reservation created successfully");
            } else {
                resp.setStatus(500);
                json.addProperty("error", "Failed to create reservation. The room may not be available.");
            }
        } catch (Exception e) {
            resp.setStatus(500);
            json.addProperty("error", "Server error: " + e.getMessage());
        }
        resp.getWriter().write(json.toString());
    }

    // ==================== PUT = Update existing reservation ====================

    /**
     * Handles PUT requests - used to update an existing reservation.
     * It finds the reservation by ID, updates the fields that were sent,
     * and saves the changes back to the database.
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // We set the response type to JSON so the browser knows what format to expect
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        // Create a JSON object to build our response
        JsonObject json = new JsonObject();

        // Get the reservation ID from the request parameters
        String idStr = req.getParameter("reservationId");
        // If no ID was provided, return a 400 Bad Request error
        if (idStr == null || idStr.isEmpty()) {
            resp.setStatus(400);
            json.addProperty("error", "Reservation ID required");
            resp.getWriter().write(json.toString());
            return;
        }

        // Look up the existing reservation in the database by its ID
        ReservationService service = createService();
        Reservation r = service.getReservationById(Integer.parseInt(idStr));
        // If no reservation was found with that ID, return a 404 Not Found error
        if (r == null) {
            resp.setStatus(404);
            json.addProperty("error", "Reservation not found");
            resp.getWriter().write(json.toString());
            return;
        }

        // Read the fields that the user wants to update (any of these can be null if not sent)
        String status = req.getParameter("status");
        String guestName = req.getParameter("guestName");
        String contactNumber = req.getParameter("contactNumber");
        String guestEmail = req.getParameter("guestEmail");
        String specialRequests = req.getParameter("specialRequests");

        // Only update each field if a new value was provided and it passes validation
        if (status != null) r.setStatus(status);
        if (guestName != null && InputValidator.isValidName(guestName)) r.setGuestName(guestName.trim());
        if (contactNumber != null && InputValidator.isValidPhone(contactNumber)) r.setContactNumber(contactNumber.trim());
        if (guestEmail != null) r.setGuestEmail(guestEmail.trim());
        if (specialRequests != null) r.setSpecialRequests(specialRequests.trim());

        // Save the updated reservation back to the database using the DAO
        ReservationDAOImpl dao = new ReservationDAOImpl();
        boolean updated = dao.updateReservation(r);
        // Tell the browser if the update was successful or not
        json.addProperty("success", updated);
        resp.getWriter().write(json.toString());
    }

    // ==================== DELETE = Cancel a reservation ====================

    /**
     * Handles DELETE requests - used to cancel an existing reservation.
     * It takes a reservation ID and marks it as cancelled in the database.
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // We set the response type to JSON so the browser knows what format to expect
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        // Create a JSON object to build our response
        JsonObject json = new JsonObject();

        // Get the reservation ID from the request parameters
        String idStr = req.getParameter("id");
        // If no ID was provided, return a 400 Bad Request error
        if (idStr == null || idStr.isEmpty()) {
            resp.setStatus(400);
            json.addProperty("error", "Reservation ID required");
            resp.getWriter().write(json.toString());
            return;
        }

        // Use the service to cancel the reservation (this updates its status in the database)
        ReservationService service = createService();
        boolean cancelled = service.cancelReservation(Integer.parseInt(idStr));
        // Tell the browser if the cancellation was successful
        json.addProperty("success", cancelled);
        if (cancelled) {
            json.addProperty("message", "Reservation cancelled successfully");
        } else {
            json.addProperty("error", "Failed to cancel reservation");
        }
        // Write the JSON response back to the browser
        resp.getWriter().write(json.toString());
    }
}
