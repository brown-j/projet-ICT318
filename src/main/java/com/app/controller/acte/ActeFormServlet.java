package com.app.controller.acte;

import com.app.jpa.config.JPAConfig;
import com.app.jpa.dao.JPADao;
import com.app.jpa.model.ActeEtatCivil;
import com.app.jpa.model.Citoyen;
import com.app.jpa.model.OfficierEtatCivil;
import com.app.jpa.model.TypeActe;
import com.app.jpa.model.JPAEnum.StatutActe;
import com.app.util.FileManager; // 💡 AJOUT : Import du gestionnaire de fichiers

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig; // 💡 AJOUT : Import pour la gestion multipart
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part; // 💡 AJOUT : Import pour récupérer le fichier binaire

import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/acte/formulaire")
// 💡 CORRECTION 1 : Configuration des limites de tailles pour le téléversement
// de l'acte numérisé
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1, // 1 Mo
        maxFileSize = 1024 * 1024 * 15, // 15 Mo maximum par acte
        maxRequestSize = 1024 * 1024 * 20 // 20 Mo maximum par requête globale
)
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
            String observations = request.getParameter("observations");

            // 💡 CORRECTION 2 : Récupération du flux binaire au lieu d'un paramètre textuel
            // de formulaire
            Part filePart = request.getPart("fichierPdf");

            boolean isModification = (idStr != null && !idStr.trim().isEmpty());

            // Objets de relations nécessaires à la création/mise à jour
            TypeActe typeActe = (idTypeActeStr != null && !idTypeActeStr.isEmpty())
                    ? jpa.typeActe.findInCache(idTypeActeStr)
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

                    // 💡 CORRECTION 3 : Traitement du remplacement de fichier pour les "actes"
                    String ancienFichierPdf = acteExistant.getFichierPdf();
                    String fichierPdfFinal = FileManager.replace(ancienFichierPdf, filePart, "actes");
                    acteExistant.setFichierPdf(fichierPdfFinal);

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
                // 💡 CORRECTION 4 : Enregistrement initial du fichier physique sous le dossier
                // "actes"
                String fichierPdfFinal = FileManager.replace(null, filePart, "actes");

                // Le numéro final est désormais garanti non-null, `validateRequiredFields`
                // laissera passer l'entité !
                ActeEtatCivil nouvelActe = jpa.acte.create(
                        numeroActe,
                        typeActe,
                        principal,
                        officier,
                        LocalDate.parse(dateEvenementStr),
                        LocalDate.parse(dateEtablissementStr),
                        lieuEvenement.trim(),
                        (statutStr != null && !statutStr.isEmpty()) ? StatutActe.valueOf(statutStr) : null,
                        fichierPdfFinal); // injection de la référence générée par FileManager

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
}