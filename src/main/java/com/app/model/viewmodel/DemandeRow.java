package com.app.model.viewmodel;

import com.app.model.jpa.DemandeAdministrative;
import com.app.model.theme.ThemeColor;
import java.time.format.DateTimeFormatter;

public class DemandeRow {
    private final Long id;
    private final String numeroSuivi;
    private final String typeDemandeLabel;
    private final String requérantNomComplet;
    private final String dateSoumissionFormatee;

    // Gestion du Statut (badge & point clignotant)
    private final String statutLabel;
    private final String statutColorClass;
    private final boolean afficherPointStatut;

    // Gestion de la Priorité (badge distinctif)
    private final String prioriteLabel;
    private final String prioriteColorClass;

    private final boolean hasDocumentFinal;

    public DemandeRow(DemandeAdministrative demande) {
        this.id = demande.getId();
        this.numeroSuivi = demande.getNumeroSuivi();

        // 1. Extraction des relations
        this.typeDemandeLabel = (demande.getTypeDemande() != null) ? demande.getTypeDemande().getLibelle()
                : "Non spécifié";

        if (demande.getCitoyenRequerant() != null) {
            this.requérantNomComplet = demande.getCitoyenRequerant().getPrenom() + " "
                    + demande.getCitoyenRequerant().getNom();
        } else {
            this.requérantNomComplet = "Anonyme / Inconnu";
        }

        // 2. Formatage de la date de soumission (Date + Heure pour le suivi précis)
        if (demande.getDateSoumission() != null) {
            this.dateSoumissionFormatee = demande.getDateSoumission()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } else {
            this.dateSoumissionFormatee = "--/--/---- --:--";
        }

        // 3. Mapping du Statut (Enum -> ThemeColor)
        if (demande.getStatut() != null) {
            switch (demande.getStatut()) {
                case SOUMISE:
                    this.statutLabel = "Nouvelle";
                    this.statutColorClass = ThemeColor.PRIMARY.getCssClass(); // Bleu communal
                    this.afficherPointStatut = true; // Alerte visuelle pour nouvelle demande
                    break;
                case EN_COURS:
                    this.statutLabel = "En cours";
                    this.statutColorClass = ThemeColor.WARNING.getCssClass(); // Orange
                    this.afficherPointStatut = false;
                    break;
                case VALIDEE:
                    this.statutLabel = "Validée";
                    this.statutColorClass = ThemeColor.SUCCESS.getCssClass(); // Vert
                    this.afficherPointStatut = false;
                    break;
                case REJETEE:
                    this.statutLabel = "Rejetée";
                    this.statutColorClass = ThemeColor.ERROR.getCssClass(); // Rouge
                    this.afficherPointStatut = false;
                    break;
                case CLOTUREE:
                    this.statutLabel = "Clôturée";
                    this.statutColorClass = ThemeColor.NEUTRAL.getCssClass(); // Gris
                    this.afficherPointStatut = false;
                    break;
                default:
                    this.statutLabel = demande.getStatut().name();
                    this.statutColorClass = ThemeColor.NEUTRAL.getCssClass();
                    this.afficherPointStatut = false;
            }
        } else {
            this.statutLabel = "Inconnu";
            this.statutColorClass = ThemeColor.NEUTRAL.getCssClass();
            this.afficherPointStatut = false;
        }

        // 4. Mapping de la Priorité (Enum -> ThemeColor)
        if (demande.getPriorite() != null) {
            switch (demande.getPriorite()) {
                case BASSE:
                    this.prioriteLabel = "Basse";
                    this.prioriteColorClass = ThemeColor.NEUTRAL.getCssClass();
                    break;
                case NORMALE:
                    this.prioriteLabel = "Normale";
                    this.prioriteColorClass = ThemeColor.PRIMARY.getCssClass();
                    break;
                case HAUTE:
                    this.prioriteLabel = "Haute";
                    this.prioriteColorClass = ThemeColor.WARNING.getCssClass();
                    break;
                case URGENTE:
                    this.prioriteLabel = "Urgente";
                    this.prioriteColorClass = ThemeColor.ERROR.getCssClass(); // Flash rouge
                    break;
                default:
                    this.prioriteLabel = demande.getPriorite().name();
                    this.prioriteColorClass = ThemeColor.NEUTRAL.getCssClass();
            }
        } else {
            this.prioriteLabel = "Normale";
            this.prioriteColorClass = ThemeColor.PRIMARY.getCssClass();
        }

        // 5. Indicateur de fichier finalisé disponible
        this.hasDocumentFinal = (demande.getDocumentFinal() != null && !demande.getDocumentFinal().trim().isEmpty());
    }

    // --- Getters Immutables pour Gson ---
    public Long getId() {
        return id;
    }

    public String getNumeroSuivi() {
        return numeroSuivi;
    }

    public String getTypeDemandeLabel() {
        return typeDemandeLabel;
    }

    public String getRequérantNomComplet() {
        return requérantNomComplet;
    }

    public String getDateSoumissionFormatee() {
        return dateSoumissionFormatee;
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

    public String getPrioriteLabel() {
        return prioriteLabel;
    }

    public String getPrioriteColorClass() {
        return prioriteColorClass;
    }

    public boolean isHasDocumentFinal() {
        return hasDocumentFinal;
    }
}