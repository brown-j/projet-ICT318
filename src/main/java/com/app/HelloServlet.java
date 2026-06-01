package com.app;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/hello") // 🏷️ L'annotation qui donne l'adresse
public class HelloServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        // ✍️ On écrit la réponse directement pour le navigateur
        resp.setContentType("text/html");
        resp.getWriter().println("<h1>Bonjour depuis Jakarta EE et Tomcat ! 🚀</h1>");
    }
}