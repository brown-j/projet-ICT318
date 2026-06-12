package com.app.model.viewmodel;

import com.app.jpa.model.ActeEtatCivil;
import com.app.model.theme.ThemeColor;
import java.time.format.DateTimeFormatter;

public class ActeCivilRow {
    private final Long id;
    private final String numeroActe;
    private final String typeActeLabel;
    private final String citoyenPrincipalNom;
    private final String dateEvenementFormatee;
    private final String dateEtablissementFormatee;
    private final String statutLabel;
    private final String statutColorClass;
    private final boolean afficherPointStatut;
    private final boolean hasPdf;

    public ActeCivilRow(ActeEtatCivil acte) {
        this.id = acte.getId();
        this.numeroActe = acte.getNumeroActe();

        // 1. Extraction du type d'acte (On suppose que TypeActe a un getter
        // getLibelle() ou getNom())
        this.typeActeLabel = (acte.getTypeActe() != null) ? acte.getTypeActe().getLibelle() : "Inconnu";

        // 2. Récupération du nom complet du citoyen concerné
        if (acte.getCitoyenPrincipal() != null) {
            this.citoyenPrincipalNom = acte.getCitoyenPrincipal().getPrenom() + " "
                    + acte.getCitoyenPrincipal().getNom();
        } else {
            this.citoyenPrincipalNom = "Non défini";
        }

        // 3. Formatage des dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.dateEvenementFormatee = (acte.getDateEvenement() != null)
                ? acte.getDateEvenement().format(formatter)
                : "--/--/----";

        this.dateEtablissementFormatee = (acte.getDateEtablissement() != null)
                ? acte.getDateEtablissement().format(formatter)
                : "--/--/----";

        // 4. Mapping du statut de l'acte avec l'enum ThemeColor
        if (acte.getStatut() != null) {
            switch (acte.getStatut()) {
                case EN_COURS:
                    this.statutLabel = "En cours";
                    this.statutColorClass = ThemeColor.WARNING.getCssClass();
                    this.afficherPointStatut = true; // Attire l'attention sur les actes en attente
                    break;
                case DELIVRE:
                    this.statutLabel = "Délivré";
                    this.statutColorClass = ThemeColor.SUCCESS.getCssClass();
                    this.afficherPointStatut = false;
                    break;
                case ANNULE:
                    this.statutLabel = "Annulé";
                    this.statutColorClass = ThemeColor.ERROR.getCssClass();
                    this.afficherPointStatut = false;
                    break;
                case ARCHIVE:
                    this.statutLabel = "Archivé";
                    this.statutColorClass = ThemeColor.NEUTRAL.getCssClass();
                    this.afficherPointStatut = false;
                    break;
                default:
                    this.statutLabel = acte.getStatut().name();
                    this.statutColorClass = ThemeColor.NEUTRAL.getCssClass();
                    this.afficherPointStatut = false;
            }
        } else {
            this.statutLabel = "Inconnu";
            this.statutColorClass = ThemeColor.NEUTRAL.getCssClass();
            this.afficherPointStatut = false;
        }

        // 5. Booléen pratique pour la JSP : Savoir si on doit afficher le bouton
        // "Télécharger PDF"
        this.hasPdf = (acte.getFichierPdf() != null && !acte.getFichierPdf().trim().isEmpty());
    }

    // --- Getters Immutables pour la sérialisation Gson ---
    public Long getId() {
        return id;
    }

    public String getNumeroActe() {
        return numeroActe;
    }

    public String getTypeActeLabel() {
        return typeActeLabel;
    }

    public String getCitoyenPrincipalNom() {
        return citoyenPrincipalNom;
    }

    public String getDateEvenementFormatee() {
        return dateEvenementFormatee;
    }

    public String getDateEtablissementFormatee() {
        return dateEtablissementFormatee;
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

    public boolean isHasPdf() {
        return hasPdf;
    }
}