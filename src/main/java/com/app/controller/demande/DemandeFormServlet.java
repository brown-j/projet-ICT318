package com.app.controller.demande;

import com.app.jpa.config.JPAConfig;
import com.app.jpa.dao.JPADao;
import com.app.jpa.model.Citoyen;
import com.app.jpa.model.DemandeAdministrative;
import com.app.jpa.model.JPAEnum.ModePaiement;
import com.app.jpa.model.JPAEnum.PrioriteDemande;
import com.app.jpa.model.JPAEnum.StatutDemande;
import com.app.jpa.model.JPAEnum.TypeDemande;
import com.app.jpa.model.OfficierEtatCivil;

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

            // 1. Récupération des paramètres du formulaire
            String idStr = request.getParameter("id");
            String statutStr = request.getParameter("statut");
            String typeDemandeStr = request.getParameter("typeDemande");
            String idCitoyenStr = request.getParameter("idCitoyenPrincipal");
            String prioriteStr = request.getParameter("priorite");
            String documentFinal = request.getParameter("documentFinal");

            // 2. MODE CRÉATION (Pas d'ID)
            if (idStr == null || idStr.trim().isEmpty()) {

                TypeDemande type = typeDemandeStr != null ? TypeDemande.valueOf(typeDemandeStr) : null;
                PrioriteDemande priorite = prioriteStr != null ? PrioriteDemande.valueOf(prioriteStr) : null;
                Citoyen requerant = idCitoyenStr != null ? dao.citoyen.findUnique(Long.parseLong(idCitoyenStr)) : null;

                dao.demande.create(
                        null,
                        type,
                        requerant,
                        LocalDateTime.now(),
                        StatutDemande.SOUMISE,
                        priorite,
                        documentFinal);

                // Adaptation pour le Toast
                request.getSession().setAttribute("toastMsg", "Demande créée avec succès.");
                request.getSession().setAttribute("toastType", "success");

            }
            // 3. MODE MISE À JOUR & CHANGEMENT D'ÉTAT (ID existant)
            else {
                Long id = Long.parseLong(idStr);
                DemandeAdministrative demande = dao.demande.findUnique(id);

                if (demande != null) {
                    if (typeDemandeStr != null)
                        demande.setTypeDemande(TypeDemande.valueOf(typeDemandeStr));
                    if (prioriteStr != null)
                        demande.setPriorite(PrioriteDemande.valueOf(prioriteStr));
                    if (idCitoyenStr != null)
                        demande.setCitoyenRequerant(dao.citoyen.findUnique(Long.parseLong(idCitoyenStr)));
                    if (documentFinal != null && !documentFinal.trim().isEmpty())
                        demande.setDocumentFinal(documentFinal.trim());

                    if (statutStr != null && !statutStr.isEmpty()) {
                        StatutDemande nouveauStatut = StatutDemande.valueOf(statutStr);

                        if (demande.getStatut() != nouveauStatut) {
                            switch (nouveauStatut) {
                                case EN_COURS:
                                    dao.demande.marquerEnCours(demande);
                                    break;
                                case VALIDEE:
                                    dao.demande.valider(demande);
                                    break;
                                case REJETEE:
                                    dao.demande.rejeter(demande);
                                    break;
                                case CLOTUREE:
                                    OfficierEtatCivil officierConnecte = (OfficierEtatCivil) request.getSession()
                                            .getAttribute("user");

                                    dao.demande.cloturer(
                                            demande,
                                            documentFinal,
                                            officierConnecte,
                                            ModePaiement.ESPECES);
                                    break;
                                default:
                                    demande.setStatut(nouveauStatut);
                                    dao.demande.update(demande);
                            }

                            // Adaptation pour le Toast
                            request.getSession().setAttribute("toastMsg",
                                    "Demande mise à jour et statut passé à " + nouveauStatut.name());
                            request.getSession().setAttribute("toastType", "success");
                        } else {
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
            request.getSession().setAttribute("toastType", "warning"); // warning ou error selon ton envie
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