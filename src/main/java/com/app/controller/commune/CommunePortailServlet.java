package com.app.controller.commune;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.app.jpa.config.JPAConfig;
import com.app.jpa.dao.JPADao;
import com.app.jpa.model.Citoyen;
import com.app.jpa.model.DemandeAdministrative;
import com.app.jpa.model.TypeActe;
import com.app.jpa.model.JPAEnum.StatutDemande;
import com.app.jpa.model.JPAEnum.PrioriteDemande;
import com.app.model.icon.Icons;
import com.app.model.theme.ThemeColor;
import com.app.model.viewmodel.TypeDemandeViewModel;
import com.app.model.viewmodel.DossierSuiviViewModel;
import com.app.model.viewmodel.TimelineStep;
import com.app.ui.PortalUiFactory;

@WebServlet(value = "/portal/commune", loadOnStartup = 2)
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2 MB
        maxFileSize = 1024 * 1024 * 10, // 10 MB
        maxRequestSize = 1024 * 1024 * 50 // 50 MB
)
public class CommunePortailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        EntityManager em = JPAConfig.getEntityManager();
        Gson gson = new Gson();

        try {
            JPADao dao = new JPADao(em);

            // =========================================================================
            // 1. GESTION CENTRALISÉE DES MESSAGES D'ERREUR (Issus du POST)
            // =========================================================================
            if (request.getSession().getAttribute("errorMsg") != null) {
                request.setAttribute("errorMsg", request.getSession().getAttribute("errorMsg"));
                request.getSession().removeAttribute("errorMsg"); // On nettoie après affichage
            }

            // =========================================================================
            // 2. CHARGEMENT ET SÉRIALISATION DU REGISTRE DES PRESTATIONS
            // =========================================================================
            List<TypeActe> listeTypesBdd = new ArrayList<>(dao.typeActe.getCache().values());

            List<TypeDemandeViewModel> typesDemandeVm = listeTypesBdd.stream().map(type -> {
                Icons icon = Icons.FILE_TEXT;
                ThemeColor theme = ThemeColor.PRIMARY;
                List<String> pieces = new ArrayList<>(
                        Arrays.asList("Copie d'Acte de Naissance", "Pièce d'Identité du requérant"));

                if (type.getCode().contains("CELIBAT")) {
                    icon = Icons.USER_CHECK;
                    theme = ThemeColor.INFO;
                    pieces.add("Certificat de résidence de moins de 3 mois");
                } else if (type.getCode().contains("CONSTRUIRE")) {
                    icon = Icons.EYE;
                    theme = ThemeColor.WARNING;
                    pieces.add("Plan de situation de la propriété");
                    pieces.add("Titre de propriété foncier");
                }

                return new TypeDemandeViewModel(
                        type.getCode(),
                        type.getLibelle(),
                        type.getCategorieParent().name(),
                        "72 heures ouvrées",
                        pieces,
                        icon,
                        theme);
            }).collect(Collectors.toList());

            request.setAttribute("typesDemande", typesDemandeVm);
            request.setAttribute("typesDemandeJson", gson.toJson(typesDemandeVm));

            // =========================================================================
            // 3. LOGIQUE DE RECHERCHE ET SUIVI SYNCHRONE (ONGLET SUIVI)
            // =========================================================================
            String numeroSuivi = request.getParameter("numeroSuivi");

            if (numeroSuivi != null && !numeroSuivi.trim().isEmpty()) {
                DemandeAdministrative demande = dao.demande.findFirst("e.numeroSuivi = ?1", numeroSuivi);
                if (demande != null) {
                    DossierSuiviViewModel suiviVm = new DossierSuiviViewModel(
                            demande.getNumeroSuivi(),
                            demande.getTypeActe().getLibelle(),
                            demande.getCitoyenRequerant().getNom() + " " + demande.getCitoyenRequerant().getPrenom(),
                            "Déposé le " + demande.getDateSoumission().toString(),
                            "Sous 3 jours",
                            demande.getStatut().getLibelle(),
                            demande.getDocumentFinal(),
                            demande.getStatut() == StatutDemande.REJETEE ? demande.getMotifRejet() : null);

                    // Frise chronologique (Timeline)
                    suiviVm.addStep(new TimelineStep(Icons.FILE_PLUS, ThemeColor.SUCCESS,
                            "Dossier soumis par le citoyen", "Enregistré", "Votre demande a été enregistrée."));
                    if (demande.getStatut() != StatutDemande.SOUMISE) {
                        suiviVm.addStep(new TimelineStep(Icons.EYE,
                                demande.getStatut() == StatutDemande.REJETEE ? ThemeColor.ERROR : ThemeColor.SUCCESS,
                                "Vérification des pièces", "Traité", "Contrôle de conformité effectué."));
                    } else {
                        suiviVm.addStep(new TimelineStep(Icons.EYE, ThemeColor.PRIMARY, "Vérification des pièces",
                                "En cours", "Examen en cours."));
                    }
                    if (demande.getStatut() == StatutDemande.VALIDEE || demande.getStatut() == StatutDemande.CLOTUREE) {
                        suiviVm.addStep(new TimelineStep(Icons.USER_CHECK, ThemeColor.SUCCESS, "Approbation", "Terminé",
                                "L'acte est finalisé."));
                    }

                    request.setAttribute("dossierHtml", PortalUiFactory.genererFicheSuiviHtml(suiviVm));
                } else {
                    request.setAttribute("errorMsg", "Aucun dossier trouvé pour la référence '" + numeroSuivi + "'.");
                }
            }

            request.getRequestDispatcher("/WEB-INF/jsp/modules/portal/commune.jsp").forward(request, response);

        } finally {
            if (em != null && em.isOpen())
                em.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getServletPath();

        if (pathInfo.contains("soumettre") || request.getParameter("typeDemandeCode") != null) {
            EntityManager em = JPAConfig.getEntityManager();
            EntityTransaction tx = em.getTransaction();

            try {
                tx.begin();
                JPADao dao = new JPADao(em);

                // 1. Extraction des paramètres
                String typeDemandeCode = request.getParameter("typeDemandeCode");
                String ninRaw = request.getParameter("nin");
                String motif = request.getParameter("motif");
                String prioriteParam = request.getParameter("priorite");
                Part fichierJoint = request.getPart("documentsJoints");

                // 2. 💡 CORRECTION DU NIN : Formatage intelligent (ajout du CM- si le front ne
                // l'a pas mis)
                if (ninRaw == null || ninRaw.trim().isEmpty()) {
                    throw new IllegalArgumentException("Le NIN est obligatoire.");
                }
                String ninFormate = ninRaw.trim().toUpperCase();
                if (!ninFormate.startsWith("CM-")) {
                    ninFormate = "CM-" + ninFormate;
                }

                // 3. Recherche stricte du Citoyen
                Citoyen requerant = dao.citoyen.findFirst("e.nin = ?1", ninFormate);

                if (requerant == null) {
                    request.getSession().setAttribute("errorMsg", "Le NIN saisi (" + ninFormate
                            + ") n'existe pas dans la base de données de l'état civil. Veuillez vérifier votre carte d'identité.");
                    response.sendRedirect(request.getContextPath() + "/portal/commune?tab=demande");
                    return;
                }

                // 4. Recherche du Type d'Acte
                TypeActe typeActe = dao.typeActe.getCache().get(typeDemandeCode);
                if (typeActe == null) {
                    throw new IllegalArgumentException("Le type de document sélectionné est invalide.");
                }

                PrioriteDemande priorite = "URGENTE".equalsIgnoreCase(prioriteParam) ? PrioriteDemande.URGENTE
                        : PrioriteDemande.NORMALE;
                String nomFichierSauvegarde = (fichierJoint != null && fichierJoint.getSize() > 0)
                        ? "upload_" + System.currentTimeMillis() + ".pdf"
                        : null;

                DemandeAdministrative d = dao.demande.create(
                        "", // auto generate et audit
                        typeActe,
                        requerant,
                        LocalDateTime.now(),
                        StatutDemande.SOUMISE,
                        priorite, // Utilisation de la priorité sélectionnée
                        nomFichierSauvegarde);
                tx.commit();

                // Succès absolu : On redirige vers le suivi
                response.sendRedirect(
                        request.getContextPath() + "/portal/commune?tab=suivi&numeroSuivi=" + d.getNumeroSuivi()
                                + "&success=true");

            } catch (Exception e) {
                if (tx != null && tx.isActive())
                    tx.rollback();
                e.printStackTrace();
                request.getSession().setAttribute("errorMsg",
                        "Erreur lors du traitement de votre dossier : " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/portal/commune?tab=demande");
            } finally {
                if (em != null && em.isOpen())
                    em.close();
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/portal/commune");
        }
    }
}