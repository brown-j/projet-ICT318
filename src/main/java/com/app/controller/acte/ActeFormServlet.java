package com.app.controller.acte;

import com.app.jpa.config.JPAConfig;
import com.app.jpa.dao.JPADao;
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

@WebServlet("/acte/formulaire")
public class ActeFormServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Instanciation locale des ressources d'accès aux données
        EntityManager em = JPAConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // Initialisation de ton mini-ORM Prisma-like
            JPADao jpa = new JPADao(em);

            // Récupération et parsing des paramètres du formulaire
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

            boolean isModification = (idStr != null && !idStr.trim().isEmpty());

            // Objets de relations nécessaires à la création/mise à jour
            TypeActe typeActe = (idTypeActeStr != null && !idTypeActeStr.isEmpty()) ? TypeActe.valueOf(idTypeActeStr)
                    : null;
            Citoyen principal = (idCitoyenPrincipalStr != null && !idCitoyenPrincipalStr.isEmpty())
                    ? jpa.citoyen.findUnique(Long.parseLong(idCitoyenPrincipalStr))
                    : null;
            OfficierEtatCivil officier = (idOfficierStr != null && !idOfficierStr.isEmpty())
                    ? jpa.officier.findUnique(Long.parseLong(idOfficierStr))
                    : null;

            if (isModification) {
                // 🔄 MODE MODIFICATION : On récupère l'entité managée
                ActeEtatCivil acteExistant = jpa.acte.findUnique(Long.parseLong(idStr));

                if (acteExistant != null) {
                    acteExistant.setNumeroActe(numeroActe != null && !numeroActe.trim().isEmpty() ? numeroActe.trim()
                            : acteExistant.getNumeroActe());
                    acteExistant.setTypeActe(typeActe);
                    acteExistant.setCitoyenPrincipal(principal);
                    acteExistant.setOfficierSignataire(officier);

                    acteExistant.setDateEvenement(LocalDate.parse(dateEvenementStr));
                    acteExistant.setDateEtablissement(LocalDate.parse(dateEtablissementStr));
                    acteExistant.setLieuEvenement(lieuEvenement.trim());
                    acteExistant.setObservations(observations);
                    acteExistant.setFichierPdf(
                            fichierPdf != null && !fichierPdf.trim().isEmpty() ? fichierPdf.trim() : null);

                    if (statutStr != null && !statutStr.isEmpty()) {
                        acteExistant.setStatut(StatutActe.valueOf(statutStr));
                    }

                    // Liaison dynamique du citoyen secondaire (optionnel)
                    if (idCitoyenSecondaireStr != null && !idCitoyenSecondaireStr.trim().isEmpty()) {
                        acteExistant
                                .setCitoyenSecondaire(jpa.citoyen.findUnique(Long.parseLong(idCitoyenSecondaireStr)));
                    } else {
                        acteExistant.setCitoyenSecondaire(null);
                    }

                    // Envoi de la mise à jour via le delegate
                    jpa.acte.update(acteExistant);
                }
            } else {
                // ✨ MODE CRÉATION

                // ⚡ RECTIFICATION ICI : Résolution préventive du numéro de l'acte avant l'envoi
                // au validateur
                String numeroFinal;
                if (numeroActe == null || numeroActe.trim().isEmpty()) {
                    numeroFinal = genererNumeroActe(em);
                } else {
                    numeroFinal = numeroActe.trim();
                }

                // Le numéro final est désormais garanti non-null, `validateRequiredFields`
                // laissera passer l'entité !
                ActeEtatCivil nouvelActe = jpa.acte.create(
                        numeroFinal,
                        typeActe,
                        principal,
                        officier,
                        LocalDate.parse(dateEvenementStr),
                        LocalDate.parse(dateEtablissementStr),
                        lieuEvenement.trim(),
                        (statutStr != null && !statutStr.isEmpty()) ? StatutActe.valueOf(statutStr) : null,
                        fichierPdf != null && !fichierPdf.trim().isEmpty() ? fichierPdf.trim() : null);

                // Assignation des champs spécifiques / optionnels
                nouvelActe.setObservations(observations);

                if (idCitoyenSecondaireStr != null && !idCitoyenSecondaireStr.trim().isEmpty()) {
                    nouvelActe.setCitoyenSecondaire(jpa.citoyen.findUnique(Long.parseLong(idCitoyenSecondaireStr)));
                }
            }

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            throw new ServletException("Erreur lors de l'enregistrement de l'acte civil via JPADao: " + e.getMessage(),
                    e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        // Pattern PRG
        response.sendRedirect(request.getContextPath() + "/acte/liste");
    }

    /**
     * Génère un numéro d'acte unique au format officiel : ACT-2026-XXXXX
     */
    private String genererNumeroActe(EntityManager em) {
        int anneeCourante = LocalDate.now().getYear(); // Est configuré sur 2026
        java.util.Random random = new java.util.Random();
        String numeroGenere;
        boolean existe;

        do {
            int uniqueId = 10000 + random.nextInt(90000); // Nombre à 5 chiffres
            numeroGenere = "ACT-" + anneeCourante + "-" + uniqueId;

            Long count = em.createQuery("SELECT COUNT(a) FROM ActeEtatCivil a WHERE a.numeroActe = :num", Long.class)
                    .setParameter("num", numeroGenere)
                    .getSingleResult();
            existe = count > 0;
        } while (existe);

        return numeroGenere;
    }
}