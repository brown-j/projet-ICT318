// Path: src/main/java/com/exemple/model/Article.java
package com.app.model;

import java.io.Serializable;

public class Article implements Serializable {
    private String nom;
    private double prix;
    private boolean enPromo;

    // Constructeur vide obligatoire
    public Article() {
    }

    public Article(String nom, double prix, boolean enPromo) {
        this.nom = nom;
        this.prix = prix;
        this.enPromo = enPromo;
    }

    // Getters et Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public boolean isEnPromo() {
        return enPromo;
    }

    public void setEnPromo(boolean enPromo) {
        this.enPromo = enPromo;
    }
}