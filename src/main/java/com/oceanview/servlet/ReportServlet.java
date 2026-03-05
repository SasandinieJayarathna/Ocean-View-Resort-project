package com.oceanview.servlet;

import com.oceanview.service.ReportService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * ReportServlet — Generates business reports (Admin only).
 * GET: accepts reportType (occupancy/revenue), startDate, endDate.
 * Calls ReportService stored procedures.
 */
@WebServlet("/api/reports")
public class ReportServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // Check admin role
        HttpSession session = req.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            resp.setStatus(403);
            resp.getWriter().write("{\"error\":\"Admin access required\"}");
            return;
        }

        String reportType = req.getParameter("type");
        String startDateStr = req.getParameter("startDate");
        String endDateStr = req.getParameter("endDate");

        if (reportType == null || startDateStr == null || endDateStr == null) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"Missing required parameters: type, startDate, endDate\"}");
            return;
        }

        ReportService reportService = new ReportService();
        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        JsonObject result = new JsonObject();
        result.addProperty("reportType", reportType);
        result.addProperty("startDate", startDateStr);
        result.addProperty("endDate", endDateStr);

        if ("occupancy".equalsIgnoreCase(reportType)) {
            List<Map<String, Object>> data = reportService.getOccupancyReport(startDate, endDate);
            result.add("data", gson.toJsonTree(data));
        } else if ("revenue".equalsIgnoreCase(reportType)) {
            List<Map<String, Object>> data = reportService.getRevenueReport(startDate, endDate);
            result.add("data", gson.toJsonTree(data));
        } else {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\":\"Invalid report type. Use 'occupancy' or 'revenue'\"}");
            return;
        }

        resp.getWriter().write(result.toString());
    }
}
