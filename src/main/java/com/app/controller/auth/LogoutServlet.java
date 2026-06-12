package com.app.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Récupère la session actuelle sans en créer une nouvelle
        HttpSession session = request.getSession(false);

        if (session != null) {
            // 💡 Détruit complètement la session et toutes ses variables (dont
            // utilisateurConnecte)
            session.invalidate();
        }

        // Redirige vers la page de login après déconnexion
        response.sendRedirect(request.getContextPath() + "/login");
    }
}