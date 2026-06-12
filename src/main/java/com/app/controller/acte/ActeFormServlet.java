package com.app.controller.acte;

import com.app.jpa.config.JPAConfig;
import com.app.jpa.model.ActeEtatCivil;
import com.app.jpa.model.Citoyen;
import com.app.jpa.model.OfficierEtatCivil;
import com.app.jpa.model.JPAEnum.StatutActe;
import com.app.jpa.model.JPAEnum.TypeActe;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Random;

@WebServlet("/acte/formulaire")
public class ActeFormServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Utilisation directe de ta configuration centralisée
    EntityManager em = JPAConfig.getEntityManager();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // 1. Récupération et parsing des paramètres du formulaire
            String idStr = request.getParameter("id");
            String numeroActe = request.getParameter("numeroActe");
            String idTypeActeStr = request.getParameter("idTypeActe");
            String idCitoyenPrincipalStr = request.getParameter("idCitoyenPrincipal");
            String idCitoyenSecondaireStr = request.getParameter("idCitoyenSecondaire");
            String idOfficierStr = request.getParameter("idOfficier");
            String dateEvenementStr = request.getParameter("dateEvenement");
            String dateEtablissementStr = request.getParameter("dateEtablissement");
            String lieuEvenement = request.getParameter("lieuEvenement");
            String statutStr = request.getParameter("statut");
            String fichierPdf = request.getParameter("fichierPdf");
            String observations = request.getParameter("observations");

            // 2. Détermination du mode : Création ou Modification
            ActeEtatCivil acte;
            if (idStr != null && !idStr.trim().isEmpty()) {
                acte = em.find(ActeEtatCivil.class, Long.parseLong(idStr));
            } else {
                acte = new ActeEtatCivil();
            }

            // 3. Chargement et liaison des entités associées (Relations JPA)
            if (idTypeActeStr != null && !idTypeActeStr.isEmpty()) {
                acte.setTypeActe(em.find(TypeActe.class, Long.parseLong(idTypeActeStr)));
            }
            if (idCitoyenPrincipalStr != null && !idCitoyenPrincipalStr.isEmpty()) {
                acte.setCitoyenPrincipal(em.find(Citoyen.class, Long.parseLong(idCitoyenPrincipalStr)));
            }

            // Le citoyen secondaire est optionnel (ex: pas de conjoint pour un acte de
            // naissance)
            if (idCitoyenSecondaireStr != null && !idCitoyenSecondaireStr.trim().isEmpty()) {
                acte.setCitoyenSecondaire(em.find(Citoyen.class, Long.parseLong(idCitoyenSecondaireStr)));
            } else {
                acte.setCitoyenSecondaire(null);
            }

            if (idOfficierStr != null && !idOfficierStr.isEmpty()) {
                acte.setOfficierSignataire(em.find(OfficierEtatCivil.class, Long.parseLong(idOfficierStr)));
            }

            // 4. Assignation des champs simples et des dates
            acte.setDateEvenement(LocalDate.parse(dateEvenementStr));
            acte.setDateEtablissement(LocalDate.parse(dateEtablissementStr));
            acte.setLieuEvenement(lieuEvenement);
            acte.setObservations(observations);
            acte.setFichierPdf(fichierPdf != null && !fichierPdf.trim().isEmpty() ? fichierPdf : null);

            if (statutStr != null && !statutStr.isEmpty()) {
                acte.setStatut(StatutActe.valueOf(statutStr));
            }

            // 5. Gestion de la génération automatique du numéro de l'acte
            if (numeroActe == null || numeroActe.trim().isEmpty()) {
                acte.setNumeroActe(genererNumeroActe(em));
            } else {
                acte.setNumeroActe(numeroActe.trim());
            }

            // 6. Persistance en Base de données
            if (acte.getId() == null) {
                em.persist(acte);
            } else {
                em.merge(acte);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            throw new ServletException("Erreur lors de l'enregistrement de l'acte civil", e);
        } finally {
            em.close();
        }

        // 7. Pattern PRG : Redirection vers la liste pour éviter les doubles
        // soumissions
        response.sendRedirect(request.getContextPath() + "/acte/liste");
    }

    /**
     * Génère un numéro d'acte unique au format officiel : ACT-2026-XXXXX
     */
    private String genererNumeroActe(EntityManager em) {
        int anneeCourante = LocalDate.now().getYear(); // Sera 2026
        Random random = new Random();
        String numeroGenere;
        boolean existe;

        // Boucle de sécurité pour s'assurer de l'unicité stricte en BDD
        do {
            int uniqueId = 10000 + random.nextInt(90000); // Génère un nombre à 5 chiffres (10000 à 99999)
            numeroGenere = "ACT-" + anneeCourante + "-" + uniqueId;

            Long count = em.createQuery("SELECT COUNT(a) FROM ActeEtatCivil a WHERE a.numeroActe = :num", Long.class)
                    .setParameter("num", numeroGenere)
                    .getSingleResult();
            existe = count > 0;
        } while (existe);

        return numeroGenere;
    }
}