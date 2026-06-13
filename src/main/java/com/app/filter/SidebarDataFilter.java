package com.app.filter;

import jakarta.persistence.EntityManager;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

import com.app.jpa.config.JPAConfig;
import com.app.jpa.dao.JPADao;
import com.app.jpa.model.JPAEnum.StatutDemande;

// Ce filtre s'exécutera sur toutes les URL de l'application
@WebFilter("/*")
public class SidebarDataFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        // 1. EXTRACTION PROPRE DU CHEMIN RELATIF
        String path = req.getServletPath();
        if (path == null || path.isEmpty()) {
            path = req.getRequestURI().substring(req.getContextPath().length());
        }

        // 2. IGNORER LES DOSSIERS ET FICHIERS STATIQUES
        if (path.startsWith("/resources/") ||
                path.startsWith("/assets/") ||
                path.startsWith("/static/") ||
                path.matches(".*\\.(css|js|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|map)$")) {

            // On passe directement à la suite sans toucher à la base de données
            chain.doFilter(request, response);
            return;
        }

        EntityManager em = null;

        try {
            // 2. INITIALISATION DE TON DAO
            em = JPAConfig.getEntityManager();
            JPADao dao = new JPADao(em);

            // 3. CALCUL DES COMPTEURS GLOBAUX VIA LE DAO
            // On utilise ta méthode générique count()
            long countCitoyens = dao.citoyen.count();
            long countActes = dao.acte.count();

            // Pour les demandes, c'est plus pertinent de n'afficher que celles qui
            // nécessitent une action
            // On utilise ta méthode générique countWithCondition()
            long countDemandes = dao.demande.countWithCondition(
                    "e.statut IN (?1, ?2)", StatutDemande.SOUMISE, StatutDemande.EN_COURS);

            // 4. INJECTION DANS LA REQUÊTE
            // C'est ici que les variables ${globalCount...} de ta JSP prennent vie !
            request.setAttribute("globalCountCitoyens", countCitoyens);
            request.setAttribute("globalCountActes", countActes);
            request.setAttribute("globalCountDemandes", countDemandes);

        } catch (Exception e) {
            System.err.println("Erreur dans le SidebarDataFilter : " + e.getMessage());
            // On ne bloque pas l'application si les compteurs échouent
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        // 5. PASSER LA MAIN À LA SERVLET DEMANDÉE
        chain.doFilter(request, response);
    }
}