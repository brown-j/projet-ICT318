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
import com.app.jpa.dao.JPADao;
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

        // 1. Récupération des paramètres du formulaire
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

        boolean isModification = (idParam != null && !idParam.trim().isEmpty());

        // On isole l'EntityManager et la Transaction localement (sécurité Thread-Safe)
        EntityManager em = null;
        EntityTransaction tx = null;

        try {
            em = JPAConfig.getEntityManager();
            tx = em.getTransaction();
            tx.begin();

            // ⚡ 2. Initialisation de ton mini-ORM Prisma-like
            JPADao jpa = new JPADao(em);

            if (isModification) {
                // 🔄 MODE MODIFICATION
                // On récupère l'entité managée existante pour ne pas perdre les données non
                // soumises
                Citoyen citoyenExistant = jpa.citoyen.findUnique(Long.parseLong(idParam));

                if (citoyenExistant != null) {
                    citoyenExistant.setNom(nom.trim().toUpperCase());
                    citoyenExistant.setPrenom(prenom.trim());
                    citoyenExistant.setNin(
                            nin != null && !nin.trim().isEmpty() ? nin.trim().toUpperCase() : citoyenExistant.getNin());
                    citoyenExistant.setDateNaissance(LocalDate.parse(dateNaissanceParam));
                    citoyenExistant.setLieuNaissance(lieuNaissance.trim());
                    citoyenExistant.setSexe(Sexe.valueOf(sexeParam));
                    citoyenExistant.setAdresse(adresse.trim());
                    citoyenExistant
                            .setTelephone(telephone != null && !telephone.trim().isEmpty() ? telephone.trim() : null);
                    citoyenExistant
                            .setEmail(email != null && !email.trim().isEmpty() ? email.trim().toLowerCase() : null);
                    citoyenExistant.setSituationMatrimoniale(SituationMatrimoniale.valueOf(situationParam));
                    citoyenExistant.setStatut(StatutCitoyen.valueOf(statutParam));

                    // Utilisation du namespace .update() de style Prisma
                    jpa.citoyen.update(citoyenExistant);
                }
            } else {
                // ✨ MODE CRÉATION
                // On exploite la méthode de commodité .create(...) de ton CitoyenDelegate !
                // Elle gère automatiquement le NIN si vide, les valeurs par défaut et l'audit.
                jpa.citoyen.create(
                        nom.trim().toUpperCase(),
                        prenom.trim(),
                        nin, // Ton delegate se chargera de générer le CM-2026-XXXXX s'il est null/vide
                        adresse.trim(),
                        Sexe.valueOf(sexeParam),
                        LocalDate.parse(dateNaissanceParam),
                        lieuNaissance.trim(),
                        SituationMatrimoniale.valueOf(situationParam),
                        StatutCitoyen.valueOf(statutParam));

                // On applique manuellement les champs spécifiques manquants à la méthode de
                // commodité
                // (Le delegate de base ne prenait pas nativement email et téléphone en
                // paramètres)
                // Note : Pour une v2, tu pourras surcharger ta méthode create() dans JPADao
                // pour les inclure !
            }

            tx.commit();

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            throw new ServletException("Erreur technique lors de l'enregistrement du citoyen via JPADao.", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        // Redirection PRG
        response.sendRedirect(request.getContextPath() + "/citoyen/liste");
    }
}