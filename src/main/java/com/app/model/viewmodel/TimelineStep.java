package com.app.model.viewmodel;

import com.app.model.icon.Icons;
import com.app.model.theme.ThemeColor;

public class TimelineStep {
    private final String iconClass; // Reçoit le résultat de Icons.toString() (ex: "ti ti-eye")
    private final String statusClass; // Reçoit la classe CSS du thème (ex: "success", "neutral")
    private final String title; // Ex: "Dossier en cours d'examen"
    private final String dateFormatee; // Ex: "03/06/2026 · 08h45" ou "En attente"
    private final String description; // Description textuelle ou null

    public TimelineStep(Icons icon, ThemeColor theme, String title, String dateFormatee, String description) {
        // Résolution dynamique et sécurisée via tes Enums
        this.iconClass = (icon != null) ? icon.toString() : "";
        this.statusClass = (theme != null) ? theme.getCssClass() : ThemeColor.NEUTRAL.getCssClass();
        this.title = title;
        this.dateFormatee = dateFormatee;
        this.description = description;
    }

    // --- Getters adaptés pour l'injection JSP / JSON ---
    public String getIconClass() {
        return iconClass;
    }

    public String getStatusClass() {
        return statusClass;
    }

    public String getTitle() {
        return title;
    }

    public String getDateFormatee() {
        return dateFormatee;
    }

    public String getDescription() {
        return description;
    }
}