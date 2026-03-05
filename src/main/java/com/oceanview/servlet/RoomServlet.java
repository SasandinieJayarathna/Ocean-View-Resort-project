package com.oceanview.servlet;

import com.oceanview.dao.RoomDAOImpl;
import com.oceanview.model.Room;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * RoomServlet — REST-like servlet for room queries.
 * GET: if checkIn/checkOut params → available rooms, else → all rooms.
 */
@WebServlet("/api/rooms")
public class RoomServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        RoomDAOImpl roomDAO = new RoomDAOImpl();
        String checkIn = req.getParameter("checkIn");
        String checkOut = req.getParameter("checkOut");
        String type = req.getParameter("type");

        List<Room> rooms;
        if (checkIn != null && checkOut != null && !checkIn.isEmpty() && !checkOut.isEmpty()) {
            // Get available rooms for date range
            rooms = roomDAO.getAvailableRooms(
                    LocalDate.parse(checkIn),
                    LocalDate.parse(checkOut),
                    type
            );
        } else {
            // Get all rooms
            rooms = roomDAO.getAllRooms();
        }

        resp.getWriter().write(gson.toJson(rooms));
    }
}
