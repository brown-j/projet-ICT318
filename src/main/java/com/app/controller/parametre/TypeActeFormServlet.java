package com.app.controller.parametre;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.io.IOException;

import com.app.jpa.config.JPAConfig;
import com.app.jpa.dao.JPADao;
import com.app.jpa.model.TypeActe;
import com.app.jpa.model.JPAEnum.CategorieActe;
import com.app.util.FileManager;

@WebServlet("/parametre/formulaire")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1, // 1 Mo
        maxFileSize = 1024 * 1024 * 15, // 15 Mo maximum par template
        maxRequestSize = 1024 * 1024 * 20 // 20 Mo maximum par requête globale
)
public class TypeActeFormServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Récupération des paramètres du formulaire
        String code = request.getParameter("code");
        String libelle = request.getParameter("libelle");
        String tarifParam = request.getParameter("tarifFCFA");
        String categorieParentParam = request.getParameter("categorieParent");
        String description = request.getParameter("description");

        // Validation de garde
        if (code == null || code.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Le code unique de la prestation est obligatoire.");
            return;
        }

        EntityManager em = null;
        EntityTransaction tx = null;

        try {
            em = JPAConfig.getEntityManager();
            tx = em.getTransaction();
            tx.begin();

            JPADao jpa = new JPADao(em);

            // 2. Récupération sécurisée du code clé et de l'état en cache
            String codeCle = code.trim().toUpperCase();
            TypeActe existant = jpa.typeActe.findInCache(codeCle);
            String ancienTemplatePath = (existant != null) ? existant.getTemplatePath() : null;

            // 3. Utilisation de la fonction atomique REPLACE
            // Supprime l'ancien du disque s'il existe ET uploade le nouveau (renvoie le
            // nouvel UUID).
            // Si aucun fichier n'est soumis, retourne simplement 'ancienTemplatePath'.
            Part filePart = request.getPart("templatePath");
            String templatePathFinal = FileManager.replace(ancienTemplatePath, filePart, "templates");

            // 4. Conversion des types
            int tarif = (tarifParam != null) ? Integer.parseInt(tarifParam.trim()) : 0;
            CategorieActe categorie = (categorieParentParam != null) ? CategorieActe.valueOf(categorieParentParam)
                    : null;

            // 5. Appel unique de ta méthode métier Prisma-like !
            jpa.typeActe.saveOrUpdate(
                    codeCle,
                    libelle != null ? libelle.trim() : "",
                    tarif,
                    templatePathFinal, // Contient l'UUID mis à jour ou préservé
                    categorie,
                    description != null ? description.trim() : null);

            tx.commit();

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            throw new ServletException(
                    "Erreur lors de l'enregistrement de la configuration via saveOrUpdate : " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        // 6. Redirection PRG (Post-Redirect-Get)
        response.sendRedirect(request.getContextPath() + "/parametre/configuration");
    }
}