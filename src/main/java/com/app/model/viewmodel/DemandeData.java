package com.app.model.viewmodel;

import com.app.jpa.model.DemandeAdministrative;
import com.app.model.theme.ThemeColor;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class DemandeData {

    private final String numeroSuivi;
    private final String titreDemande;
    private final String infosDemandeur;
    private final String tempsEcoule; // Nouvelle propriété configurée
    private final String colorClass;
    private final String badgeLabel;

    public DemandeData(DemandeAdministrative demandeBrute) {
        // 1. Extractions de base
        this.numeroSuivi = demandeBrute.getNumeroSuivi();

        // 💡 CORRECTION : Utilisation de getTypeActe() au lieu de getTypeDemande()
        this.titreDemande = (demandeBrute.getTypeActe() != null)
                ? demandeBrute.getTypeActe().getLibelle()
                : "Demande non spécifiée";

        String nomDemandeur = "";
        if (demandeBrute.getCitoyenRequerant() != null) {
            nomDemandeur = demandeBrute.getCitoyenRequerant().getPrenom() + " " +
                    demandeBrute.getCitoyenRequerant().getNom();
        }
        this.infosDemandeur = nomDemandeur;

        // 2. Calcul dynamique du temps écoulé depuis la soumission
        this.tempsEcoule = calculerTempsEcoule(demandeBrute.getDateSoumission());

        // 3. Mapping des Enums vers ThemeColor
        if (demandeBrute.getStatut() != null) {
            switch (demandeBrute.getStatut()) {
                case SOUMISE:
                    this.colorClass = ThemeColor.INFO.getCssClass();
                    this.badgeLabel = "Soumise";
                    break;
                case EN_COURS:
                    this.colorClass = ThemeColor.WARNING.getCssClass();
                    this.badgeLabel = "En cours";
                    break;
                case VALIDEE:
                    this.colorClass = ThemeColor.SUCCESS.getCssClass();
                    this.badgeLabel = "Validée";
                    break;
                case REJETEE:
                    this.colorClass = ThemeColor.ERROR.getCssClass();
                    this.badgeLabel = "Rejetée";
                    break;
                case CLOTUREE:
                    this.colorClass = ThemeColor.NEUTRAL.getCssClass();
                    this.badgeLabel = "Clôturée";
                    break;
                default:
                    this.colorClass = ThemeColor.NEUTRAL.getCssClass();
                    this.badgeLabel = demandeBrute.getStatut().name();
                    break;
            }
        } else {
            this.colorClass = ThemeColor.NEUTRAL.getCssClass();
            this.badgeLabel = "Inconnu";
        }
    }

    /**
     * Calcule l'écart temporel et retourne une chaîne human-readable
     */
    private String calculerTempsEcoule(LocalDateTime dateSoumission) {
        if (dateSoumission == null) {
            return "date inconnue";
        }

        LocalDateTime now = LocalDateTime.now();

        // Sécurité si l'horloge système ou la DB a un léger décalage dans le futur
        if (dateSoumission.isAfter(now)) {
            return "à l'instant";
        }

        long minutes = Duration.between(dateSoumission, now).toMinutes();
        if (minutes < 1) {
            return "à l'instant";
        }
        if (minutes < 60) {
            return "il y a " + minutes + " min";
        }

        long hours = Duration.between(dateSoumission, now).toHours();
        if (hours < 24 && dateSoumission.getDayOfMonth() == now.getDayOfMonth()) {
            return "il y a " + hours + "h";
        }

        // Calcul basé sur les jours calendaires pour gérer "hier" proprement
        long jours = ChronoUnit.DAYS.between(dateSoumission.toLocalDate(), now.toLocalDate());
        if (jours == 1) {
            return "hier";
        }

        return "il y a " + jours + "j";
    }

    // --- Getters ---
    public String getNumeroSuivi() {
        return numeroSuivi;
    }

    public String getTitreDemande() {
        return titreDemande;
    }

    public String getInfosDemandeur() {
        return infosDemandeur;
    }

    public String getTempsEcoule() {
        return tempsEcoule;
    }

    public String getColorClass() {
        return colorClass;
    }

    public String getBadgeLabel() {
        return badgeLabel;
    }
}