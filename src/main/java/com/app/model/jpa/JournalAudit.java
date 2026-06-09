package com.app.model.jpa;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "journal_audit")
public class JournalAudit implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_audit")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_officier", nullable = false)
    private OfficierEtatCivil officier;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(name = "table_affectee", nullable = false, length = 50)
    private String tableAffectee;

    @Column(name = "donnees_json", columnDefinition = "JSON")
    private String donneesJson;

    @Column(name = "date_action", updatable = false)
    private LocalDateTime dateAction;

    @Column(name = "adresse_ip", length = 45)
    private String adresseIp;

    @PrePersist
    protected void onCreate() {
        dateAction = LocalDateTime.now();
    }

    // Constructeur vide
    public JournalAudit() {
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OfficierEtatCivil getOfficier() {
        return officier;
    }

    public void setOfficier(OfficierEtatCivil officier) {
        this.officier = officier;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTableAffectee() {
        return tableAffectee;
    }

    public void setTableAffectee(String tableAffectee) {
        this.tableAffectee = tableAffectee;
    }

    public String getDonneesJson() {
        return donneesJson;
    }

    public void setDonneesJson(String donneesJson) {
        this.donneesJson = donneesJson;
    }

    public LocalDateTime getDateAction() {
        return dateAction;
    }

    public void setDateAction(LocalDateTime dateAction) {
        this.dateAction = dateAction;
    }

    public String getAdresseIp() {
        return adresseIp;
    }

    public void setAdresseIp(String adresseIp) {
        this.adresseIp = adresseIp;
    }
}
