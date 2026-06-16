package com.app.model.viewmodel;

import com.app.model.icon.Icons;
import com.app.model.theme.ThemeColor;
import java.util.ArrayList;
import java.util.List;

public class DossierSuiviViewModel {
    private final String numeroSuivi;
    private final String typeLabel;
    private final String citoyenNomComplet;
    private final String dateSoumission;
    private final String estimationDelai;

    // Propriétés calculées dynamiquement via le Design System (Enums)
    private String badgeColorClass;
    private String badgeIconClass;
    private String badgeLabel;

    private final String documentFinalPath; // Chemin du PDF signé si disponible
    private final String messageRejet; // Motif de rejet si applicable
    private final List<TimelineStep> timeline = new ArrayList<>();

    public DossierSuiviViewModel(String numeroSuivi, String typeLabel, String citoyenNomComplet,
            String dateSoumission, String estimationDelai, String statutBrut,
            String documentFinalPath, String messageRejet) {
        this.numeroSuivi = numeroSuivi;
        this.typeLabel = typeLabel;
        this.citoyenNomComplet = citoyenNomComplet;
        this.dateSoumission = dateSoumission;
        this.estimationDelai = estimationDelai;
        this.documentFinalPath = documentFinalPath;
        this.messageRejet = messageRejet;

        configurerStatut(statutBrut);
    }

    private void configurerStatut(String statut) {
        // Alignement sur les conventions de DemandeRow et DemandeData
        if ("valide".equalsIgnoreCase(statut) || "validee".equalsIgnoreCase(statut)) {
            this.badgeColorClass = ThemeColor.SUCCESS.getCssClass(); // "success"[cite: 5]
            this.badgeIconClass = Icons.USER_CHECK.toString(); // "ti ti-user-check"[cite: 6]
            this.badgeLabel = "Validé ✓";
        } else if ("rejete".equalsIgnoreCase(statut) || "rejetee".equalsIgnoreCase(statut)) {
            this.badgeColorClass = ThemeColor.ERROR.getCssClass(); // "error"[cite: 5]
            this.badgeIconClass = Icons.X.toString(); // "ti ti-x"[cite: 6]
            this.badgeLabel = "Rejeté";
        } else {
            this.badgeColorClass = ThemeColor.WARNING.getCssClass(); // "warning"[cite: 5]
            this.badgeIconClass = Icons.EYE.toString(); // "ti ti-eye"[cite: 6]
            this.badgeLabel = "En cours";
        }
    }

    public void addStep(TimelineStep step) {
        this.timeline.add(step);
    }

    // --- Getters ---
    public String getNumeroSuivi() {
        return numeroSuivi;
    }

    public String getTypeLabel() {
        return typeLabel;
    }

    public String getCitoyenNomComplet() {
        return citoyenNomComplet;
    }

    public String getDateSoumission() {
        return dateSoumission;
    }

    public String getEstimationDelai() {
        return estimationDelai;
    }

    public String getBadgeColorClass() {
        return badgeColorClass;
    }

    public String getBadgeIconClass() {
        return badgeIconClass;
    }

    public String getBadgeLabel() {
        return badgeLabel;
    }

    public String getDocumentFinalPath() {
        return documentFinalPath;
    }

    public String getMessageRejet() {
        return messageRejet;
    }

    public List<TimelineStep> getTimeline() {
        return timeline;
    }
}