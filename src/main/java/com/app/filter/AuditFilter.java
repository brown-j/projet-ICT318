package com.app.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import com.app.jpa.model.OfficierEtatCivil;
import com.app.util.AuditContext;

@WebFilter("/*")
public class AuditFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        // 1. Récupération de l'officier depuis la session
        OfficierEtatCivil officier = (OfficierEtatCivil) req.getSession().getAttribute("user");

        // 2. Récupération de l'adresse IP (gère les proxys / reverse proxys comme
        // Nginx)
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = req.getRemoteAddr();
        }

        try {
            // 3. On injecte dans le contexte du Thread actuel
            AuditContext.set(officier, ip);
            chain.doFilter(request, response);
        } finally {
            // 4. ⚠️ CRUCIAL : On nettoie toujours pour éviter les fuites de mémoire !
            AuditContext.clear();
        }
    }
}