package com.app.controller.dashboard;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Contrôleur du tableau de bord.
 * Agrège les KPIs (citoyens, actes, finances) pour la vue principale.
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO: Charger les KPIs depuis les DAOs
        // - Nombre de citoyens
        // - Nombre d'actes ce mois
        // - Nombre de demandes en attente
        // - Recettes du mois

        // Définir la vue à injecter dans le layout maître
        request.setAttribute("view", "/WEB-INF/jsp/modules/dashboard/index.jsp");

        // Transférer vers le layout
        request.getRequestDispatcher("/WEB-INF/jsp/layouts/base-layout.jsp")
                .forward(request, response);
    }
}
