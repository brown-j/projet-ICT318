package com.app.model.viewmodel;

import com.app.model.jpa.Citoyen;
import com.app.model.theme.ThemeColor;
import java.time.format.DateTimeFormatter;

public class CitoyenRow {
    private final Long id;
    private final String nin;
    private final String nomComplet;
    private final String dateNaissanceFormatee;
    private final String quartier;
    private final String situationLabel;
    private final String situationColorClass;
    private final String statutLabel;
    private final String statutColorClass;
    private final boolean afficherPointStatut;

    public CitoyenRow(Citoyen citoyen) {
        this.id = citoyen.getId();
        this.nin = citoyen.getNin();

        // 1. Fusion propre du Nom complet
        this.nomComplet = citoyen.getPrenom() + " " + citoyen.getNom();

        // 2. Formatage de la date de naissance (ex: 14/03/1990)
        if (citoyen.getDateNaissance() != null) {
            this.dateNaissanceFormatee = citoyen.getDateNaissance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } else {
            this.dateNaissanceFormatee = "--/--/----";
        }

        // 3. Extraction du quartier
        this.quartier = (citoyen.getAdresse() != null) ? citoyen.getAdresse() : "Non spécifié";

        // 4. Mapping de la situation matrimoniale avec l'enum ThemeColor
        if (citoyen.getSituationMatrimoniale() != null) {
            switch (citoyen.getSituationMatrimoniale()) {
                case MARIE:
                    this.situationLabel = "Marié(e)";
                    this.situationColorClass = ThemeColor.PRIMARY.getCssClass();
                    break;
                case CELIBATAIRE:
                    this.situationLabel = "Célibataire";
                    this.situationColorClass = ThemeColor.NEUTRAL.getCssClass();
                    break;
                case DIVORCE:
                    this.situationLabel = "Divorcé(e)";
                    this.situationColorClass = ThemeColor.WARNING.getCssClass();
                    break;
                case VEUF:
                    this.situationLabel = "Veuf(ve)";
                    this.situationColorClass = ThemeColor.ACCENT.getCssClass();
                    break;
                default:
                    this.situationLabel = citoyen.getSituationMatrimoniale().name();
                    this.situationColorClass = ThemeColor.NEUTRAL.getCssClass();
            }
        } else {
            this.situationLabel = "Inconnue";
            this.situationColorClass = ThemeColor.NEUTRAL.getCssClass();
        }

        // 5. Mapping du statut du citoyen avec l'enum ThemeColor
        if (citoyen.getStatut() != null) {
            switch (citoyen.getStatut()) {
                case ACTIF:
                    this.statutLabel = "Actif";
                    this.statutColorClass = ThemeColor.SUCCESS.getCssClass();
                    this.afficherPointStatut = true;
                    break;
                case ARCHIVE:
                    this.statutLabel = "Archivé";
                    this.statutColorClass = ThemeColor.WARNING.getCssClass();
                    this.afficherPointStatut = false;
                    break;
                case DECEDE:
                    this.statutLabel = "Décédé";
                    this.statutColorClass = ThemeColor.ERROR.getCssClass();
                    this.afficherPointStatut = false;
                    break;
                default:
                    this.statutLabel = citoyen.getStatut().name();
                    this.statutColorClass = ThemeColor.NEUTRAL.getCssClass();
                    this.afficherPointStatut = false;
            }
        } else {
            this.statutLabel = "Inconnu";
            this.statutColorClass = ThemeColor.NEUTRAL.getCssClass();
            this.afficherPointStatut = false;
        }
    }

    // --- Getters Immutables pour la JSP ---
    public Long getId() {
        return id;
    }

    public String getNin() {
        return nin;
    }

    public String getNomComplet() {
        return nomComplet;
    }

    public String getDateNaissanceFormatee() {
        return dateNaissanceFormatee;
    }

    public String getQuartier() {
        return quartier;
    }

    public String getSituationLabel() {
        return situationLabel;
    }

    public String getSituationColorClass() {
        return situationColorClass;
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
}