package com.app.model.viewmodel;

import com.app.model.jpa.JournalAudit;
import com.app.model.theme.ThemeColor;
import com.app.model.icon.Icons;

import java.time.format.DateTimeFormatter;

public class ActiviteData {

    private final String iconClass;
    private final String colorClass;
    private final String titre;
    private final String description;
    private final String heureFormatee;

    public ActiviteData(JournalAudit audit) {

        // 1. Formatage de l'heure
        if (audit.getDateAction() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            this.heureFormatee = audit.getDateAction().format(formatter);
        } else {
            this.heureFormatee = "--:--";
        }

        // 2. Initialisation des variables de fallback (par défaut)
        Icons selectedIcon = Icons.POINT_FILLED;
        ThemeColor selectedColor = ThemeColor.NEUTRAL;
        String generatedTitle = audit.getAction().toLowerCase();
        String generatedDescription = "Table : " + audit.getTableAffectee();

        // 3. Logique de traduction métier basée sur TES entités (Le Cerveau)
        if (audit.getTableAffectee() != null) {
            switch (audit.getTableAffectee().toLowerCase()) {

                case "acte_etat_civil":
                    selectedIcon = Icons.FILE_CERTIFICATE;
                    selectedColor = ThemeColor.PRIMARY;
                    generatedTitle = "Acte " + audit.getAction().toLowerCase();
                    generatedDescription = "Action enregistrée par " +
                            (audit.getOfficier() != null ? audit.getOfficier().getNom() : "Système");
                    break;

                case "citoyen":
                    selectedIcon = Icons.USERS;
                    selectedColor = ThemeColor.SUCCESS;
                    generatedTitle = "Citoyen " + audit.getAction().toLowerCase();
                    generatedDescription = "Mise à jour du registre population";
                    break;

                case "demande_administrative":
                    selectedIcon = Icons.CLIPBOARD_LIST;
                    selectedColor = ThemeColor.INFO;
                    generatedTitle = "Demande " + audit.getAction().toLowerCase();
                    generatedDescription = "Suivi d'une démarche administrative";
                    break;

                case "officier_etat_civil":
                    selectedIcon = Icons.USER_CHECK;
                    selectedColor = ThemeColor.SECONDARY;
                    generatedTitle = "Officier " + audit.getAction().toLowerCase();
                    generatedDescription = "Gestion du personnel d'état civil";
                    break;

                case "paiement":
                    selectedIcon = Icons.CASH; // ou CURRENCY_FRANC
                    selectedColor = ThemeColor.ACCENT;
                    generatedTitle = "Paiement " + audit.getAction().toLowerCase();
                    generatedDescription = "Opération comptable";
                    break;

                case "piece_jointe":
                    selectedIcon = Icons.FILE_PLUS;
                    selectedColor = ThemeColor.NEUTRAL;
                    generatedTitle = "Document " + audit.getAction().toLowerCase();
                    generatedDescription = "Gestion des pièces jointes";
                    break;

                case "rendez_vous":
                    selectedIcon = Icons.CALENDAR_PLUS;
                    selectedColor = ThemeColor.WARNING;
                    generatedTitle = "Rendez-vous " + audit.getAction().toLowerCase();
                    generatedDescription = "Planification au calendrier";
                    break;

                // Regroupement des tables de configuration/paramétrage
                case "role":
                case "type_acte":
                case "type_demande":
                    selectedIcon = Icons.CLIPBOARD_LIST;
                    selectedColor = ThemeColor.NEUTRAL;
                    generatedTitle = "Configuration " + audit.getAction().toLowerCase();
                    generatedDescription = "Mise à jour des paramètres système";
                    break;
            }
        }

        // 4. Assignation finale pour la vue (Strings purs)
        this.iconClass = selectedIcon.toString(); // Appelle automatiquement "ti ti-..."
        this.colorClass = selectedColor.name().toLowerCase();
        this.titre = generatedTitle;

        // Optionnel : si ton JSON contient des infos plus précises, tu pourras les
        // parser ici à l'avenir.
        this.description = generatedDescription;
    }

    // --- Getters (Immutables) ---
    public String getIconClass() {
        return iconClass;
    }

    public String getColorClass() {
        return colorClass;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    public String getHeureFormatee() {
        return heureFormatee;
    }
}