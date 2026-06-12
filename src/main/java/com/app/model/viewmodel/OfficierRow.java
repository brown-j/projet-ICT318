package com.app.model.viewmodel;

import com.app.jpa.model.OfficierEtatCivil;
import com.app.model.theme.ThemeColor;
import java.time.format.DateTimeFormatter;

public class OfficierRow {
    private final Long id;
    private final String matricule;
    private final String nomComplet;
    private final String email;
    private final String service;
    private final String datePriseFonctionFormatee;

    // Rôle (badge distinctif)
    private final String roleLabel;
    private final String roleColorClass;

    // Statut (badge de modération & point d'activité)
    private final String statutLabel;
    private final String statutColorClass;
    private final boolean afficherPointStatut;

    // Signature numérique disponible
    private final boolean hasSignature;

    public OfficierRow(OfficierEtatCivil officier) {
        this.id = officier.getId();
        this.matricule = (officier.getMatricule() != null) ? officier.getMatricule() : "Non assigné";
        this.email = officier.getEmail();
        this.service = (officier.getService() != null) ? officier.getService() : "Non affecté";

        // 1. Fusion propre du Nom et Prénom de l'agent
        this.nomComplet = officier.getPrenom() + " " + officier.getNom();

        // 2. Formatage de la date de prise de fonction
        if (officier.getDatePriseFonction() != null) {
            this.datePriseFonctionFormatee = officier.getDatePriseFonction()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } else {
            this.datePriseFonctionFormatee = "--/--/----";
        }

        // 3. Mapping du Rôle (Utilisation directe du libellé de l'enum Role)
        if (officier.getRole() != null) {
            this.roleLabel = officier.getRole().getNom(); // Récupère le nom propre "Super Administrateur", etc.

            switch (officier.getRole()) {
                case SUPER_ADMIN:
                    this.roleColorClass = ThemeColor.ERROR.getCssClass(); // Rouge écarlate de sécurité
                    break;
                case ADMIN:
                    this.roleColorClass = ThemeColor.ACCENT.getCssClass(); // Violet administratif
                    break;
                case OFFICIER:
                    this.roleColorClass = ThemeColor.PRIMARY.getCssClass(); // Bleu communal principal
                    break;
                case AGENT_SAISIE:
                    this.roleColorClass = ThemeColor.NEUTRAL.getCssClass(); // Gris standard
                    break;
                default:
                    this.roleColorClass = ThemeColor.NEUTRAL.getCssClass();
            }
        } else {
            this.roleLabel = "Aucun rôle";
            this.roleColorClass = ThemeColor.NEUTRAL.getCssClass();
        }

        // 4. Mapping du Statut (Utilisation du libellé de l'enum StatutOfficier)
        if (officier.getStatut() != null) {
            this.statutLabel = officier.getStatut().getLibelle();

            switch (officier.getStatut()) {
                case ACTIF:
                    this.statutColorClass = ThemeColor.SUCCESS.getCssClass(); // Vert
                    this.afficherPointStatut = true; // Point d'activité clignotant (accès autorisé)
                    break;
                case SUSPENDU:
                    this.statutColorClass = ThemeColor.WARNING.getCssClass(); // Orange / Ambre
                    this.afficherPointStatut = false;
                    break;
                case INACTIF:
                    this.statutColorClass = ThemeColor.NEUTRAL.getCssClass(); // Gris
                    this.afficherPointStatut = false;
                    break;
                default:
                    this.statutColorClass = ThemeColor.NEUTRAL.getCssClass();
                    this.afficherPointStatut = false;
            }
        } else {
            this.statutLabel = "Inconnu";
            this.statutColorClass = ThemeColor.NEUTRAL.getCssClass();
            this.afficherPointStatut = false;
        }

        // 5. Indicateur visuel pour savoir si l'image de la signature a été téléversée
        this.hasSignature = (officier.getSignatureNumerique() != null
                && !officier.getSignatureNumerique().trim().isEmpty());
    }

    // --- Getters Immutables pour l'export JSON / Gson ---
    public Long getId() {
        return id;
    }

    public String getMatricule() {
        return matricule;
    }

    public String getNomComplet() {
        return nomComplet;
    }

    public String getEmail() {
        return email;
    }

    public String getService() {
        return service;
    }

    public String getDatePriseFonctionFormatee() {
        return datePriseFonctionFormatee;
    }

    public String getRoleLabel() {
        return roleLabel;
    }

    public String getRoleColorClass() {
        return roleColorClass;
    }

    public String getStatutLabel() {
        return statutLabel;
    }

    public String getStatutColorClass() {
        return statutColorClass;
    }

    public boolean isAfficherPointStatut() {
        return afficherPointStatut;
    }

    public boolean isHasSignature() {
        return hasSignature;
    }
}
