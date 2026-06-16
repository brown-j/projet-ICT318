package com.app.jpa.model;

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

    public enum CategorieActe {
        ACTE("acte"),
        COPIE("Copie"),
        EXTRAIT("Extrait"),
        CERTIFICAT("Certificat"),
        AUTORISATION("Autorisation"),
        LEGALISTION("Legalisation");

        private final String libelle;

        CategorieActe(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }

        public String getShortName() {
            return name().substring(0, 3);
        }
    }
}