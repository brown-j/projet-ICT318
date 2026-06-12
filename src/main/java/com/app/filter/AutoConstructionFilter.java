package com.app.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
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

        // 1. Détection de l'action utilisateur (Navigation) vs Action Navigateur
        // (Assets)
        String acceptHeader = httpRequest.getHeader("Accept");

        // Si le navigateur ne demande pas explicitement du HTML, on ne s'interpose
        // JAMAIS.
        // Cela exclut d'office les scripts, images, polices, styles, et requêtes
        // AJAX/JSON.
        if (acceptHeader == null || !acceptHeader.contains("text/html")) {
            chain.doFilter(request, response);
            return;
        }

        // 2. On ignore les méthodes autres que GET pour l'affichage de courtoisie
        if (!"GET".equalsIgnoreCase(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        ResponseSpyWrapper responseSpy = new ResponseSpyWrapper(httpResponse);

        // Exécution de la route
        chain.doFilter(httpRequest, responseSpy);

        // 3. Traitement du signal d'interception pour les pages HTML uniquement
        if (responseSpy.shouldIntercept()) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            httpResponse.setContentType("text/html;charset=UTF-8");

            // Propulsion de la page d'attente dans le layout général
            httpRequest.setAttribute("view", "/WEB-INF/jsp/modules/shared/not-found.jsp");
            httpRequest.getRequestDispatcher("/WEB-INF/jsp/layouts/base-layout.jsp").forward(httpRequest, httpResponse);
        }
    }

    @Override
    public void destroy() {
    }

    /**
     * Wrapper espion universel (Gère les flux texte et binaires)
     */
    private static class ResponseSpyWrapper extends HttpServletResponseWrapper {
        private boolean actionTaken = false;
        private int interceptedStatus = 200;

        public ResponseSpyWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            this.actionTaken = true; // Utilisé par les Servlets et JSP (Texte)
            return super.getWriter();
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            this.actionTaken = true; // Utilisé par Tomcat pour les fichiers statiques (Binaire)
            return super.getOutputStream();
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

        public boolean shouldIntercept() {
            return !this.actionTaken || this.interceptedStatus == 404;
        }
    }
}