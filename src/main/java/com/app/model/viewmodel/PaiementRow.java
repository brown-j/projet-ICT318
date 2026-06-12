package com.app.model.viewmodel;

import com.app.jpa.model.Paiement;
import java.time.format.DateTimeFormatter;

public class PaiementRow {
    private final Long id;
    private final String referenceRecu;
    private final String typeDemandeLabel;
    private final String modePaiement;
    private final String modePaiementLabel;
    private final String montantFormate;
    private final String caissierNom;
    private final String datePaiementFormatee;

    public PaiementRow(Paiement paiement) {
        this.id = paiement.getId();
        this.referenceRecu = paiement.getReferenceRecu();

        // 1. Extraction du type de demande associée
        if (paiement.getDemande() != null && paiement.getDemande().getTypeDemande() != null) {
            this.typeDemandeLabel = paiement.getDemande().getTypeDemande().getLibelle();
        } else {
            this.typeDemandeLabel = "Recette Directe / Autre";
        }

        // 2. Récupération et formatage du mode de paiement pour l'UI et les filtres JS
        if (paiement.getModePaiement() != null) {
            this.modePaiement = paiement.getModePaiement().name(); // Ex: "ESPECES" pour le filtre JS
            this.modePaiementLabel = paiement.getModePaiement().getLibelle(); // Ex: "Espèces" pour l'affichage
        } else {
            this.modePaiement = "ESPECES";
            this.modePaiementLabel = "Espèces";
        }

        // 3. Formatage propre du montant (Ex: 142 500)
        if (paiement.getMontant() != null) {
            this.montantFormate = String.format("%,d", paiement.getMontant().longValue());
        } else {
            this.montantFormate = "0";
        }

        // 4. Extraction du nom de l'officier caissier
        if (paiement.getOfficierCaissier() != null) {
            String prenom = paiement.getOfficierCaissier().getPrenom();
            String premiereLettrePrenom = (prenom != null && !prenom.isEmpty()) ? prenom.substring(0, 1) + "." : "";
            this.caissierNom = paiement.getOfficierCaissier().getNom() + " " + premiereLettrePrenom;
        } else {
            this.caissierNom = "Système";
        }

        // 5. Formatage de la date et de l'heure de la transaction
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        this.datePaiementFormatee = (paiement.getDatePaiement() != null)
                ? paiement.getDatePaiement().format(formatter)
                : "--/--/---- --:--";
    }

    // --- Getters Immutables requis pour la sérialisation GSON ---
    public Long getId() {
        return id;
    }

    public String getReferenceRecu() {
        return referenceRecu;
    }

    public String getTypeDemandeLabel() {
        return typeDemandeLabel;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public String getModePaiementLabel() {
        return modePaiementLabel;
    }

    public String getMontantFormate() {
        return montantFormate;
    }

    public String getCaissierNom() {
        return caissierNom;
    }

    public String getDatePaiementFormatee() {
        return datePaiementFormatee;
    }
}