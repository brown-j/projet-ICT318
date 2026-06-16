package com.app.ui;

import com.app.jpa.model.Citoyen;
import com.app.jpa.model.DemandeAdministrative;
import com.app.jpa.model.TypeActe;
import com.app.jpa.model.JPAEnum.PrioriteDemande;
import com.app.jpa.model.JPAEnum.StatutDemande;
import com.app.ui.FormBuilder.*;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class DemandeFormFactory {

    public static String genererHtml(DemandeAdministrative demande, List<TypeActe> types,
            List<Citoyen> citoyens, String actionUrl, boolean readOnly) {

        // 1. Préparation des variables
        String idValeur = (demande.getId() != null) ? String.valueOf(demande.getId()) : "";
        String numeroSuiviValeur = (demande.getNumeroSuivi() != null) ? demande.getNumeroSuivi() : "";
        String descriptionValeur = (demande.getDescription() != null) ? demande.getDescription() : "";
        String motifRejetValeur = (demande.getMotifRejet() != null) ? demande.getMotifRejet() : "";
        String commentairesValeur = (demande.getCommentaires() != null) ? demande.getCommentaires() : "";

        // 💡 Le document final stocke désormais le Numéro de l'Acte
        String documentFinalValeur = (demande.getDocumentFinal() != null) ? demande.getDocumentFinal() : "";

        boolean isEdit = !numeroSuiviValeur.isEmpty();

        String typeActeSelected = (demande.getTypeActe() != null) ? demande.getTypeActe().getCode() : "";
        String prioriteSelected = (demande.getPriorite() != null) ? demande.getPriorite().name()
                : PrioriteDemande.NORMALE.name();

        String citoyenSelected = (demande.getCitoyenRequerant() != null)
                ? String.valueOf(demande.getCitoyenRequerant().getId())
                : "";

        StatutDemande statut = (demande.getStatut() != null) ? demande.getStatut() : StatutDemande.SOUMISE;

        Map<String, String> mapStatus = new LinkedHashMap<>();
        for (StatutDemande s : StatutDemande.values()) {
            mapStatus.put(s.name(), (s == statut ? "👉 " : "") + s.name().replace("_", " "));
        }

        Map<String, String> mapCitoyens = citoyens.stream().collect(Collectors.toMap(
                c -> String.valueOf(c.getId()),
                c -> (c.getNom() + " " + c.getPrenom() + " (" + c.getNin() + ")").toUpperCase(),
                (v1, v2) -> v1, LinkedHashMap::new));

        Map<String, String> mapTypesActe = types.stream().collect(Collectors.toMap(
                TypeActe::getCode,
                TypeActe::getLibelle,
                (v1, v2) -> v1, LinkedHashMap::new));

        // 2. Dessin du formulaire
        return new FormContainer(actionUrl)
                .addRow(FormRow.single(FormItem.hidden("id", idValeur)))

                .addRow(FormRow.duo(
                        FormItem.input(FormItemType.TEXT, "numeroSuivi", "Numéro de suivi")
                                .value(numeroSuiviValeur)
                                .readonly(isEdit)
                                .placeholder("Génération automatique si vide"),
                        FormItem.select("idTypeActe", "Prestation demandée (Catalogue)", mapTypesActe)
                                .value(typeActeSelected)
                                .readonly(isEdit)
                                .required()))

                .addRow(FormRow.single(
                        FormItem.select("idCitoyenPrincipal", "Citoyen requérant", mapCitoyens)
                                .value(citoyenSelected)
                                .readonly(isEdit)
                                .required()
                                .searchable()))

                .addRow(FormRow.duo(
                        FormItem.select("priorite", "Priorité de traitement", PrioriteDemande.values())
                                .value(prioriteSelected)
                                .required(),
                        FormItem.select("statut", "Statut d'avancement", mapStatus)
                                .value(statut.name())
                                .required()))

                .addRow(FormRow.single(
                        FormItem.input(FormItemType.TEXT, "description", "Description / Justification")
                                .value(descriptionValeur)
                                .placeholder("Détails complets de la demande...")
                                .required()))

                // 🌟 CORRECTION LIGNE 5 : Changement du rôle du champ
                .addRow(FormRow.duo(
                        FormItem.input(FormItemType.TEXT, "documentFinal",
                                "N° de l'Acte correspondant (ne rien ecrire)")
                                .value(documentFinalValeur)
                                .placeholder("Génération automatique...")
                                .readonly(true),
                        FormItem.input(FormItemType.TEXT, "motifRejet", "Motif de rejet")
                                .value(motifRejetValeur)
                                .placeholder("À remplir uniquement si rejetée...")))

                .addRow(FormRow.single(
                        FormItem.input(FormItemType.TEXT, "commentaires", "Notes internes / Commentaires")
                                .value(commentairesValeur)
                                .placeholder("Notes réservées exclusivement à l'administration...")))

                .readOnly(readOnly)
                .render();
    }
}