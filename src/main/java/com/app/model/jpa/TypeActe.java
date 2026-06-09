package com.app.model.jpa;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "type_acte")
public class TypeActe implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type_acte")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String libelle;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Constructeur vide
    public TypeActe() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
