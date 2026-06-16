package com.app.controller.auth;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

import com.app.jpa.config.JPAConfig;
import com.app.jpa.dao.JPADao;
import com.app.jpa.model.OfficierEtatCivil;
import com.app.jpa.model.JPAEnum.StatutOfficier;
import com.app.util.HashUtil;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Si l'utilisateur est déjà connecté, on évite qu'il revoie la page login
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard"); // Vers le dashboard global
            return;
        }

        // Sinon, on affiche la page de connexion
        request.getRequestDispatcher("/WEB-INF/jsp/modules/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        EntityManager em = JPAConfig.getEntityManager();

        try {
            JPADao jpa = new JPADao(em);

            // 1. Recherche de l'officier par son email via ton abstraction Prisma
            OfficierEtatCivil officier = jpa.officier.findFirst("e.email = ?1", email);

            // 2. Validation de l'existence et vérification du hash BCrypt
            if (officier != null && HashUtil.checkPassword(password, officier.getMotDePasse())) {

                // 3. Vérification typée du statut avec ton Enum StatutOfficier
                if (officier.getStatut() == StatutOfficier.ACTIF) {

                    // Connexion approuvée : Création/Récupération de la session
                    HttpSession session = request.getSession();
                    session.setAttribute("user", officier);

                    // 4. Redirection selon les privilèges de l'acteur
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                    return;
                } else {
                    request.setAttribute("erreur", "Votre compte a été suspendu ou désactivé par l'administrateur.");
                }
            } else {
                request.setAttribute("erreur", "Identifiants invalides. Veuillez réessayer.");
            }

            // En cas d'échec, conservation de l'email pour le confort utilisateur
            request.setAttribute("emailSaisi", email);
            request.getRequestDispatcher("/WEB-INF/jsp/modules/auth/login.jsp").forward(request, response);

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}