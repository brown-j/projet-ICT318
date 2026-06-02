package com.app.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "piece_jointe")
public class PieceJointe implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_piece")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demande")
    private DemandeAdministrative demande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_citoyen")
    private Citoyen citoyen;

    @Column(name = "nom_fichier", nullable = false, length = 255)
    private String nomFichier;

    @Column(name = "chemin_fichier", nullable = false, length = 255)
    private String cheminFichier;

    @Column(name = "type_mime", nullable = false, length = 100)
    private String typeMime;

    @Column(name = "date_upload", updatable = false)
    private LocalDateTime dateUpload;

    @PrePersist
    protected void onCreate() {
        dateUpload = LocalDateTime.now();
    }

    // Constructeur vide
    public PieceJointe() {
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DemandeAdministrative getDemande() {
        return demande;
    }

    public void setDemande(DemandeAdministrative demande) {
        this.demande = demande;
    }

    public Citoyen getCitoyen() {
        return citoyen;
    }

    public void setCitoyen(Citoyen citoyen) {
        this.citoyen = citoyen;
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public String getCheminFichier() {
        return cheminFichier;
    }

    public void setCheminFichier(String cheminFichier) {
        this.cheminFichier = cheminFichier;
    }

    public String getTypeMime() {
        return typeMime;
    }

    public void setTypeMime(String typeMime) {
        this.typeMime = typeMime;
    }

    public LocalDateTime getDateUpload() {
        return dateUpload;
    }

    public void setDateUpload(LocalDateTime dateUpload) {
        this.dateUpload = dateUpload;
    }
}
