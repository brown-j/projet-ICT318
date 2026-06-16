package com.app.ui;

import com.app.jpa.model.TypeActe;
import com.app.jpa.model.JPAEnum.CategorieActe;
import com.app.ui.FormBuilder.*;

public class TypeActeFormFactory {

    public static String genererHtml(TypeActe typeActe, String actionUrl, boolean readOnly) {

        // 1. Préparation des variables et protections Null-Safe
        String codeValeur = (typeActe.getCode() != null) ? typeActe.getCode() : "";
        String libelleValeur = (typeActe.getLibelle() != null) ? typeActe.getLibelle() : "";
        String tarifValeur = String.valueOf(typeActe.getTarifFCFA());
        String templatePathValeur = (typeActe.getTemplatePath() != null) ? typeActe.getTemplatePath() : "";
        String categorieParentSelected = (typeActe.getCategorieParent() != null)
                ? typeActe.getCategorieParent().name()
                : "";
        String descriptionValeur = (typeActe.getDescription() != null) ? typeActe.getDescription() : "";

        // Un type d'acte est en mode édition si son code unique est déjà assigné
        boolean isEdit = !codeValeur.isEmpty();

        // 2. Dessin du formulaire via l'API fluide du FormBuilder
        return new FormContainer(actionUrl)
                .addRow(FormRow.duo(
                        FormItem.input(FormItemType.TEXT, "code", "Code unique")
                                .value(codeValeur)
                                .readonly(isEdit) // Interdit la modification de la clé primaire `@Id`
                                .placeholder("Ex: EXTR_NAISS, ACTE_MARI")
                                .required(),
                        FormItem.input(FormItemType.TEXT, "libelle", "Nom officiel affiché")
                                .value(libelleValeur)
                                .placeholder("Ex: Extrait d'Acte de Naissance")
                                .required()))
                .addRow(FormRow.duo(
                        FormItem.input(FormItemType.NUMBER, "tarifFCFA", "Tarif légal (FCFA)")
                                .value(tarifValeur)
                                .required(),
                        FormItem.select("categorieParent", "Registre ou Secteur Parent", CategorieActe.values())
                                .value(categorieParentSelected)
                                .required()))
                .addRow(FormRow.single(
                        FormItem.input(FormItemType.FILE, "templatePath", "Chemin d'accès du Gabarit / Template")
                                .value(templatePathValeur)
                                .placeholder("Ex: template_naissance_extrait.pdf")))
                .addRow(FormRow.single(
                        FormItem.input(FormItemType.TEXT, "description",
                                "Description de l'acte / Notes administratives")
                                .value(descriptionValeur)
                                .placeholder("Ex: Document délivré pour les démarches courantes...")))
                .readOnly(readOnly)
                .multipart()
                .render();
    }
}