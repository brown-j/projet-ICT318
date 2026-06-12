package com.app.controller.officier;

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
import java.nio.file.Paths;
import java.time.LocalDate;

import com.app.jpa.config.JPAConfig;
import com.app.jpa.model.OfficierEtatCivil;
import com.app.jpa.model.JPAEnum.Role;
import com.app.jpa.model.JPAEnum.StatutOfficier;
import com.app.util.HashUtil;

@WebServlet("/officier/formulaire")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1, // 1 Mo
        maxFileSize = 1024 * 1024 * 5, // 5 Mo maximum par fichier
        maxRequestSize = 1024 * 1024 * 10 // 10 Mo maximum par requête globale
)
public class OfficierFormServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String titre = request.getParameter("titre");
        String service = request.getParameter("service");
        String matricule = request.getParameter("matricule");
        String datePriseFonctionParam = request.getParameter("datePriseFonction");
        String email = request.getParameter("email");
        String telephone = request.getParameter("telephone");
        String motDePasse = request.getParameter("motDePasse");
        String roleParam = request.getParameter("role");
        String statutParam = request.getParameter("statut");

        boolean isModification = (idParam != null && !idParam.trim().isEmpty());
        EntityManager em = null;
        EntityTransaction tx = null;

        try {
            em = JPAConfig.getEntityManager();
            tx = em.getTransaction();
            tx.begin();

            OfficierEtatCivil officier;

            if (isModification) {
                // 1. Charger l'objet existant directement attaché à la session
                officier = em.find(OfficierEtatCivil.class, Long.parseLong(idParam));
                if (officier == null) {
                    throw new ServletException("Officier introuvable pour l'ID : " + idParam);
                }
            } else {
                // Créer une nouvelle instance si c'est une insertion
                officier = new OfficierEtatCivil();
            }

            // 2. Hydratation / Mise à jour des champs communs
            officier.setNom(nom.trim().toUpperCase());
            officier.setPrenom(prenom.trim());
            officier.setTitre(titre.trim());
            officier.setService(service.trim());
            officier.setMatricule(matricule.trim().toUpperCase());
            officier.setDatePriseFonction(LocalDate.parse(datePriseFonctionParam));
            officier.setEmail(email.trim().toLowerCase());
            officier.setTelephone(telephone != null && !telephone.trim().isEmpty() ? telephone.trim() : null);
            officier.setRole(Role.valueOf(roleParam));
            officier.setStatut(StatutOfficier.valueOf(statutParam));

            // 3. Gestion intelligente du mot de passe (uniquement si saisi)
            if (motDePasse != null && !motDePasse.trim().isEmpty()) {
                officier.setMotDePasse(HashUtil.hashPassword(motDePasse.trim()));
            }

            // 4. Traitement du fichier de signature numérique
            Part filePart = request.getPart("signatureFile");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String extension = fileName.substring(fileName.lastIndexOf("."));
                String uniqueImageName = "sig_" + officier.getMatricule() + "_" + System.currentTimeMillis()
                        + extension;
                officier.setSignatureNumerique(uniqueImageName);
            }

            // 5. Persistance ou synchronisation automatique
            if (isModification) {
                // Pas besoin de em.merge() explicite car l'objet est managé,
                // Hibernate détecte les changements lors du commit.
                System.out.println("Mise à jour effectuée en BDD pour l'officier : " + officier.getNom());
            } else {
                em.persist(officier);
                System.out.println("Nouvel officier inséré avec succès en BDD : " + officier.getNom());
            }

            tx.commit();

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            throw new ServletException("Erreur technique lors de l'enregistrement de l'officier.", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        // Redirection PRG (Post-Redirect-Get) vers la liste
        response.sendRedirect(request.getContextPath() + "/officier/liste");
    }

}