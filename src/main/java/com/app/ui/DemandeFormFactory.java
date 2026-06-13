package com.app.ui;

import com.app.jpa.model.Citoyen;
import com.app.jpa.model.DemandeAdministrative;
import com.app.jpa.model.JPAEnum.PrioriteDemande;
import com.app.jpa.model.JPAEnum.StatutDemande;
import com.app.jpa.model.JPAEnum.TypeDemande;
import com.app.ui.FormBuilder.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class DemandeFormFactory {

    public static String genererHtml(DemandeAdministrative demande, List<TypeDemande> types,
            List<PrioriteDemande> priorites, List<Citoyen> citoyens,
            String actionUrl, boolean readOnly) {

        // 1. Préparation des variables et protections Null-Safe
        String idValeur = (demande.getId() != null) ? String.valueOf(demande.getId()) : "";
        String numeroSuiviValeur = (demande.getNumeroSuivi() != null) ? demande.getNumeroSuivi() : "";
        String descriptionValeur = (demande.getDescription() != null) ? demande.getDescription() : "";
        String motifRejetValeur = (demande.getMotifRejet() != null) ? demande.getMotifRejet() : "";
        String commentairesValeur = (demande.getCommentaires() != null) ? demande.getCommentaires() : "";
        String documentFinalValeur = (demande.getDocumentFinal() != null) ? demande.getDocumentFinal() : "";

        String typeDemandeSelected = (demande.getTypeDemande() != null) ? demande.getTypeDemande().name() : "";
        String prioriteSelected = (demande.getPriorite() != null) ? demande.getPriorite().name()
                : PrioriteDemande.NORMALE.name();
        String citoyenSelected = (demande.getCitoyenRequerant() != null)
                ? String.valueOf(demande.getCitoyenRequerant().getId())
                : "";

        // ajoute indicateur
        StatutDemande statut = (demande.getStatut() != null) ? demande.getStatut()
                : StatutDemande.SOUMISE;

        // ajout indicateur
        Map<String, String> mapStatus = new HashMap<>();
        for (StatutDemande s : StatutDemande.values()) {
            mapStatus.put(s.name(), (s == statut ? "* " : "  ") + s.name());
        }

        // Formatage optimisé pour la recherche (NOM Prénom)
        Map<String, String> mapCitoyens = citoyens.stream().collect(Collectors.toMap(
                c -> String.valueOf(c.getId()),
                c -> (c.getNom() + " " + c.getPrenom() + " (" + c.getNin() + ")").toUpperCase(),
                (v1, v2) -> v1, LinkedHashMap::new));

        // 2. Dessin du formulaire via l'API fluide du FormBuilder
        return new FormContainer(actionUrl)
                .addRow(FormRow.single(FormItem.hidden("id", idValeur)))

                // Ligne 1 : Numéro et Type
                .addRow(FormRow.duo(
                        FormItem.input(FormItemType.TEXT, "numeroSuivi", "Numéro de suivi")
                                .value(numeroSuiviValeur)
                                .placeholder("Génération automatique si vide"),
                        FormItem.select("typeDemande", "Type de demande", TypeDemande.values())
                                .value(typeDemandeSelected)
                                .required()))

                // Ligne 2 : Citoyen et Priorité
                .addRow(FormRow.duo(
                        FormItem.select("idCitoyenPrincipal", "Citoyen requérant", mapCitoyens)
                                .value(citoyenSelected)
                                .required()
                                .searchable(),
                        FormItem.select("priorite", "Priorité de la demande",
                                PrioriteDemande.values())
                                .value(prioriteSelected)
                                .required()))

                // Ligne 3 : Statut et Document Final
                .addRow(FormRow.duo(
                        FormItem.select("statut", "Statut du traitement",
                                mapStatus)
                                .value(statut.name())
                                .required(),
                        FormItem.input(FormItemType.TEXT, "documentFinal",
                                "Document final (Réf ou Lien)")
                                .value(documentFinalValeur)
                                .placeholder("ex: certif_naissance_2026.pdf")))

                // Ligne 4 : Description longue
                .addRow(FormRow.single(
                        FormItem.input(FormItemType.TEXT, "description",
                                "Description / Justification")
                                .value(descriptionValeur)
                                .placeholder("Détails complets de la demande...")
                                .required()))

                // Ligne 5 : Rejet et Commentaires internes
                .addRow(FormRow.duo(
                        FormItem.input(FormItemType.TEXT, "motifRejet", "Motif de rejet")
                                .value(motifRejetValeur)
                                .placeholder("À remplir uniquement si rejetée..."),
                        FormItem.input(FormItemType.TEXT, "commentaires",
                                "Notes internes / Commentaires")
                                .value(commentairesValeur)
                                .placeholder("Notes réservées à l'administration...")))

                .readOnly(readOnly)
                .render();
    }
}