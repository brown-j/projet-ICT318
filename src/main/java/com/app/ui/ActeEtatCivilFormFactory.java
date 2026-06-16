package com.app.ui;

import com.app.jpa.model.ActeEtatCivil;
import com.app.jpa.model.Citoyen;
import com.app.jpa.model.OfficierEtatCivil;
import com.app.jpa.model.TypeActe;
import com.app.jpa.model.JPAEnum.StatutActe;
import com.app.ui.FormBuilder.*;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class ActeEtatCivilFormFactory {

    public static String genererHtml(ActeEtatCivil acte, List<TypeActe> types, List<Citoyen> citoyens,
            List<OfficierEtatCivil> officiers, String actionUrl, boolean readOnly) {

        // 1. Préparation des variables et protections Null-Safe
        String idValeur = (acte.getId() != null) ? String.valueOf(acte.getId()) : "";
        String numeroActeValeur = (acte.getNumeroActe() != null) ? acte.getNumeroActe() : "";
        String lieuEvenementValeur = (acte.getLieuEvenement() != null) ? acte.getLieuEvenement() : "";
        String observationsValeur = (acte.getObservations() != null) ? acte.getObservations() : "";
        String fichierPdfValeur = (acte.getFichierPdf() != null) ? acte.getFichierPdf() : "";

        boolean isEdit = !numeroActeValeur.isEmpty();

        String dateEtablissementValeur = (acte.getDateEtablissement() != null)
                ? acte.getDateEtablissement().toString()
                : "";
        String dateEvenementValeur = (acte.getDateEvenement() != null) ? acte.getDateEvenement().toString()
                : "";

        String typeActeSelected = (acte.getTypeActe() != null) ? acte.getTypeActe().getCode() : "";
        String citoyenPrincipalSelected = (acte.getCitoyenPrincipal() != null)
                ? String.valueOf(acte.getCitoyenPrincipal().getId())
                : "";
        String citoyenSecondaireSelected = (acte.getCitoyenSecondaire() != null)
                ? String.valueOf(acte.getCitoyenSecondaire().getId())
                : "";
        String officierSelected = (acte.getOfficierSignataire() != null)
                ? String.valueOf(acte.getOfficierSignataire().getId())
                : "";
        String statutValeur = (acte.getStatut() != null) ? acte.getStatut().name() : StatutActe.EN_COURS.name();

        // Formatage optimisé pour la recherche (NOM Prénom)
        Map<String, String> mapCitoyens = citoyens.stream().collect(Collectors.toMap(
                c -> String.valueOf(c.getId()),
                c -> (c.getNom() + " " + c.getPrenom() + " (" + c.getNin() + ")").toUpperCase(),
                (v1, v2) -> v1, LinkedHashMap::new));

        Map<String, String> mapOfficiers = officiers.stream().collect(Collectors.toMap(
                o -> String.valueOf(o.getId()),
                o -> o.getNom() + " " + o.getPrenom(),
                (v1, v2) -> v1, LinkedHashMap::new));

        // Création de la Map pour le catalogue des types d'actes
        Map<String, String> mapTypesActe = types.stream().collect(Collectors.toMap(
                TypeActe::getCode,
                TypeActe::getLibelle,
                (v1, v2) -> v1, LinkedHashMap::new));

        // 2. Dessin du formulaire via l'API fluide du FormBuilder
        return new FormContainer(actionUrl)
                .addRow(FormRow.single(FormItem.hidden("id", idValeur)))
                .addRow(FormRow.duo(
                        FormItem.input(FormItemType.TEXT, "numeroActe", "Numéro de l'acte")
                                .value(numeroActeValeur)
                                .readonly(isEdit)
                                .placeholder("Génération automatique si vide"),
                        FormItem.select("idTypeActe", "Type d'acte", mapTypesActe)
                                .value(typeActeSelected)
                                .readonly(isEdit)
                                .required()))
                .addRow(FormRow.duo(
                        FormItem.select("idCitoyenPrincipal", "Citoyen principal", mapCitoyens)
                                .value(citoyenPrincipalSelected)
                                .required()
                                .searchable(),
                        FormItem.select("idCitoyenSecondaire", "Citoyen secondaire (Optionnel)",
                                mapCitoyens)
                                .value(citoyenSecondaireSelected)
                                .searchable()))
                .addRow(FormRow.duo(
                        FormItem.input(FormItemType.DATE, "dateEvenement",
                                "Date de l'événement")
                                .value(dateEvenementValeur)
                                .required(),
                        FormItem.input(FormItemType.TEXT, "lieuEvenement",
                                "Lieu de l'événement")
                                .value(lieuEvenementValeur)
                                .placeholder("ex: Mairie de Yaoundé II")
                                .required()))
                .addRow(FormRow.duo(
                        FormItem.input(FormItemType.DATE, "dateEtablissement",
                                "Date d'établissement")
                                .value(dateEtablissementValeur)
                                .required(),
                        FormItem.select("idOfficier", "Officier signataire", mapOfficiers)
                                .value(officierSelected)
                                .required()
                                .searchable()))
                .addRow(FormRow.duo(
                        FormItem.select("statut", "Statut de l'acte", StatutActe.values())
                                .value(statutValeur)
                                .required(),
                        // 💡 CORRECTION 1 : Type de champ modifié en FILE pour l'upload
                        FormItem.input(FormItemType.FILE, "fichierPdf", "Fichier PDF numérisé")
                                .value(fichierPdfValeur)
                                .placeholder("ex: acte_naissance_2026_45.pdf")))
                .addRow(FormRow.single(
                        FormItem.input(FormItemType.TEXT, "observations",
                                "Observations / Mentions marginales")
                                .value(observationsValeur)
                                .placeholder("Notes supplémentaires...")))
                .readOnly(readOnly)
                .multipart() // 💡 CORRECTION 2 : Ajout du support de l'encapsulation multipart/form-data
                .render();
    }
}