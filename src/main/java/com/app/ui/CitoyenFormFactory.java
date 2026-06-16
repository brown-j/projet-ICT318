package com.app.ui;

import com.app.jpa.model.Citoyen;
import com.app.jpa.model.JPAEnum.Sexe;
import com.app.jpa.model.JPAEnum.SituationMatrimoniale;
import com.app.jpa.model.JPAEnum.StatutCitoyen;
import com.app.ui.FormBuilder.*;

public class CitoyenFormFactory {
    // Une seule méthode qui centralise la création du formulaire pour tout le
    // projet !
    public static String genererHtml(Citoyen citoyen, String actionUrl, boolean readOnly) {

        // Protection contre les NullPointerExceptions
        String idValeur = (citoyen.getId() != null) ? String.valueOf(citoyen.getId()) : "";
        String dateValeur = (citoyen.getDateNaissance() != null) ? citoyen.getDateNaissance().toString() : "";
        String sexeValeur = (citoyen.getSexe() != null) ? citoyen.getSexe().name() : "";
        String situationValeur = (citoyen.getSituationMatrimoniale() != null)
                ? citoyen.getSituationMatrimoniale().name()
                : "";
        String statutValeur = (citoyen.getStatut() != null) ? citoyen.getStatut().name()
                : StatutCitoyen.ACTIF.name();
        boolean isEdit = citoyen.getNin() == null ? false : !citoyen.getNin().isEmpty();

        return new FormContainer(actionUrl)
                .addRow(FormRow.single(FormItem.hidden("id", idValeur)))
                .addRow(FormRow.duo(
                        FormItem.input(FormItemType.TEXT, "nom", "Nom").value(citoyen.getNom())
                                .placeholder("ex: Mbarga").required(),
                        FormItem.input(FormItemType.TEXT, "prenom", "Prénom(s)")
                                .value(citoyen.getPrenom())
                                .placeholder("ex: Jean-Paul").required()))
                .addRow(FormRow.duo(
                        FormItem.input(FormItemType.DATE, "dateNaissance", "Date de naissance")
                                .value(dateValeur)
                                .required(),
                        FormItem.select("sexe", "Sexe", Sexe.values()).value(sexeValeur)
                                .required()))
                .addRow(FormRow.single(
                        FormItem.input(FormItemType.TEXT, "nin", "NIN (Numéro National)")
                                .value(citoyen.getNin())
                                .readonly(isEdit)
                                .placeholder("Génération automatique si vide")))
                .addRow(FormRow.duo(
                        FormItem.input(FormItemType.TEXT, "lieuNaissance", "Lieu de naissance")
                                .value(citoyen.getLieuNaissance())
                                .placeholder("ex: Yaoundé").required(),
                        FormItem.input(FormItemType.TEXT, "adresse", "Quartier / Adresse")
                                .value(citoyen.getAdresse())
                                .placeholder("ex: Bastos").required()))
                .addRow(FormRow.duo(
                        FormItem.select("situationMatrimoniale", "Situation matrimoniale",
                                SituationMatrimoniale.values()).value(situationValeur)
                                .required(),
                        FormItem.input(FormItemType.EMAIL, "email", "Email")
                                .value(citoyen.getEmail())
                                .placeholder("email@exemple.cm")))
                .addRow(FormRow.duo(
                        FormItem.input(FormItemType.TEL, "telephone", "Téléphone")
                                .value(citoyen.getTelephone())
                                .placeholder("ex: 6XX XX XX XX"),
                        FormItem.select("statut", "Statut", StatutCitoyen.values())
                                .value(statutValeur).required()))
                .readOnly(readOnly)
                .render();
    }
}