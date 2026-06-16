package com.app.model.viewmodel;

import com.app.model.theme.ThemeColor;
import com.app.jpa.model.JournalAudit;
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

        String actionRaw = audit.getAction() != null ? audit.getAction().toUpperCase() : "ACTION";
        String actionTraduit = traduireAction(actionRaw);

        String generatedTitle = actionTraduit;
        String nomOfficier = (audit.getOfficier() != null) ? audit.getOfficier().getNom() : "Système";
        String generatedDescription = "Par " + nomOfficier + " (Table: " + audit.getTableAffectee() + ")";

        // 3. Logique de traduction métier corrigée (Prend en compte le nom des classes
        // Java du JPADao)
        if (audit.getTableAffectee() != null) {
            switch (audit.getTableAffectee().toLowerCase()) {

                case "acte_etat_civil":
                case "acteetatcivil": // ✨ Correctif : Match "ActeEtatCivil".toLowerCase()
                    selectedIcon = Icons.FILE_CERTIFICATE;
                    selectedColor = ThemeColor.PRIMARY;
                    generatedTitle = "Acte civil " + actionTraduit.toLowerCase();
                    generatedDescription = "Enregistré dans le registre d'état civil par " + nomOfficier;
                    break;

                case "citoyen":
                    selectedIcon = Icons.USERS;
                    selectedColor = ThemeColor.SUCCESS;
                    generatedTitle = "Citoyen " + actionTraduit.toLowerCase();
                    generatedDescription = "Fiche citoyen altérée par " + nomOfficier;
                    break;

                case "demande_administrative":
                case "demandeadministrative": // ✨ Correctif : Match "DemandeAdministrative".toLowerCase()
                    selectedIcon = Icons.CLIPBOARD_LIST;
                    selectedColor = ThemeColor.INFO;
                    generatedTitle = "Demande " + actionTraduit.toLowerCase();
                    generatedDescription = "Suivi de dossier mis à jour par " + nomOfficier;
                    break;

                case "officier_etat_civil":
                case "officieretatcivil": // ✨ Correctif : Match "OfficierEtatCivil".toLowerCase()
                    selectedIcon = Icons.USER_CHECK;
                    selectedColor = ThemeColor.SECONDARY;
                    generatedTitle = "Compte Officier " + actionTraduit.toLowerCase();
                    generatedDescription = "Droits ou profils modifiés par " + nomOfficier;
                    break;

                case "paiement":
                    selectedIcon = Icons.CASH;
                    selectedColor = ThemeColor.ACCENT;
                    generatedTitle = "Encaissement " + actionTraduit.toLowerCase();
                    generatedDescription = "Émission d'un reçu de paiement par " + nomOfficier;
                    break;

                case "piece_jointe":
                case "piecejointe":
                    selectedIcon = Icons.FILE_PLUS;
                    selectedColor = ThemeColor.NEUTRAL;
                    generatedTitle = "Pièce jointe " + actionTraduit.toLowerCase();
                    generatedDescription = "Fichier lié à une demande par " + nomOfficier;
                    break;

                case "rendez_vous":
                case "rendezvous":
                    selectedIcon = Icons.CALENDAR_PLUS;
                    selectedColor = ThemeColor.WARNING;
                    generatedTitle = "Rendez-vous " + actionTraduit.toLowerCase();
                    generatedDescription = "Planification gérée par " + nomOfficier;
                    break;

                case "role":
                case "type_acte":
                case "type_demande":
                case "typeacte":
                case "typedemande":
                    selectedIcon = Icons.CLIPBOARD_LIST;
                    selectedColor = ThemeColor.NEUTRAL;
                    generatedTitle = "Configuration " + actionTraduit.toLowerCase();
                    generatedDescription = "Mise à jour des paramètres système";
                    break;
            }
        }

        // 4. Assignation finale pour la vue
        this.iconClass = selectedIcon.toString();
        this.colorClass = selectedColor.name().toLowerCase();
        this.titre = generatedTitle;
        this.description = generatedDescription;
    }

    /**
     * Petit helper de traduction des actions CRUD pour l'affichage utilisateur
     */
    private String traduireAction(String actionRaw) {
        switch (actionRaw) {
            case "CREATE":
                return "Créé";
            case "UPDATE":
                return "Modifié";
            case "DELETE":
                return "Supprimé";
            default:
                return actionRaw;
        }
    }

    // --- Getters ---
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