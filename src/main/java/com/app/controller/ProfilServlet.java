// Path: src/main/java/com/exemple/controller/ProfilServlet.java
package com.app.controller;

import com.app.models.Article;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/profil") // L'URL pour appeler cette servlet
public class ProfilServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Donnée simple (String)
        String nomUtilisateur = "Alice";

        // 2. Liste de JavaBeans (Articles du panier)
        List<Article> panier = new ArrayList<>();
        panier.add(new Article("Ordinateur portable", 899.99, false));
        panier.add(new Article("Souris sans fil", 25.00, true)); // En promo !
        panier.add(new Article("Clavier mécanique", 75.50, false));

        // 3. Stockage des données dans les attributs de la requête
        request.setAttribute("nom", nomUtilisateur);
        request.setAttribute("listeArticles", panier);

        // 4. Redirection vers la vue (JSP)
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/profil.jsp");
        dispatcher.forward(request, response);
    }
}