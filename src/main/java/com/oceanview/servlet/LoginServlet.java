package com.oceanview.servlet;

import com.oceanview.dao.UserDAOImpl;
import com.oceanview.model.User;
import com.oceanview.service.AuthService;
import com.google.gson.JsonObject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * LoginServlet — Handles user authentication via POST.
 * Returns JSON response. Creates session on success.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        AuthService authService = new AuthService(new UserDAOImpl());
        User user = authService.authenticate(username, password);

        JsonObject json = new JsonObject();
        if (user != null) {
            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("fullName", user.getFullName());
            session.setAttribute("role", user.getRole());
            json.addProperty("success", true);
            json.addProperty("fullName", user.getFullName());
            json.addProperty("role", user.getRole());
        } else {
            resp.setStatus(401);
            json.addProperty("success", false);
            json.addProperty("error", "Invalid username or password");
        }
        resp.getWriter().write(json.toString());
    }
}
