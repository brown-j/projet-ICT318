package com.app.ui;

import java.util.List;

import com.app.jpa.model.Citoyen;
import com.app.jpa.model.DemandeAdministrative;
import com.app.jpa.model.JPAEnum.PrioriteDemande;
import com.app.jpa.model.JPAEnum.StatutDemande;
import com.app.jpa.model.JPAEnum.TypeDemande;

public class DemandeFormFactory {

        /**
         * Génère dynamiquement le code HTML du formulaire d'une demande administrative.
         * * @param demande L'instance de la demande (vide pour création, chargée pour
         * édition/consultation).
         * 
         * @param types      La liste de tous les types de demandes disponibles (Enum).
         * @param priorites  La liste de toutes les priorités disponibles (Enum).
         * @param citoyens   La liste des citoyens de la base pour l'aide à la saisie.
         * @param actionUrl  L'URL de soumission du formulaire (POST).
         * @param isReadOnly Si vrai, désactive tous les champs pour le mode "preview".
         * @return Le bloc HTML sous forme de String.
         */
        public static String genererHtml(
                        DemandeAdministrative demande,
                        List<TypeDemande> types,
                        List<PrioriteDemande> priorites,
                        List<Citoyen> citoyens,
                        String actionUrl,
                        boolean isReadOnly) {

                StringBuilder html = new StringBuilder();

                // Attributs globaux du formulaire
                String disabledAttr = isReadOnly ? " disabled" : "";
                String titleSubmit = (demande.getId() == null) ? "Soumettre la demande"
                                : "Enregistrer les modifications";

                // Début du formulaire
                html.append("<form action=\"").append(actionUrl)
                                .append("\" method=\"POST\" style=\"padding:var(--space-4)\">");

                // Champ caché pour l'ID en cas de modification
                if (demande.getId() != null) {
                        html.append("<input type=\"hidden\" name=\"id\" value=\"").append(demande.getId())
                                        .append("\">");
                }

                html.append("<div style=\"display:flex; flex-direction:column; gap:var(--space-4)\">");

                // --- LIGNE 1 : Numéro de suivi (Lecture seule automatique si existant) & Type
                // de demande ---
                html.append("  <div style=\"display:flex; gap:var(--space-3); flex-wrap:wrap;\">");

                html.append("    <div style=\"flex:1; min-width:200px;\">");
                html.append("      <label class=\"form-label\" for=\"numeroSuivi\">Numéro de suivi</label>");
                String numSuivi = demande.getNumeroSuivi() != null ? demande.getNumeroSuivi() : "";
                String numDisabled = (demande.getId() != null || isReadOnly)
                                ? " readonly style=\"background:var(--bg-muted)\""
                                : "";
                html.append(
                                "      <input class=\"form-control\" id=\"numeroSuivi\" name=\"numeroSuivi\" type=\"text\" value=\"")
                                .append(numSuivi).append("\" placeholder=\"Ex: DM-2026-XXXX\" required")
                                .append(numDisabled)
                                .append(">");
                html.append("    </div>");

                html.append("    <div style=\"flex:1; min-width:200px;\">");
                html.append("      <label class=\"form-label\" for=\"typeDemande\">Type de demande</label>");
                html.append("      <select class=\"form-control\" id=\"typeDemande\" name=\"typeDemande\" required")
                                .append(disabledAttr).append(">");
                html.append("        <option value=\"\">-- Sélectionner un type --</option>");
                for (TypeDemande t : types) {
                        String selected = (demande.getTypeDemande() == t) ? " selected" : "";
                        html.append("        <option value=\"").append(t.name()).append("\"").append(selected)
                                        .append(">")
                                        .append(t.name().replace("_", " ")).append("</option>");
                }
                html.append("      </select>");
                html.append("    </div>");

                html.append("  </div>");

                // --- LIGNE 2 : Citoyen requérant & Priorité ---
                html.append("  <div style=\"display:flex; gap:var(--space-3); flex-wrap:wrap;\">");

                html.append("    <div style=\"flex:2; min-width:250px;\">");
                html.append("      <label class=\"form-label\" for=\"idCitoyen\">Citoyen requérant</label>");
                html.append("      <select class=\"form-control\" id=\"idCitoyen\" name=\"idCitoyen\" required")
                                .append(disabledAttr).append(">");
                html.append("        <option value=\"\">-- Sélectionner le citoyen --</option>");
                for (Citoyen c : citoyens) {
                        boolean isSelected = demande.getCitoyenRequerant() != null
                                        && demande.getCitoyenRequerant().getId().equals(c.getId());
                        String selected = isSelected ? " selected" : "";
                        html.append("        <option value=\"").append(c.getId()).append("\"").append(selected)
                                        .append(">")
                                        .append(c.getNom()).append(" ").append(c.getPrenom()).append(" (NIN: ")
                                        .append(c.getNin())
                                        .append(")</option>");
                }
                html.append("      </select>");
                html.append("    </div>");

                html.append("    <div style=\"flex:1; min-width:150px;\">");
                html.append("      <label class=\"form-label\" for=\"priorite\">Priorité</label>");
                html.append("      <select class=\"form-control\" id=\"priorite\" name=\"priorite\" required")
                                .append(disabledAttr).append(">");
                for (PrioriteDemande p : priorites) {
                        PrioriteDemande currentPriorite = demande.getPriorite() != null ? demande.getPriorite()
                                        : PrioriteDemande.NORMALE;
                        String selected = (currentPriorite == p) ? " selected" : "";
                        html.append("        <option value=\"").append(p.name()).append("\"").append(selected)
                                        .append(">")
                                        .append(p.name()).append("</option>");
                }
                html.append("      </select>");
                html.append("    </div>");

                html.append("  </div>");

                // --- LIGNE 3 : Statut (Affiché uniquement en modification) ---
                if (demande.getId() != null) {
                        html.append("  <div>");
                        html.append("    <label class=\"form-label\" for=\"statut\">Statut du traitement</label>");
                        html.append("    <select class=\"form-control\" id=\"statut\" name=\"statut\"")
                                        .append(disabledAttr)
                                        .append(">");
                        for (StatutDemande s : StatutDemande.values()) {
                                String selected = (demande.getStatut() == s) ? " selected" : "";
                                html.append("      <option value=\"").append(s.name()).append("\"").append(selected)
                                                .append(">")
                                                .append(s.name().replace("_", " ")).append("</option>");
                        }
                        html.append("    </select>");
                        html.append("  </div>");
                }

                // --- LIGNE 4 : Description de la demande ---
                html.append("  <div>");
                html.append("    <label class=\"form-label\" for=\"description\">Description / Justification</label>");
                String descValue = demande.getDescription() != null ? demande.getDescription() : "";
                html.append(
                                "    <textarea class=\"form-control\" id=\"description\" name=\"description\" rows=\"3\" placeholder=\"Détails de la demande...\" required style=\"resize:vertical\"")
                                .append(disabledAttr).append(">")
                                .append(descValue).append("</textarea>");
                html.append("  </div>");

                // --- LIGNE 5 : Section conditionnelle si Rejetée ou Clôturée (Mode Preview /
                // Edit) ---
                if (demande.getId() != null) {
                        if (demande.getStatut() == StatutDemande.REJETEE || !isReadOnly) {
                                html.append("  <div id=\"section-motif-rejet\">");
                                html.append(
                                                "    <label class=\"form-label\" for=\"motifRejet\">Motif de rejet (le cas échéant)</label>");
                                String motif = demande.getMotifRejet() != null ? demande.getMotifRejet() : "";
                                html.append(
                                                "    <textarea class=\"form-control\" id=\"motifRejet\" name=\"motifRejet\" rows=\"2\" style=\"resize:vertical\"")
                                                .append(disabledAttr).append(">")
                                                .append(motif).append("</textarea>");
                                html.append("  </div>");
                        }

                        html.append("  <div>");
                        html.append("    <label class=\"form-label\" for=\"commentaires\">Notes internes / Commentaires</label>");
                        String notes = demande.getCommentaires() != null ? demande.getCommentaires() : "";
                        html.append(
                                        "    <textarea class=\"form-control\" id=\"commentaires\" name=\"commentaires\" rows=\"2\" placeholder=\"Notes de l'officier...\" style=\"resize:vertical\"")
                                        .append(disabledAttr).append(">")
                                        .append(notes).append("</textarea>");
                        html.append("  </div>");
                }

                // --- ACTIONS FOOTER : Boutons masqués si le mode est isReadOnly ---
                if (!isReadOnly) {
                        html.append(
                                        "  <div style=\"display:flex; justify-content:flex-end; gap:var(--space-2); margin-top:var(--space-2);\">");
                        html.append(
                                        "    <button type=\"button\" class=\"btn btn-outline\" onclick=\"closeModal('demande')\">Annuler</button>");
                        html.append("    <button type=\"submit\" class=\"btn btn-primary\">").append(titleSubmit)
                                        .append("</button>");
                        html.append("  </div>");
                } else {
                        html.append("  <div style=\"display:flex; justify-content:flex-end; margin-top:var(--space-2);\">");
                        html.append(
                                        "    <button type=\"button\" class=\"btn btn-primary\" onclick=\"closeModal('demande')\">Fermer la vue</button>");
                        html.append("  </div>");
                }

                html.append("</div>"); // Fin du container flex
                html.append("</form>");

                return html.toString();
        }
}