package com.app.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "type_demande")
public class TypeDemande implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type_demande")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String libelle;

    @Column(name = "delai_standard_jours")
    private Integer delaiStandardJours = 3;

    @Column(name = "tarif_fcfa", precision = 10, scale = 2)
    private BigDecimal tarifFcfa = BigDecimal.ZERO;

    // Constructeur vide
    public TypeDemande() {
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Integer getDelaiStandardJours() {
        return delaiStandardJours;
    }

    public void setDelaiStandardJours(Integer delaiStandardJours) {
        this.delaiStandardJours = delaiStandardJours;
    }

    public BigDecimal getTarifFcfa() {
        return tarifFcfa;
    }

    public void setTarifFcfa(BigDecimal tarifFcfa) {
        this.tarifFcfa = tarifFcfa;
    }
}
