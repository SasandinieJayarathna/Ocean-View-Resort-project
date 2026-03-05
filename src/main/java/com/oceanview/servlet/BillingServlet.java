package com.oceanview.servlet;

import com.oceanview.dao.BillDAOImpl;
import com.oceanview.dao.ReservationDAOImpl;
import com.oceanview.dao.RoomDAOImpl;
import com.oceanview.model.Bill;
import com.oceanview.service.BillingService;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * BillingServlet — Handles bill generation and retrieval.
 * POST: generate bill with strategy. GET: retrieve existing bill.
 * PATTERN: Uses Strategy pattern via BillingService.
 */
@WebServlet("/api/billing")
public class BillingServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String reservationIdStr = req.getParameter("reservationId");
        String strategyType = req.getParameter("strategyType");

        if (reservationIdStr == null || reservationIdStr.isEmpty()) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"Reservation ID required\"}");
            return;
        }

        // Get logged-in user ID from session
        int generatedBy = 0;
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            generatedBy = (Integer) session.getAttribute("userId");
        }

        BillingService billingService = new BillingService(
                new BillDAOImpl(), new ReservationDAOImpl(), new RoomDAOImpl());
        Bill bill = billingService.generateBill(
                Integer.parseInt(reservationIdStr), strategyType, generatedBy);

        if (bill != null) {
            resp.getWriter().write(gson.toJson(bill));
        } else {
            resp.setStatus(404);
            resp.getWriter().write("{\"error\":\"Reservation not found or bill generation failed\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String reservationIdStr = req.getParameter("reservationId");
        if (reservationIdStr == null || reservationIdStr.isEmpty()) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"Reservation ID required\"}");
            return;
        }

        BillingService billingService = new BillingService(
                new BillDAOImpl(), new ReservationDAOImpl(), new RoomDAOImpl());
        Bill bill = billingService.getBillByReservationId(Integer.parseInt(reservationIdStr));

        if (bill != null) {
            resp.getWriter().write(gson.toJson(bill));
        } else {
            resp.setStatus(404);
            resp.getWriter().write("{\"error\":\"Bill not found\"}");
        }
    }
}
