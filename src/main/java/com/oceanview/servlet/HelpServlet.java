package com.oceanview.servlet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * HelpServlet — Provides help content for the system.
 * Returns JSON array of help sections with title and content.
 */
@WebServlet("/api/help")
public class HelpServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        JsonArray sections = new JsonArray();

        sections.add(createSection("How to Login",
                "Enter your username and password on the login page. Staff accounts have standard access. Admin accounts have access to reports and user management."));

        sections.add(createSection("How to Add a Reservation",
                "1. Click 'Add Reservation' from the dashboard or navigation menu.\n2. Fill in the guest details: name, address, contact number, and email.\n3. Select the room type and choose check-in/check-out dates.\n4. Select an available room from the list.\n5. Add any special requests.\n6. Click 'Create Reservation' to confirm."));

        sections.add(createSection("How to View Reservations",
                "1. Click 'View Reservations' from the navigation menu.\n2. Use the search bar to find reservations by guest name, reservation number, or contact number.\n3. Click on a reservation to view full details.\n4. Use the Cancel button to cancel a reservation if needed."));

        sections.add(createSection("How to Generate a Bill",
                "1. Go to the Billing page from the navigation menu.\n2. Enter the reservation number to look up the reservation.\n3. Review the reservation details.\n4. Select a billing strategy: Standard, Seasonal (20% surcharge), or Loyalty (10% discount).\n5. Click 'Generate Bill' to create the invoice.\n6. Use the Print button to print the bill."));

        sections.add(createSection("How to View Reports",
                "Reports are available to Admin users only.\n1. Go to the Reports page.\n2. Select date range (start and end dates).\n3. Choose report type: Occupancy Report or Revenue Report.\n4. Click 'Generate Report' to view results."));

        sections.add(createSection("System FAQ",
                "Q: What is the default login?\nA: Contact your system administrator for credentials.\n\nQ: Can I modify a reservation after creation?\nA: Yes, you can update guest details and status from the View Reservations page.\n\nQ: What billing strategies are available?\nA: Standard (base rate), Seasonal (20% peak surcharge), and Loyalty (10% returning guest discount)."));

        resp.getWriter().write(sections.toString());
    }

    private JsonObject createSection(String title, String content) {
        JsonObject section = new JsonObject();
        section.addProperty("title", title);
        section.addProperty("content", content);
        return section;
    }
}
