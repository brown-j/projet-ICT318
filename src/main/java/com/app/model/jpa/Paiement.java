package com.app.model.jpa;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.app.model.jpa.enums.ModePaiement;
import com.app.model.jpa.enums.ModePaiement;

@Entity
@Table(name = "paiement")
public class Paiement implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paiement")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demande")
    private DemandeAdministrative demande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_officier", nullable = false)
    private OfficierEtatCivil officierCaissier;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_paiement", nullable = false, length = 50)
    private ModePaiement modePaiement;

    @Column(name = "reference_recu", nullable = false, unique = true, length = 100)
    private String referenceRecu;

    @Column(name = "date_paiement", updatable = false)
    private LocalDateTime datePaiement;

    @PrePersist
    protected void onCreate() {
        datePaiement = LocalDateTime.now();
    }

    // Constructeur vide
    public Paiement() {
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

    public OfficierEtatCivil getOfficierCaissier() {
        return officierCaissier;
    }

    public void setOfficierCaissier(OfficierEtatCivil officierCaissier) {
        this.officierCaissier = officierCaissier;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public ModePaiement getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(ModePaiement modePaiement) {
        this.modePaiement = modePaiement;
    }

    public String getReferenceRecu() {
        return referenceRecu;
    }

    public void setReferenceRecu(String referenceRecu) {
        this.referenceRecu = referenceRecu;
    }

    public LocalDateTime getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDateTime datePaiement) {
        this.datePaiement = datePaiement;
    }
}
