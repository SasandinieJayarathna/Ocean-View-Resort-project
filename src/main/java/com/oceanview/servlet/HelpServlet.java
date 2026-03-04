package com.oceanview.servlet;

// JsonArray lets us build a JSON array (a list of items in JSON format)
import com.google.gson.JsonArray;
// JsonObject lets us manually build a JSON object with key-value pairs
import com.google.gson.JsonObject;
// @WebServlet annotation tells Tomcat what URL this servlet responds to
import javax.servlet.annotation.WebServlet;
// Importing all the HTTP servlet classes we need (HttpServlet, HttpServletRequest, etc.)
import javax.servlet.http.*;
// Importing IOException which is required when we write data to the response
import java.io.IOException;

/**
 * HelpServlet - This servlet provides help content for the hotel management system.
 * When the user navigates to the Help page, the browser sends a GET request here,
 * and this servlet returns a JSON array of help sections. Each section has a title
 * and a content field with instructions on how to use the system.
 *
 * This is a simple servlet - it just returns static help text as JSON.
 * No database access is needed because the help content is hardcoded here.
 */
// @WebServlet annotation maps this servlet to the "/api/help" URL path
@WebServlet("/api/help")
public class HelpServlet extends HttpServlet {

    /**
     * Handles GET requests - returns all the help sections as a JSON array.
     * Each section contains a "title" and "content" field.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // We set the response type to JSON so the browser knows what format to expect
        resp.setContentType("application/json");
        // Set character encoding to UTF-8 so special characters display correctly
        resp.setCharacterEncoding("UTF-8");

        // Create a JSON array to hold all the help sections
        JsonArray sections = new JsonArray();

        // Add each help section using the createSection() helper method
        // Each section has a title and detailed instructions

        // Section 1: How to log into the system
        sections.add(createSection("How to Login",
                "Enter your username and password on the login page. Staff accounts have standard access. Admin accounts have access to reports and user management."));

        // Section 2: Step-by-step guide to creating a reservation
        sections.add(createSection("How to Add a Reservation",
                "1. Click 'Add Reservation' from the dashboard or navigation menu.\n2. Fill in the guest details: name, address, contact number, and email.\n3. Select the room type and choose check-in/check-out dates.\n4. Select an available room from the list.\n5. Add any special requests.\n6. Click 'Create Reservation' to confirm."));

        // Section 3: How to view and search for reservations
        sections.add(createSection("How to View Reservations",
                "1. Click 'View Reservations' from the navigation menu.\n2. Use the search bar to find reservations by guest name, reservation number, or contact number.\n3. Click on a reservation to view full details.\n4. Use the Cancel button to cancel a reservation if needed."));

        // Section 4: How to generate a bill for a guest
        sections.add(createSection("How to Generate a Bill",
                "1. Go to the Billing page from the navigation menu.\n2. Enter the reservation number to look up the reservation.\n3. Review the reservation details.\n4. Select a billing strategy: Standard, Seasonal (20% surcharge), or Loyalty (10% discount).\n5. Click 'Generate Bill' to create the invoice.\n6. Use the Print button to print the bill."));

        // Section 5: How to view reports (admin only feature)
        sections.add(createSection("How to View Reports",
                "Reports are available to Admin users only.\n1. Go to the Reports page.\n2. Select date range (start and end dates).\n3. Choose report type: Occupancy Report or Revenue Report.\n4. Click 'Generate Report' to view results."));

        // Section 6: Frequently asked questions
        sections.add(createSection("System FAQ",
                "Q: What is the default login?\nA: Contact your system administrator for credentials.\n\nQ: Can I modify a reservation after creation?\nA: Yes, you can update guest details and status from the View Reservations page.\n\nQ: What billing strategies are available?\nA: Standard (base rate), Seasonal (20% peak surcharge), and Loyalty (10% returning guest discount)."));

        // Write the JSON array of all help sections to the response
        resp.getWriter().write(sections.toString());
    }

    /**
     * Helper method to create a single help section as a JSON object.
     * This keeps the code cleaner by avoiding repetition.
     *
     * @param title   - The heading for the help section (e.g., "How to Login")
     * @param content - The detailed instructions for that section
     * @return A JsonObject with "title" and "content" properties
     */
    private JsonObject createSection(String title, String content) {
        // Create a new JSON object for this help section
        JsonObject section = new JsonObject();
        // Add the title property
        section.addProperty("title", title);
        // Add the content property
        section.addProperty("content", content);
        // Return the completed section object
        return section;
    }
}
