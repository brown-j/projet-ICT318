package com.app.jpa.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "type_acte")
public class TypeActe implements Serializable {

    @Id
    @Column(name = "code_type", length = 30)
    private String code; // Ex: "ACTE_NAISS", "EXTR_NAISS", "ACTE_MARI", "CERT_CELIBAT"

    @Column(nullable = false, length = 100)
    private String libelle; // Ex: "Extrait d'Acte de Naissance"

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "tarif_fcfa", nullable = false)
    private int tarifFCFA; // Ex: 2000

    @Column(name = "template_path", nullable = false, length = 255)
    private String templatePath; // Ex: "/templates/naissance_extrait.html"

    // 💡 CORRECTION : Utilisation de l'Enum CategorieActe avec @Enumerated
    @Enumerated(EnumType.STRING)
    @Column(name = "categorie_parent", nullable = false, length = 30)
    private JPAEnum.CategorieActe categorieParent;

    // Constructeur vide obligatoire pour JPA
    public TypeActe() {
    }

    // --- Getters ---
    public String[] getDocsRequis() {
        // TODO: setter & persist
        String docsRequis = "Copie d'Acte de Naissance;Pièce d'Identité du requérant;Plan de situation de la propriété;Titre de propriété foncier";
        return docsRequis.split(";");
    }

    public String getCode() {
        return code;
    }

    public String getLibelle() {
        return libelle;
    }

    public String getDescription() {
        return description;
    }

    public int getTarifFCFA() {
        return tarifFCFA;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public JPAEnum.CategorieActe getCategorieParent() {
        return categorieParent;
    }

    // --- Setters ---

    public void setCode(String code) {
        this.code = code;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTarifFCFA(int tarifFCFA) {
        this.tarifFCFA = tarifFCFA;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public void setCategorieParent(JPAEnum.CategorieActe categorieParent) {
        this.categorieParent = categorieParent;
    }
}