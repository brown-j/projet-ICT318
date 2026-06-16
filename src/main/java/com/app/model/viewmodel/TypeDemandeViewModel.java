package com.app.model.viewmodel;

import com.app.model.icon.Icons;
import com.app.model.theme.ThemeColor;
import java.util.List;

public class TypeDemandeViewModel {
    private final String code;
    private final String label;
    private final String categorie;
    private final String delaiEstimation;
    private final List<String> piecesRequises;
    private final String iconClass; // Génère automatiquement "ti ti-file-certificate", etc.
    private final String colorClass; // Génère automatiquement "primary", "success", etc.

    public TypeDemandeViewModel(String code, String label, String categorie, String delaiEstimation,
            List<String> piecesRequises, Icons icon, ThemeColor theme) {
        this.code = code;
        this.label = label;
        this.categorie = categorie;
        this.delaiEstimation = delaiEstimation;
        this.piecesRequises = piecesRequises;

        // Extraction propre et sécurisée depuis tes énumérations fournies
        this.iconClass = (icon != null) ? icon.toString() : "";
        this.colorClass = (theme != null) ? theme.getCssClass() : ThemeColor.PRIMARY.getCssClass();
    }

    // Getters pour la sérialisation JSON (Gson) et l'accès dans tes pages JSP
    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public String getCategorie() {
        return categorie;
    }

    public String getDelaiEstimation() {
        return delaiEstimation;
    }

    public List<String> getPiecesRequises() {
        return piecesRequises;
    }

    public String getIconClass() {
        return iconClass;
    }

    public String getColorClass() {
        return colorClass;
    }
}