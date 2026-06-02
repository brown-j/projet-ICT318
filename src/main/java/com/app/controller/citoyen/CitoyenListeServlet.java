package com.app.controller.citoyen;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Contrôleur pour la liste des citoyens.
 * Gère la pagination, la recherche (NIN, nom) et les filtres.
 */
@WebServlet("/citoyen/liste")
public class CitoyenListeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO: Récupérer les paramètres de pagination
        int page = Integer.parseInt(request.getParameter("page") != null ? request.getParameter("page") : "1");
        int size = Integer.parseInt(request.getParameter("size") != null ? request.getParameter("size") : "10");

        // TODO: Récupérer les paramètres de recherche/filtrage
        String search = request.getParameter("search");
        String statut = request.getParameter("statut");
        String quartier = request.getParameter("quartier");

        // TODO: Appeler le DAO pour récupérer la liste des citoyens
        // List<Citoyen> citoyens = citoyenDAO.findWithPagination(page, size, search,
        // statut, quartier);

        // TODO: Ajouter les citoyens et les métadonnées à la requête
        // request.setAttribute("citoyens", citoyens);
        // request.setAttribute("totalCount", totalCount);
        // request.setAttribute("currentPage", page);

        // Définir la vue à injecter dans le layout maître
        request.setAttribute("view", "/WEB-INF/jsp/modules/citoyen/liste.jsp");

        // Transférer vers le layout
        request.getRequestDispatcher("/WEB-INF/jsp/layouts/base-layout.jsp")
                .forward(request, response);
    }
}
