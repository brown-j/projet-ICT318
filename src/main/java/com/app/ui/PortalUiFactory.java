package com.app.ui;

import com.app.model.icon.Icons;
import com.app.model.viewmodel.DossierSuiviViewModel;
import com.app.model.viewmodel.TimelineStep;

public class PortalUiFactory {

    /**
     * Génère le composant HTML complet de la fiche de suivi d'un dossier citoyen.
     */
    public static String genererFicheSuiviHtml(DossierSuiviViewModel dossier) {
        StringBuilder html = new StringBuilder();

        html.append("<div class=\"dossier-card\" role=\"article\" aria-label=\"Dossier ")
                .append(dossier.getNumeroSuivi()).append("\">\n");

        // 1. Header de la carte
        html.append("    <div class=\"dossier-card-header\">\n");
        html.append("        <div>\n");
        html.append("            <div class=\"dossier-num\">").append(dossier.getNumeroSuivi()).append("</div>\n");
        html.append("            <div class=\"dossier-type\">").append(dossier.getTypeLabel()).append(" · ")
                .append(dossier.getCitoyenNomComplet()).append("</div>\n");
        html.append("        </div>\n");

        // Utilisation de la classe de couleur issue de ThemeColor (ex: "success",
        // "warning")
        html.append("        <div class=\"status-badge ").append(dossier.getBadgeColorClass())
                .append("\" role=\"status\">\n");
        // L'icône générée par l'enum contient déjà "ti ti-...", le préfixe statique est
        // retiré
        html.append("            <i class=\"").append(dossier.getBadgeIconClass())
                .append("\" aria-hidden=\"true\"></i> ").append(dossier.getBadgeLabel()).append("\n");
        html.append("        </div>\n");
        html.append("    </div>\n");

        // 2. Corps de la carte et Timeline
        html.append("    <div class=\"dossier-body\">\n");
        html.append("        <div class=\"timeline\" role=\"list\" aria-label=\"Historique du dossier\">\n");

        for (TimelineStep step : dossier.getTimeline()) {
            // Utilisation de la classe sémantique du statut (ex: "success", "neutral")
            html.append("            <div class=\"timeline-item ").append(step.getStatusClass())
                    .append("\" role=\"listitem\">\n");
            html.append("                <div class=\"timeline-icon\">\n");
            // Injection de la classe d'icône complète résolue par le serveur
            html.append("                    <i class=\"").append(step.getIconClass())
                    .append("\" aria-hidden=\"true\"></i>\n");
            html.append("                </div>\n");
            html.append("                <div class=\"timeline-content\">\n");
            html.append("                    <div class=\"timeline-header\">\n");
            html.append("                        <h3 class=\"timeline-title\">").append(step.getTitle())
                    .append("</h3>\n");
            html.append("                        <span class=\"timeline-date\">").append(step.getDateFormatee())
                    .append("</span>\n");
            html.append("                    </div>\n");
            if (step.getDescription() != null && !step.getDescription().trim().isEmpty()) {
                html.append("                    <p class=\"timeline-desc\">").append(step.getDescription())
                        .append("</p>\n");
            }
            html.append("                </div>\n");
            html.append("            </div>\n");
        }

        html.append("        </div>\n"); // Fin timeline

        // 3. Bouton d'action pour le téléchargement du document finalisé (si
        // disponible)
        if (dossier.getDocumentFinalPath() != null && !dossier.getDocumentFinalPath().trim().isEmpty()) {
            html.append(
                    "        <button class=\"portal-btn-primary\" style=\"margin-top:1.5rem;width:100%\" onclick=\"simulateDownload('")
                    .append(dossier.getDocumentFinalPath()).append("')\">\n");
            // Utilisation sécurisée de Icons.DOWNLOAD pour uniformiser l'interface
            html.append("            <i class=\"").append(Icons.DOWNLOAD.toString())
                    .append("\" aria-hidden=\"true\"></i> Télécharger mon document officiel (PDF signé)\n");
            html.append("        </button>\n");
        }

        // 4. Message d'alerte en cas de rejet motivé
        if (dossier.getMessageRejet() != null && !dossier.getMessageRejet().trim().isEmpty()) {
            html.append(
                    "        <div class=\"rejet-box\" style=\"margin-top:1.25rem; padding:12px; background:var(--c-danger-50); border:1px solid var(--c-danger-100); border-radius:10px; color:var(--c-danger-800); font-size:13px;\">\n");

            // Icône de blocage/non-conformité basée sur l'enum Icons.X
            html.append("            <div style=\"font-weight:600;margin-bottom:4px;\"><i class=\"")
                    .append(Icons.X.toString())
                    .append("\"></i> Dossier non conforme</div>\n");
            html.append("            <p style=\"margin:0;line-height:1.4;\">").append(dossier.getMessageRejet())
                    .append("</p>\n");
            html.append("        </div>\n");

            // Lien pour relancer directement une demande corrigée
            html.append(
                    "        <a href=\"#\" onclick=\"resetForm();showPage('demande');return false;\" style=\"display:block; text-align:center; margin-top:1rem; font-size:12px; color:var(--color-primary); font-weight:600; text-decoration:none;\">\n");
            // Icône de création d'un nouveau fichier basée sur l'enum Icons.FILE_PLUS
            html.append("            <i class=\"").append(Icons.FILE_PLUS.toString())
                    .append("\"></i> Soumettre une nouvelle demande corrigée\n");
            html.append("        </a>\n");
        }

        html.append("    </div>\n"); // Fin body
        html.append("</div>\n"); // Fin card

        return html.toString();
    }
}