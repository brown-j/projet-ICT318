package com.app.jpa.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.app.jpa.model.JPAEnum.PrioriteDemande;
import com.app.jpa.model.JPAEnum.StatutDemande;
import com.app.jpa.model.JPAEnum.TypeDemande;

@Entity
@Table(name = "demande_administrative")
public class DemandeAdministrative implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_demande")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_demande", nullable = false, length = 50)
    private TypeDemande typeDemande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_citoyen", nullable = false)
    private Citoyen citoyenRequerant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agent")
    private OfficierEtatCivil agentEnCharge;

    @Column(name = "numero_suivi", nullable = false, unique = true, length = 30)
    private String numeroSuivi;

    @Column(name = "date_soumission", updatable = false)
    private LocalDateTime dateSoumission;

    @Column(name = "date_traitement")
    private LocalDate dateTraitement;

    @Column(name = "date_cloture")
    private LocalDate dateCloture;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutDemande statut = StatutDemande.SOUMISE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrioriteDemande priorite = PrioriteDemande.NORMALE;

    @Column(name = "motif_rejet", columnDefinition = "TEXT")
    private String motifRejet;

    @Column(name = "document_final", length = 255)
    private String documentFinal;

    @Column(columnDefinition = "TEXT")
    private String commentaires;

    @PrePersist
    protected void onCreate() {
        if (dateSoumission == null) {
            dateSoumission = LocalDateTime.now();
        }
    }

    // Constructeur vide
    public DemandeAdministrative() {
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeDemande getTypeDemande() {
        return typeDemande;
    }

    public void setTypeDemande(TypeDemande typeDemande) {
        this.typeDemande = typeDemande;
    }

    public Citoyen getCitoyenRequerant() {
        return citoyenRequerant;
    }

    public void setCitoyenRequerant(Citoyen citoyenRequerant) {
        this.citoyenRequerant = citoyenRequerant;
    }

    public OfficierEtatCivil getAgentEnCharge() {
        return agentEnCharge;
    }

    public void setAgentEnCharge(OfficierEtatCivil agentEnCharge) {
        this.agentEnCharge = agentEnCharge;
    }

    public String getNumeroSuivi() {
        return numeroSuivi;
    }

    public void setNumeroSuivi(String numeroSuivi) {
        this.numeroSuivi = numeroSuivi;
    }

    public LocalDateTime getDateSoumission() {
        return dateSoumission;
    }

    public void setDateSoumission(LocalDateTime dateSoumission) {
        this.dateSoumission = dateSoumission;
    }

    public LocalDate getDateTraitement() {
        return dateTraitement;
    }

    public void setDateTraitement(LocalDate dateTraitement) {
        this.dateTraitement = dateTraitement;
    }

    public LocalDate getDateCloture() {
        return dateCloture;
    }

    public void setDateCloture(LocalDate dateCloture) {
        this.dateCloture = dateCloture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatutDemande getStatut() {
        return statut;
    }

    public void setStatut(StatutDemande statut) {
        this.statut = statut;
    }

    public PrioriteDemande getPriorite() {
        return priorite;
    }

    public void setPriorite(PrioriteDemande priorite) {
        this.priorite = priorite;
    }

    public String getMotifRejet() {
        return motifRejet;
    }

    public void setMotifRejet(String motifRejet) {
        this.motifRejet = motifRejet;
    }

    public String getDocumentFinal() {
        return documentFinal;
    }

    public void setDocumentFinal(String documentFinal) {
        this.documentFinal = documentFinal;
    }

    public String getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(String commentaires) {
        this.commentaires = commentaires;
    }
}
