package com.oceanview.servlet;

import com.oceanview.dao.ReservationDAOImpl;
import com.oceanview.dao.RoomDAOImpl;
import com.oceanview.model.Reservation;
import com.oceanview.model.Room;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * DashboardServlet — Provides dashboard statistics.
 * Returns today's reservation count, total rooms, and recent reservations.
 */
@WebServlet("/api/dashboard")
public class DashboardServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        ReservationDAOImpl reservationDAO = new ReservationDAOImpl();
        RoomDAOImpl roomDAO = new RoomDAOImpl();

        // Gather dashboard stats
        int todayCount = reservationDAO.getTodayReservationCount();
        List<Room> allRooms = roomDAO.getAllRooms();
        int totalRooms = allRooms.size();
        long availableRooms = allRooms.stream().filter(Room::isAvailable).count();
        List<Reservation> allReservations = reservationDAO.getAllReservations();

        // Build JSON response
        JsonObject dashboard = new JsonObject();
        dashboard.addProperty("todayReservations", todayCount);
        dashboard.addProperty("totalRooms", totalRooms);
        dashboard.addProperty("availableRooms", availableRooms);
        dashboard.addProperty("totalReservations", allReservations.size());

        // Get user info from session
        HttpSession session = req.getSession(false);
        if (session != null) {
            dashboard.addProperty("fullName", (String) session.getAttribute("fullName"));
            dashboard.addProperty("role", (String) session.getAttribute("role"));
        }

        // Recent reservations (last 5)
        JsonArray recent = new JsonArray();
        int limit = Math.min(5, allReservations.size());
        for (int i = 0; i < limit; i++) {
            recent.add(gson.toJsonTree(allReservations.get(i)));
        }
        dashboard.add("recentReservations", recent);

        resp.getWriter().write(dashboard.toString());
    }
}
