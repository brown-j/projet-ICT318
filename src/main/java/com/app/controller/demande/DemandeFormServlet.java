package com.app.controller.demande;

import com.app.jpa.config.JPAConfig;
import com.app.jpa.dao.JPADao;
import com.app.jpa.model.Citoyen;
import com.app.jpa.model.DemandeAdministrative;
import com.app.jpa.model.JPAEnum.PrioriteDemande;
import com.app.jpa.model.JPAEnum.StatutDemande;
import com.app.jpa.model.JPAEnum.TypeDemande;

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
            String statutStr = request.getParameter("statut"); // L'action est dictée par le statut choisi
            String typeDemandeStr = request.getParameter("typeDemande");
            String idCitoyenStr = request.getParameter("idCitoyenPrincipal");
            String prioriteStr = request.getParameter("priorite");
            String documentFinal = request.getParameter("documentFinal");

            // 2. MODE CRÉATION (Pas d'ID)
            if (idStr == null || idStr.trim().isEmpty()) {

                TypeDemande type = typeDemandeStr != null ? TypeDemande.valueOf(typeDemandeStr) : null;
                PrioriteDemande priorite = prioriteStr != null ? PrioriteDemande.valueOf(prioriteStr) : null;
                Citoyen requerant = idCitoyenStr != null ? dao.citoyen.findUnique(Long.parseLong(idCitoyenStr)) : null;

                // Création via le DAO (Le numéro de suivi est généré à l'intérieur en passant
                // null)
                dao.demande.create(
                        null,
                        type,
                        requerant,
                        LocalDateTime.now(),
                        StatutDemande.SOUMISE,
                        priorite,
                        documentFinal);
                request.getSession().setAttribute("successMsg", "Demande créée avec succès.");

            }
            // 3. MODE MISE À JOUR & CHANGEMENT D'ÉTAT (ID existant)
            else {
                Long id = Long.parseLong(idStr);
                DemandeAdministrative demande = dao.demande.findUnique(id);

                if (demande != null) {
                    // a) Mise à jour des champs basiques
                    if (typeDemandeStr != null)
                        demande.setTypeDemande(TypeDemande.valueOf(typeDemandeStr));
                    if (prioriteStr != null)
                        demande.setPriorite(PrioriteDemande.valueOf(prioriteStr));
                    if (idCitoyenStr != null)
                        demande.setCitoyenRequerant(dao.citoyen.findUnique(Long.parseLong(idCitoyenStr)));
                    if (documentFinal != null && !documentFinal.trim().isEmpty())
                        demande.setDocumentFinal(documentFinal.trim());

                    // b) Vérification et application du changement d'état
                    if (statutStr != null && !statutStr.isEmpty()) {
                        StatutDemande nouveauStatut = StatutDemande.valueOf(statutStr);

                        // Si le statut a changé, on déclenche les règles métier du DAO
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
                                    dao.demande.cloturer(demande, demande.getDocumentFinal());
                                    break;
                                default:
                                    // Fallback sécurisé (ex: on la remet SOUMISE)
                                    demande.setStatut(nouveauStatut);
                                    dao.demande.update(demande);
                            }
                            request.getSession().setAttribute("successMsg",
                                    "Demande mise à jour et statut passé à " + nouveauStatut.name());
                        } else {
                            // Le statut n'a pas changé, simple mise à jour des autres champs
                            dao.demande.update(demande);
                            request.getSession().setAttribute("successMsg", "Informations de la demande mises à jour.");
                        }
                    } else {
                        dao.demande.update(demande);
                    }
                } else {
                    request.getSession().setAttribute("errorMsg", "Erreur : Demande introuvable.");
                }
            }

            tx.commit();
        } catch (IllegalStateException e) {
            // Capture des erreurs de logique métier du DAO (Ex: "Impossible de valider une
            // demande rejetée")
            if (tx.isActive())
                tx.rollback();
            request.getSession().setAttribute("errorMsg", e.getMessage());
        } catch (Exception e) {
            // Capture des autres erreurs (Base de données, NullPointer, etc.)
            if (tx.isActive())
                tx.rollback();
            e.printStackTrace();
            request.getSession().setAttribute("errorMsg", "Une erreur technique est survenue.");
        } finally {
            if (em.isOpen())
                em.close();
        }

        // 4. Redirection PRG (Post-Redirect-Get) vers la liste
        response.sendRedirect(request.getContextPath() + "/demande/liste");
    }
}