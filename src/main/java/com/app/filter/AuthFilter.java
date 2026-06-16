package com.app.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

// 💡 Intercepte toutes les requêtes entrantes de l'application
@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialisation du filtre (vide ici)
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false); // false = ne pas créer de session si elle n'existe pas

        // 1. Définir les chemins publics (qui ne nécessitent pas de connexion)
        String loginURI = request.getContextPath() + "/login";
        String portalURI = request.getContextPath() + "/portal/commune";
        String cheminRequete = request.getRequestURI();

        // 💡 On laisse passer les ressources statiques (CSS, JS, Images) y compris le
        // dossier resources
        boolean isRessourceStatique = cheminRequete.startsWith(request.getContextPath() + "/assets") ||
                cheminRequete.startsWith(request.getContextPath() + "/resources") ||
                cheminRequete.startsWith(request.getContextPath() + "/css") ||
                cheminRequete.startsWith(request.getContextPath() + "/js");

        // 2. Vérifier l'état de l'utilisateur
        boolean isConnecte = (session != null && session.getAttribute("user") != null);
        boolean isPageLogin = cheminRequete.equals(loginURI);

        // 🌟 NOUVEAU : On autorise le portail citoyen public en accès libre
        boolean isPagePortailPublic = cheminRequete.equals(portalURI) || cheminRequete.startsWith(portalURI);

        // 3. Logique d'autorisation
        if (isConnecte || isPageLogin || isRessourceStatique || isPagePortailPublic) {
            // ✅ Accès autorisé : Utilisateur connecté, page login, ressources statiques OU
            // portail citoyen
            chain.doFilter(request, response);
        } else {
            // ❌ Accès refusé : L'utilisateur n'est pas connecté et essaie d'accéder au
            // back-office protégé
            response.sendRedirect(loginURI);
        }
    }

    @Override
    public void destroy() {
        // Destruction du filtre (vide ici)
    }
}