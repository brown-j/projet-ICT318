package com.app.jpa.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.app.jpa.model.JPAEnum.StatutActe;
import com.app.jpa.model.JPAEnum.TypeActe;

@Entity
@Table(name = "acte_etat_civil")
public class ActeEtatCivil implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acte")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_acte", nullable = false, length = 30)
    private TypeActe typeActe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_citoyen", nullable = false)
    private Citoyen citoyenPrincipal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_citoyen2")
    private Citoyen citoyenSecondaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_officier", nullable = false)
    private OfficierEtatCivil officierSignataire;

    @Column(name = "numero_acte", nullable = false, unique = true, length = 50)
    private String numeroActe;

    @Column(name = "date_etablissement", nullable = false)
    private LocalDate dateEtablissement;

    @Column(name = "date_evenement", nullable = false)
    private LocalDate dateEvenement;

    @Column(name = "lieu_evenement", nullable = false, length = 200)
    private String lieuEvenement;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutActe statut = StatutActe.EN_COURS;

    @Column(name = "fichier_pdf", length = 255)
    private String fichierPdf;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }

    // Constructeur vide
    public ActeEtatCivil() {
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeActe getTypeActe() {
        return typeActe;
    }

    public void setTypeActe(TypeActe typeActe) {
        this.typeActe = typeActe;
    }

    public Citoyen getCitoyenPrincipal() {
        return citoyenPrincipal;
    }

    public void setCitoyenPrincipal(Citoyen citoyenPrincipal) {
        this.citoyenPrincipal = citoyenPrincipal;
    }

    public Citoyen getCitoyenSecondaire() {
        return citoyenSecondaire;
    }

    public void setCitoyenSecondaire(Citoyen citoyenSecondaire) {
        this.citoyenSecondaire = citoyenSecondaire;
    }

    public OfficierEtatCivil getOfficierSignataire() {
        return officierSignataire;
    }

    public void setOfficierSignataire(OfficierEtatCivil officierSignataire) {
        this.officierSignataire = officierSignataire;
    }

    public String getNumeroActe() {
        return numeroActe;
    }

    public void setNumeroActe(String numeroActe) {
        this.numeroActe = numeroActe;
    }

    public LocalDate getDateEtablissement() {
        return dateEtablissement;
    }

    public void setDateEtablissement(LocalDate dateEtablissement) {
        this.dateEtablissement = dateEtablissement;
    }

    public LocalDate getDateEvenement() {
        return dateEvenement;
    }

    public void setDateEvenement(LocalDate dateEvenement) {
        this.dateEvenement = dateEvenement;
    }

    public String getLieuEvenement() {
        return lieuEvenement;
    }

    public void setLieuEvenement(String lieuEvenement) {
        this.lieuEvenement = lieuEvenement;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public StatutActe getStatut() {
        return statut;
    }

    public void setStatut(StatutActe statut) {
        this.statut = statut;
    }

    public String getFichierPdf() {
        return fichierPdf;
    }

    public void setFichierPdf(String fichierPdf) {
        this.fichierPdf = fichierPdf;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }
}
