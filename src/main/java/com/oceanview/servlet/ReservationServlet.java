package com.oceanview.servlet;

import com.oceanview.dao.ReservationDAOImpl;
import com.oceanview.dao.RoomDAOImpl;
import com.oceanview.model.Reservation;
import com.oceanview.pattern.observer.EmailNotificationObserver;
import com.oceanview.pattern.observer.LoggingObserver;
import com.oceanview.pattern.observer.ReservationNotifier;
import com.oceanview.service.ReservationService;
import com.oceanview.util.InputValidator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * ReservationServlet — REST-like servlet for reservation CRUD.
 * GET: list all, search, or get by number.
 * POST: create new reservation with validation.
 * PUT: update reservation details/status.
 * DELETE: cancel a reservation.
 */
@WebServlet("/api/reservations")
public class ReservationServlet extends HttpServlet {
    private final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create();

    /** Creates ReservationService with DAO + Observer setup. */
    private ReservationService createService() {
        ReservationNotifier notifier = new ReservationNotifier();
        notifier.addObserver(new LoggingObserver());
        notifier.addObserver(new EmailNotificationObserver());
        return new ReservationService(new ReservationDAOImpl(), new RoomDAOImpl(), notifier);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        ReservationService service = createService();

        String number = req.getParameter("number");
        String search = req.getParameter("search");

        if (number != null && !number.isEmpty()) {
            // Get single reservation by number
            Reservation r = service.getReservationByNumber(number);
            if (r != null) {
                resp.getWriter().write(gson.toJson(r));
            } else {
                resp.setStatus(404);
                resp.getWriter().write("{\"error\":\"Reservation not found\"}");
            }
        } else if (search != null && !search.isEmpty()) {
            // Search reservations
            List<Reservation> results = service.searchReservations(search);
            resp.getWriter().write(gson.toJson(results));
        } else {
            // List all reservations
            List<Reservation> all = service.getAllReservations();
            resp.getWriter().write(gson.toJson(all));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        JsonObject json = new JsonObject();

        // Read form parameters
        String guestName = req.getParameter("guestName");
        String guestAddress = req.getParameter("guestAddress");
        String contactNumber = req.getParameter("contactNumber");
        String guestEmail = req.getParameter("guestEmail");
        String roomIdStr = req.getParameter("roomId");
        String checkInStr = req.getParameter("checkInDate");
        String checkOutStr = req.getParameter("checkOutDate");
        String specialRequests = req.getParameter("specialRequests");

        // Validate required fields
        if (!InputValidator.isValidName(guestName)) {
            resp.setStatus(400);
            json.addProperty("error", "Invalid guest name");
            resp.getWriter().write(json.toString()); return;
        }
        if (!InputValidator.isValidPhone(contactNumber)) {
            resp.setStatus(400);
            json.addProperty("error", "Invalid contact number");
            resp.getWriter().write(json.toString()); return;
        }
        if (guestEmail != null && !guestEmail.isEmpty() && !InputValidator.isValidEmail(guestEmail)) {
            resp.setStatus(400);
            json.addProperty("error", "Invalid email address");
            resp.getWriter().write(json.toString()); return;
        }
        if (!InputValidator.isValidDateRange(checkInStr, checkOutStr)) {
            resp.setStatus(400);
            json.addProperty("error", "Invalid date range");
            resp.getWriter().write(json.toString()); return;
        }

        // Build reservation object
        Reservation reservation = new Reservation();
        reservation.setGuestName(guestName.trim());
        reservation.setGuestAddress(guestAddress != null ? guestAddress.trim() : "");
        reservation.setContactNumber(contactNumber.trim());
        reservation.setGuestEmail(guestEmail != null ? guestEmail.trim() : "");
        reservation.setRoomId(Integer.parseInt(roomIdStr));
        reservation.setCheckInDate(LocalDate.parse(checkInStr));
        reservation.setCheckOutDate(LocalDate.parse(checkOutStr));
        reservation.setSpecialRequests(specialRequests != null ? specialRequests.trim() : "");

        // Get logged-in user ID from session
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            reservation.setCreatedBy((Integer) session.getAttribute("userId"));
        }

        ReservationService service = createService();
        boolean created = service.createReservation(reservation);

        if (created) {
            json.addProperty("success", true);
            json.addProperty("reservationNumber", reservation.getReservationNumber());
            json.addProperty("message", "Reservation created successfully");
            resp.getWriter().write(json.toString());
        } else {
            resp.setStatus(500);
            json.addProperty("error", "Failed to create reservation");
            resp.getWriter().write(json.toString());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        JsonObject json = new JsonObject();

        String idStr = req.getParameter("reservationId");
        if (idStr == null || idStr.isEmpty()) {
            resp.setStatus(400);
            json.addProperty("error", "Reservation ID required");
            resp.getWriter().write(json.toString()); return;
        }

        ReservationService service = createService();
        Reservation r = service.getReservationById(Integer.parseInt(idStr));
        if (r == null) {
            resp.setStatus(404);
            json.addProperty("error", "Reservation not found");
            resp.getWriter().write(json.toString()); return;
        }

        // Update fields if provided
        String status = req.getParameter("status");
        String guestName = req.getParameter("guestName");
        String contactNumber = req.getParameter("contactNumber");
        String guestEmail = req.getParameter("guestEmail");
        String specialRequests = req.getParameter("specialRequests");

        if (status != null) r.setStatus(status);
        if (guestName != null && InputValidator.isValidName(guestName)) r.setGuestName(guestName.trim());
        if (contactNumber != null && InputValidator.isValidPhone(contactNumber)) r.setContactNumber(contactNumber.trim());
        if (guestEmail != null) r.setGuestEmail(guestEmail.trim());
        if (specialRequests != null) r.setSpecialRequests(specialRequests.trim());

        ReservationDAOImpl dao = new ReservationDAOImpl();
        boolean updated = dao.updateReservation(r);
        json.addProperty("success", updated);
        resp.getWriter().write(json.toString());
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        JsonObject json = new JsonObject();

        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            resp.setStatus(400);
            json.addProperty("error", "Reservation ID required");
            resp.getWriter().write(json.toString()); return;
        }

        ReservationService service = createService();
        boolean cancelled = service.cancelReservation(Integer.parseInt(idStr));
        json.addProperty("success", cancelled);
        if (cancelled) {
            json.addProperty("message", "Reservation cancelled successfully");
        } else {
            json.addProperty("error", "Failed to cancel reservation");
        }
        resp.getWriter().write(json.toString());
    }
}
