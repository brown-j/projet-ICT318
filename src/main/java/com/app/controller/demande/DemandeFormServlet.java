package com.app.controller.demande;

import com.app.jpa.config.JPAConfig;
import com.app.jpa.dao.JPADao;
import com.app.jpa.model.Citoyen;
import com.app.jpa.model.DemandeAdministrative;
import com.app.jpa.model.JPAEnum.ModePaiement;
import com.app.jpa.model.JPAEnum.PrioriteDemande;
import com.app.jpa.model.JPAEnum.StatutDemande;
import com.app.jpa.model.OfficierEtatCivil;
import com.app.jpa.model.TypeActe;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/demande/formulaire")
public class DemandeFormServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        EntityManager em = JPAConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            JPADao dao = new JPADao(em);

            // 1. Récupération des paramètres du formulaire (Aligné avec DemandeFormFactory)
            String idStr = request.getParameter("id");
            String statutStr = request.getParameter("statut");
            String idTypeActeStr = request.getParameter("idTypeActe"); // Le code du TypeActe (ex: ACTE_NAISS)
            String idCitoyenStr = request.getParameter("idCitoyenPrincipal");
            String prioriteStr = request.getParameter("priorite");
            String documentFinal = request.getParameter("documentFinal");
            String description = request.getParameter("description");
            String motifRejet = request.getParameter("motifRejet");
            String commentaires = request.getParameter("commentaires");

            // 2. MODE CRÉATION (Pas d'ID)
            if (idStr == null || idStr.trim().isEmpty()) {

                TypeActe type = idTypeActeStr != null ? dao.typeActe.findInCache(idTypeActeStr) : null;
                PrioriteDemande priorite = prioriteStr != null ? PrioriteDemande.valueOf(prioriteStr)
                        : PrioriteDemande.NORMALE;
                Citoyen requerant = idCitoyenStr != null ? dao.citoyen.findUnique(Long.parseLong(idCitoyenStr)) : null;

                // Création via le DAO
                DemandeAdministrative nouvelleDemande = dao.demande.create(
                        null,
                        type,
                        requerant,
                        LocalDateTime.now(),
                        StatutDemande.SOUMISE,
                        priorite,
                        documentFinal);

                // Ajout des textes supplémentaires (au cas où le dao.demande.create ne les gère
                // pas dans ses arguments)
                if (nouvelleDemande != null) {
                    nouvelleDemande.setDescription(description);
                    nouvelleDemande.setCommentaires(commentaires);
                    dao.demande.update(nouvelleDemande);
                }

                // Toast Notification
                request.getSession().setAttribute("toastMsg", "Demande créée avec succès.");
                request.getSession().setAttribute("toastType", "success");

            }
            // 3. MODE MISE À JOUR & CHANGEMENT D'ÉTAT (ID existant)
            else {
                Long id = Long.parseLong(idStr);
                DemandeAdministrative demande = dao.demande.findUnique(id);

                if (demande != null) {

                    // --- Mise à jour des informations modifiables ---
                    if (idTypeActeStr != null) {
                        demande.setTypeActe(dao.typeActe.findInCache(idTypeActeStr));
                    }
                    if (prioriteStr != null) {
                        demande.setPriorite(PrioriteDemande.valueOf(prioriteStr));
                    }
                    if (idCitoyenStr != null) {
                        demande.setCitoyenRequerant(dao.citoyen.findUnique(Long.parseLong(idCitoyenStr)));
                    }
                    if (documentFinal != null)
                        demande.setDocumentFinal(documentFinal.trim());
                    if (description != null)
                        demande.setDescription(description.trim());
                    if (motifRejet != null)
                        demande.setMotifRejet(motifRejet.trim());
                    if (commentaires != null)
                        demande.setCommentaires(commentaires.trim());

                    // --- Gestion intelligente du Statut ---
                    if (statutStr != null && !statutStr.isEmpty()) {
                        StatutDemande nouveauStatut = StatutDemande.valueOf(statutStr);
                        OfficierEtatCivil user = (OfficierEtatCivil) request.getSession().getAttribute("user");
                        if (user == null) // on peut verifier le role
                            throw new IllegalStateException("Acces refusé: connexion requise");

                        if (demande.getStatut() != nouveauStatut) {
                            switch (nouveauStatut) {
                                case EN_COURS:
                                    dao.demande.marquerEnCours(demande, user);
                                    break;
                                case VALIDEE:
                                    dao.demande.valider(demande);
                                    break;
                                case REJETEE:
                                    dao.demande.rejeter(demande);
                                    break;
                                case CLOTUREE:
                                    dao.demande.cloturer(demande, user, ModePaiement.ESPECES);
                                    break;
                                default:
                                    demande.setStatut(nouveauStatut);
                                    dao.demande.update(demande);
                            }

                            request.getSession().setAttribute("toastMsg",
                                    "Demande mise à jour et statut passé à " + nouveauStatut.name().replace("_", " "));
                            request.getSession().setAttribute("toastType", "success");
                        } else {
                            // Le statut n'a pas changé, on update juste les autres champs
                            dao.demande.update(demande);
                            request.getSession().setAttribute("toastMsg", "Informations de la demande mises à jour.");
                            request.getSession().setAttribute("toastType", "success");
                        }
                    } else {
                        dao.demande.update(demande);
                        request.getSession().setAttribute("toastMsg", "Informations mises à jour.");
                        request.getSession().setAttribute("toastType", "success");
                    }
                } else {
                    request.getSession().setAttribute("toastMsg", "Erreur : Demande introuvable.");
                    request.getSession().setAttribute("toastType", "error");
                }
            }

            tx.commit();
        } catch (IllegalStateException e) {
            if (tx.isActive())
                tx.rollback();
            // Capture des erreurs de logique métier (ex: Clôture d'une demande non validée)
            request.getSession().setAttribute("toastMsg", e.getMessage());
            request.getSession().setAttribute("toastType", "warning");
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            e.printStackTrace();
            // Capture des crashs techniques
            request.getSession().setAttribute("toastMsg", "Une erreur technique est survenue.");
            request.getSession().setAttribute("toastType", "error");
        } finally {
            if (em.isOpen())
                em.close();
        }

        // 4. Redirection PRG (Post-Redirect-Get)
        response.sendRedirect(request.getContextPath() + "/demande/liste");
    }
}