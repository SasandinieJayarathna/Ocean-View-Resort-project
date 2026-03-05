package com.oceanview.servlet;

// Importing DAO classes that talk to the database for bills, reservations, and rooms
import com.oceanview.dao.BillDAOImpl;
import com.oceanview.dao.ReservationDAOImpl;
import com.oceanview.dao.RoomDAOImpl;
// Importing the Bill model class that represents an invoice in our system
import com.oceanview.model.Bill;
// Importing BillingService which contains the logic for generating and retrieving bills
// It uses the Strategy design pattern to apply different pricing strategies
import com.oceanview.service.BillingService;
// Gson is a Google library that converts Java objects to JSON format
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
// @WebServlet annotation tells Tomcat what URL this servlet responds to
import javax.servlet.annotation.WebServlet;
// Importing all the HTTP servlet classes we need (HttpServlet, HttpServletRequest, etc.)
import javax.servlet.http.*;
// Importing IOException which is required when we write data to the response
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * BillingServlet - This servlet handles all billing operations.
 * It supports two HTTP methods:
 *   POST = Generate a new bill for a reservation (using a pricing strategy)
 *   GET  = Retrieve an existing bill by reservation ID
 *
 * DESIGN PATTERN: This uses the Strategy pattern via BillingService.
 * The "strategyType" parameter lets the user choose different pricing:
 *   - "standard" = normal pricing
 *   - "seasonal" = adds a 20% peak season surcharge
 *   - "loyalty"  = gives a 10% returning guest discount
 */
// @WebServlet annotation maps this servlet to the "/api/billing" URL path
@WebServlet("/api/billing")
public class BillingServlet extends HttpServlet {

    // Gson configured with Java 17-compatible serializers for LocalDate/LocalDateTime
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
                @Override
                public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext ctx) {
                    return new JsonPrimitive(src.toString());
                }
            })
            .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                @Override
                public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext ctx) {
                    return new JsonPrimitive(src.toString());
                }
            })
            .create();

    // ==================== POST = Generate a new bill ====================

    /**
     * Handles POST requests - used to generate a new bill for a reservation.
     * The browser sends the reservation ID and the pricing strategy type.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // We set the response type to JSON so the browser knows what format to expect
        resp.setContentType("application/json");
        // Set character encoding to UTF-8 so special characters display correctly
        resp.setCharacterEncoding("UTF-8");

        // Get the reservation ID and billing strategy from the form data
        String reservationIdStr = req.getParameter("reservationId");
        String strategyType = req.getParameter("strategyType");  // e.g., "standard", "seasonal", "loyalty"

        // Check that a reservation ID was provided
        if (reservationIdStr == null || reservationIdStr.isEmpty()) {
            resp.setStatus(400);  // 400 = Bad Request
            resp.getWriter().write("{\"error\":\"Reservation ID required\"}");
            return;  // Stop here if no ID was given
        }

        // Get the logged-in user's ID from the session so we know who generated this bill
        int generatedBy = 0;
        HttpSession session = req.getSession(false);  // false = don't create a new session
        if (session != null && session.getAttribute("userId") != null) {
            // Cast the session attribute to Integer and store it
            generatedBy = (Integer) session.getAttribute("userId");
        }

        // Create the BillingService with all three DAOs it needs to access the database
        BillingService billingService = new BillingService(
                new BillDAOImpl(), new ReservationDAOImpl(), new RoomDAOImpl());

        // Generate the bill using the chosen pricing strategy
        // This calculates the total cost based on room rate, number of nights, and strategy
        Bill bill = billingService.generateBill(
                Integer.parseInt(reservationIdStr), strategyType, generatedBy);

        // Return the generated bill as JSON, or an error if something went wrong
        if (bill != null) {
            // Convert the Bill object to JSON and send it back to the browser
            resp.getWriter().write(gson.toJson(bill));
        } else {
            // If bill generation failed, return a 404 error
            resp.setStatus(404);
            resp.getWriter().write("{\"error\":\"Reservation not found or bill generation failed\"}");
        }
    }

    // ==================== GET = Retrieve an existing bill ====================

    /**
     * Handles GET requests - used to look up an existing bill for a reservation.
     * The browser sends the reservation ID as a URL parameter.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // We set the response type to JSON so the browser knows what format to expect
        resp.setContentType("application/json");
        // Set character encoding to UTF-8 so special characters display correctly
        resp.setCharacterEncoding("UTF-8");

        // Get the reservation ID from the URL query string (e.g., ?reservationId=5)
        String reservationIdStr = req.getParameter("reservationId");
        // Check that a reservation ID was provided
        if (reservationIdStr == null || reservationIdStr.isEmpty()) {
            resp.setStatus(400);  // 400 = Bad Request
            resp.getWriter().write("{\"error\":\"Reservation ID required\"}");
            return;
        }

        // Create the BillingService with all three DAOs it needs
        BillingService billingService = new BillingService(
                new BillDAOImpl(), new ReservationDAOImpl(), new RoomDAOImpl());

        // Look up the bill for this reservation in the database
        Bill bill = billingService.getBillByReservationId(Integer.parseInt(reservationIdStr));

        // Return the bill as JSON, or an error if not found
        if (bill != null) {
            // Convert the Bill object to JSON and send it back to the browser
            resp.getWriter().write(gson.toJson(bill));
        } else {
            // If no bill was found, return a 404 (Not Found) error
            resp.setStatus(404);
            resp.getWriter().write("{\"error\":\"Bill not found\"}");
        }
    }
}
