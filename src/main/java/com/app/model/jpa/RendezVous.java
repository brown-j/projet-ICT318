package com.app.model.jpa;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.app.model.jpa.enums.StatutRendezVous;

@Entity
@Table(name = "rendez_vous")
public class RendezVous implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rendez_vous")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_citoyen", nullable = false)
    private Citoyen citoyen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_officier", nullable = false)
    private OfficierEtatCivil officier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demande")
    private DemandeAdministrative demande;

    @Column(name = "date_heure", nullable = false)
    private LocalDateTime dateHeure;

    @Column(nullable = false, length = 150)
    private String motif;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutRendezVous statut = StatutRendezVous.PLANIFIE;

    // Constructeur vide
    public RendezVous() {
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Citoyen getCitoyen() {
        return citoyen;
    }

    public void setCitoyen(Citoyen citoyen) {
        this.citoyen = citoyen;
    }

    public OfficierEtatCivil getOfficier() {
        return officier;
    }

    public void setOfficier(OfficierEtatCivil officier) {
        this.officier = officier;
    }

    public DemandeAdministrative getDemande() {
        return demande;
    }

    public void setDemande(DemandeAdministrative demande) {
        this.demande = demande;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public StatutRendezVous getStatut() {
        return statut;
    }

    public void setStatut(StatutRendezVous statut) {
        this.statut = statut;
    }
}
