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
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

@WebFilter("/*")
public class AutoConstructionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // On ignore les assets statiques (CSS, JS, images) pour éviter de casser le
        // design
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        if (path.startsWith("/assets/") || path.startsWith("/css/") || path.startsWith("/js/")) {
            chain.doFilter(request, response);
            return;
        }

        if (!"GET".equalsIgnoreCase(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        ResponseSpyWrapper responseSpy = new ResponseSpyWrapper(httpResponse);

        // On tente d'exécuter la route
        chain.doFilter(httpRequest, responseSpy);

        // SI aucune action n'a été prise OU si le serveur a levé un 404 (route
        // inexistante)
        if (responseSpy.shouldIntercept()) {
            // On réinitialise le statut à 200 pour que le layout s'affiche proprement
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            httpResponse.setContentType("text/html;charset=UTF-8");

            // Propulsion vers le layout maître avec la vue "En construction / Introuvable"
            httpRequest.setAttribute("view", "/WEB-INF/jsp/modules/shared/not-found.jsp");
            httpRequest.getRequestDispatcher("/WEB-INF/jsp/layouts/base-layout.jsp").forward(httpRequest, httpResponse);
        }
    }

    @Override
    public void destroy() {
    }

    /**
     * Wrapper espion amélioré pour intercepter les absences de routes (404)
     */
    private static class ResponseSpyWrapper extends HttpServletResponseWrapper {
        private boolean actionTaken = false;
        private int interceptedStatus = 200;

        public ResponseSpyWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            this.actionTaken = true;
            return super.getWriter();
        }

        @Override
        public void sendRedirect(String location) throws IOException {
            this.actionTaken = true;
            super.sendRedirect(location);
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            this.interceptedStatus = sc;
            if (sc != 404) {
                // On laisse passer les vraies erreurs applicatives (ex: 500)
                super.sendError(sc, msg);
            }
        }

        @Override
        public void sendError(int sc) throws IOException {
            this.interceptedStatus = sc;
            if (sc != 404) {
                super.sendError(sc);
            }
        }

        @Override
        public void setStatus(int sc) {
            this.interceptedStatus = sc;
            super.setStatus(sc);
        }

        /**
         * Détermine si le filtre doit appliquer l'écran de courtoisie.
         */
        public boolean shouldIntercept() {
            // Intercepte si : aucune écriture faite (méthode vide) OU si c'est un 404
            // détecté
            return !this.actionTaken || this.interceptedStatus == 404;
        }
    }
}