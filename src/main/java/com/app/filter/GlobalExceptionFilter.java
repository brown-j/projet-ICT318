package com.app.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

// S'applique à toute l'application
@WebFilter("/*")
public class GlobalExceptionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        try {
            // On laisse l'application tourner normalement
            chain.doFilter(request, response);

        } catch (Exception e) {
            // 🛑 BOUM ! Une erreur non gérée a été lancée (throw)
            e.printStackTrace(); // On garde ça pour lire l'erreur dans la console

            // 1. On prépare le Toast
            req.getSession().setAttribute("toastMsg", "Une erreur est survenue : " + e.getMessage());
            req.getSession().setAttribute("toastType", "error");

            // 2. On récupère l'URL de la page où l'utilisateur se trouvait (le Referer)
            String pagePrecedente = req.getHeader("Referer");

            // 3. On le renvoie d'où il vient (ou vers le dashboard par défaut)
            if (pagePrecedente != null) {
                res.sendRedirect(pagePrecedente);
            } else {
                res.sendRedirect(req.getContextPath() + "/dashboard");
            }
        }
    }
}