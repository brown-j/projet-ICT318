package com.app.jpa.model;

import java.math.BigDecimal;

public class JPAEnum {

    public enum ModePaiement {
        ESPECES("Espèces"),
        MOBILE_MONEY("Mobile Money"),
        CARTE_BANCAIRE("Carte Bancaire"),
        VIREMENT("Virement");

        private final String libelle;

        ModePaiement(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public enum PrioriteDemande {
        BASSE("Basse"),
        NORMALE("Normale"),
        HAUTE("Haute"),
        URGENTE("Urgente");

        private final String libelle;

        PrioriteDemande(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public enum Role {
        SUPER_ADMIN("Super Administrateur", "Le compte racine unique"),
        ADMIN("Administrateur",
                "Gestion complète de la plateforme, des configurations et des comptes utilisateurs."),
        OFFICIER("Officier d'État Civil", "Validation, approbation et signature numérique des actes d'état civil."),
        AGENT_SAISIE("Agent de Saisie",
                "Réception des déclarations, enregistrement initial et pré-remplissage des dossiers."),
        CITOYEN("Citoyen", "Utilisateur externe habilité à formuler des demandes d'actes et suivre ses dossiers.");

        private final String nom;
        private final String description;

        Role(String nom, String description) {
            this.nom = nom;
            this.description = description;
        }

        public String getNom() {
            return nom;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum Sexe {
        M("Masculin"),
        F("Féminin");

        private final String libelle;

        Sexe(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public enum SituationMatrimoniale {
        CELIBATAIRE("Célibataire"),
        MARIE("Marié(e)"),
        DIVORCE("Divorcé(e)"),
        VEUF("Veuf(ve)");

        private final String libelle;

        SituationMatrimoniale(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public enum StatutActe {
        EN_COURS("En cours"),
        DELIVRE("Délivré"),
        ANNULE("Annulé"),
        ARCHIVE("Archivé");

        private final String libelle;

        StatutActe(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public enum StatutCitoyen {
        ACTIF("Actif"),
        ARCHIVE("Archivé"),
        DECEDE("Décédé");

        private final String libelle;

        StatutCitoyen(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public enum StatutDemande {
        SOUMISE("Soumise"),
        EN_COURS("En cours"),
        VALIDEE("Validée"),
        REJETEE("Rejetée"),
        CLOTUREE("Clôturée");

        private final String libelle;

        StatutDemande(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public enum StatutOfficier {
        ACTIF("Actif"),
        SUSPENDU("Suspendu"),
        INACTIF("Inactif");

        private final String libelle;

        StatutOfficier(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public enum StatutRendezVous {
        PLANIFIE("Planifié"),
        HONORE("Honoré"),
        ANNULE("Annulé"),
        REPORTE("Reporté");

        private final String libelle;

        StatutRendezVous(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public enum TypeActe {
        NAISSANCE("Naissance"),
        MARIAGE("Mariage"),
        DECES("Décès");

        private final String libelle;

        TypeActe(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public enum TypeDemande {
        ACTE_NAISSANCE("acte de naissance", 3, new BigDecimal("3000.00")),
        COPIE_NAISSANCE("Copie d'acte de naissance", 2, new BigDecimal("2000.00")),
        COPIE_MARIAGE("Copie d'acte de mariage", 3, new BigDecimal("3000.00")),
        COPIE_DECES("Copie d'acte de décès", 1, new BigDecimal("1500.00")),
        EXTRAIT_NAISSANCE("Extrait naissance", 5, new BigDecimal("5000.00")),
        CERTIFICAT_CELIBAT("Certificat de célibat", 3, new BigDecimal("2500.00")),
        CERTIFICAT_COUTUME("Certificat de coutume", 5, new BigDecimal("5000.00")),
        CERTIFICAT_RESIDENCE("Certificat de Résidence", 3, new BigDecimal("1500.00")),
        AUTORISATION_CONSTRUIRE("Autorisation construire", 5, new BigDecimal("5000.00")),
        LEGALISTION_SIGNATURE("Legalisation signature", 5, new BigDecimal("5000.00"));

        private final String libelle;
        private final Integer delaiStandardJours;
        private final BigDecimal tarifFcfa;

        TypeDemande(String libelle, Integer delaiStandardJours, BigDecimal tarifFcfa) {
            this.libelle = libelle;
            this.delaiStandardJours = delaiStandardJours;
            this.tarifFcfa = tarifFcfa;
        }

        public String getLibelle() {
            return libelle;
        }

        public Integer getDelaiStandardJours() {
            return delaiStandardJours;
        }

        public BigDecimal getTarifFcfa() {
            return tarifFcfa;
        }
    }
}