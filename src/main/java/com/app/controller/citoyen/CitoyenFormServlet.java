package com.app.controller.citoyen;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.io.IOException;
import java.time.LocalDate;

import com.app.jpa.config.JPAConfig;
import com.app.jpa.model.Citoyen;
import com.app.jpa.model.JPAEnum.Sexe;
import com.app.jpa.model.JPAEnum.SituationMatrimoniale;
import com.app.jpa.model.JPAEnum.StatutCitoyen;

@WebServlet("/citoyen/formulaire")
public class CitoyenFormServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        String nin = request.getParameter("nin");
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String dateNaissanceParam = request.getParameter("dateNaissance");
        String lieuNaissance = request.getParameter("lieuNaissance");
        String sexeParam = request.getParameter("sexe");
        String adresse = request.getParameter("adresse");
        String telephone = request.getParameter("telephone");
        String email = request.getParameter("email");
        String situationParam = request.getParameter("situationMatrimoniale");
        String statutParam = request.getParameter("statut");

        Citoyen citoyen = new Citoyen();
        boolean isModification = (idParam != null && !idParam.trim().isEmpty());

        if (isModification) {
            citoyen.setId(Long.parseLong(idParam));
        }

        // nul check
        if (nin == null || nin.trim().isEmpty()) {
            nin = "";
        }

        citoyen.setNin(nin.trim().toUpperCase());
        citoyen.setNom(nom.trim().toUpperCase());
        citoyen.setPrenom(prenom.trim());
        citoyen.setDateNaissance(LocalDate.parse(dateNaissanceParam));
        citoyen.setLieuNaissance(lieuNaissance.trim());
        citoyen.setSexe(Sexe.valueOf(sexeParam));
        citoyen.setAdresse(adresse.trim());

        citoyen.setTelephone(telephone != null && !telephone.trim().isEmpty() ? telephone.trim() : null);
        citoyen.setEmail(email != null && !email.trim().isEmpty() ? email.trim().toLowerCase() : null);

        citoyen.setSituationMatrimoniale(SituationMatrimoniale.valueOf(situationParam));
        citoyen.setStatut(StatutCitoyen.valueOf(statutParam));

        EntityManager em = null;
        EntityTransaction tx = null;

        try {
            em = JPAConfig.getEntityManager();
            tx = em.getTransaction();
            tx.begin();

            if (isModification) {
                em.merge(citoyen);
                System.out.println("Mise à jour effectuée en BDD pour le citoyen : " + citoyen.getNom());
            } else {
                em.persist(citoyen);
                System.out.println("Nouveau citoyen inséré avec succès en BDD : " + citoyen.getNom());
            }

            tx.commit();

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            throw new ServletException("Erreur technique lors de l'enregistrement du citoyen.", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        // Redirection PRG vers la liste qui rouvrira ta vue principale avec la base de
        // données mise à jour
        response.sendRedirect(request.getContextPath() + "/citoyen/liste");
    }
}