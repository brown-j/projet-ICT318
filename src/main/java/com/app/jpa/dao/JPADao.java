package com.app.jpa.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.app.jpa.dao.JPADao.JPADelegate;
import com.app.jpa.model.*;
import com.app.jpa.model.JPAEnum.*;
import com.app.util.HashUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.security.SecureRandom;

/**
 * Mini-ORM inspiré de Prisma pour la gestion fluide, typée et centralisée des
 * entités de l'application.
 */
public class JPADao {

    private final EntityManager em;

    // Namespaces d'accès de style Prisma (Ex: jpa.citoyen.findUnique(1L))
    public final CitoyenDelegate citoyen;
    public final OfficierEtatCivilDelegate officier;
    public final ActeEtatCivilDelegate acte;
    public final DemandeAdministrativeDelegate demande;
    public final PaiementDelegate paiement;

    public JPADao(EntityManager em) {
        this.em = em;
        this.citoyen = new CitoyenDelegate();
        this.officier = new OfficierEtatCivilDelegate();
        this.acte = new ActeEtatCivilDelegate();
        this.demande = new DemandeAdministrativeDelegate();
        this.paiement = new PaiementDelegate();
    }

    // ===================================================================================
    // ABSTRACTION PRISMA - CLASSE GENERIQUE DELEGATE
    // ===================================================================================

    /**
     * Classe parente implémentant les opérations CRUD génériques standard de
     * Prisma.
     * 
     * @param <E> Le type de l'entité JPA gérée.
     */
    public abstract class JPADelegate<E> {
        protected final Class<E> entityClass;
        protected final String entityName;

        protected JPADelegate(Class<E> entityClass) {
            this.entityClass = entityClass;
            this.entityName = entityClass.getSimpleName();
        }

        private void validateRequiedFields(E entity) {
            for (Field field : entityClass.getDeclaredFields()) {
                field.setAccessible(true); // Permet de lire les champs privés

                try {
                    Object value = field.get(entity);

                    // 1. Détection via @Column(nullable = false)
                    if (field.isAnnotationPresent(Column.class)) {
                        Column col = field.getAnnotation(Column.class);
                        if (!col.nullable() && value == null) {
                            throw new IllegalArgumentException(String.format(
                                    " Erreur Prisma-ORM : Le champ requis '%s' de l'entité %s ne peut pas être null.",
                                    field.getName(), entityName));
                        }
                    }

                    // 2. Détection via @NotNull (sécurité supplémentaire si utilisée)
                    if (field.isAnnotationPresent(NotNull.class) && value == null) {
                        throw new IllegalArgumentException(String.format(
                                " Erreur Prisma-ORM : Le champ obligatoire '%s' [%s] est manquant.",
                                field.getName(), entityName));
                    }

                } catch (IllegalAccessException e) {
                    // Ignorer les champs inaccessibles (très rare avec setAccessible)
                }
            }
        }

        /**
         * Persiste une entité brute après validation préventive.
         */
        public E create(E entity) {
            // Validation automatique par introspection
            validateRequiedFields(entity);

            em.persist(entity);
            System.out.println("→ [JPADao] " + entityName + " créé avec succès.");
            return entity;
        }

        /**
         * Récupère une entité par sa clé primaire. (Prisma: prisma.model.findUnique)
         */
        public E findUnique(Object id) {
            return em.find(entityClass, id);
        }

        /**
         * Récupère le premier enregistrement correspondant à une clause conditionnelle
         * JPQL.
         * Exemple d'usage : jpa.officier.findFirst("e.email = ?1", "jean@mairie.cm")
         */
        public E findFirst(String whereClause, Object... params) {
            try {
                var query = em.createQuery("SELECT e FROM " + entityName + " e WHERE " + whereClause, entityClass);
                for (int i = 0; i < params.length; i++) {
                    query.setParameter(i + 1, params[i]);
                }
                return query.setMaxResults(1).getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        }

        /**
         * Récupère la liste complète des entités. (Prisma: prisma.model.findMany)
         */
        public List<E> findMany() {
            return em.createQuery("SELECT e FROM " + entityName + " e", entityClass).getResultList();
        }

        /**
         * Met à jour l'état d'une entité en BDD (Merge). (Prisma: prisma.model.update)
         */
        public E update(E entity) {
            E merged = em.merge(entity);
            System.out.println("→ [JPADao] " + entityName + " mis à jour avec succès.");
            return merged;
        }

        /**
         * Supprime une entité à partir de son identifiant. (Prisma:
         * prisma.model.delete)
         */
        public void delete(Object id) {
            E entity = findUnique(id);
            if (entity != null) {
                em.remove(entity);
                System.out.println("→ [JPADao] " + entityName + " supprimé avec succès (ID: " + id + ").");
            }
        }

        /**
         * Compte le nombre total d'enregistrements. (Prisma: prisma.model.count)
         */
        public long count() {
            return em.createQuery("SELECT COUNT(e) FROM " + entityName + " e", Long.class).getSingleResult();
        }
    }

    // ===================================================================================
    // CLIENT DELEGATES - IMPLEMENTATIONS SPECIFIQUES
    // ===================================================================================

    public class CitoyenDelegate extends JPADelegate<Citoyen> {
        public CitoyenDelegate() {
            super(Citoyen.class);
        }

        // 💡 Ajout d'une instance statique de SecureRandom (plus performant de le créer
        // une seule fois)
        private static final SecureRandom SECURE_RANDOM = new SecureRandom();

        /**
         * Génère un Numéro d'Identification National (NIN) sécurisé et formaté.
         * Format : CM-YYYY-XXXXX (ex: CM-2026-48512)
         */
        private String generateNin() {
            int annee = LocalDate.now().getYear();
            // Génère un nombre aléatoire sécurisé à 5 chiffres (entre 10000 et 99999)
            int randomDigits = 10000 + SECURE_RANDOM.nextInt(90000);
            return "CM-" + annee + "-" + randomDigits;
        }

        /**
         * Méthode de commodité pour créer un Citoyen avec gestion des paramètres
         * optionnels et génération automatique du NIN.
         */
        public Citoyen create(String nom, String prenom, String nin, String adresse, Sexe sexe, LocalDate dateNaissance,
                String lieuNaissance, SituationMatrimoniale situationMatr, StatutCitoyen statut) {
            Citoyen c = new Citoyen();

            c.setNom(nom);
            c.setPrenom(prenom);

            // 💡 GESTION DU NIN : Si aucun NIN n'est fourni, on le génère de façon
            // sécurisée
            if (nin == null || nin.trim().isEmpty()) {
                c.setNin(generateNin());
            } else {
                c.setNin(nin);
            }

            c.setSexe(sexe);
            c.setDateNaissance(dateNaissance);

            c.setAdresse(adresse != null ? adresse : "Adresse non spécifiée");
            c.setLieuNaissance(lieuNaissance != null ? lieuNaissance : "Yaoundé");
            c.setSituationMatrimoniale(situationMatr != null ? situationMatr : SituationMatrimoniale.CELIBATAIRE);
            c.setStatut(statut != null ? statut : StatutCitoyen.ACTIF);

            return super.create(c); // Utilisation de la logique du parent
        }
    }

    public class OfficierEtatCivilDelegate extends JPADelegate<OfficierEtatCivil> {

        public OfficierEtatCivilDelegate() {
            super(OfficierEtatCivil.class);
        }

        private static final SecureRandom SECURE_RANDOM = new SecureRandom();

        /**
         * Génère un matricule d'agent unique et formaté.
         * Format : OEC-YYYY-XXXX (ex: OEC-2026-7841)
         */
        private String generateMatricule() {
            int annee = LocalDate.now().getYear();
            int randomDigits = 1000 + SECURE_RANDOM.nextInt(9000); // Nombre à 4 chiffres
            return "OEC-" + annee + "-" + randomDigits;
        }

        /**
         * Méthode de commodité pour créer un Officier avec hachage et validation
         * d'unicité.
         */
        public OfficierEtatCivil create(String matricule, String nom, String prenom, String tel, String titre,
                String service, String email, String motDePasseClair, LocalDate datePriseFonct, Role role) {

            if (motDePasseClair == null || motDePasseClair.trim().isEmpty()) {
                throw new IllegalArgumentException("Erreur de sécurité : Le mot de passe ne peut pas être vide.");
            }

            // Détermination du rôle cible (valeur par défaut si null)
            Role roleCible = role != null ? role : Role.OFFICIER;

            // 1. SÉCURITÉ : Empêcher la création d'un deuxième SUPER_ADMIN
            if (roleCible == Role.SUPER_ADMIN) {
                Long superAdminCount = em.createQuery(
                        "SELECT COUNT(o) FROM OfficierEtatCivil o WHERE o.role = :role", Long.class)
                        .setParameter("role", Role.SUPER_ADMIN)
                        .getSingleResult();

                if (superAdminCount > 0) {
                    throw new IllegalStateException(
                            "Erreur de sécurité critique : Un compte Super-Admin existe déjà dans le système. Impossible d'en créer un second.");
                }
            }

            // 1. CHIFFREMENT / HACHAGE du mot de passe en clair immédiat
            String motDePasseHache = HashUtil.hashPassword(motDePasseClair);

            // 2. Vérification de sécurité réglementaire sur l'unicité du mot de passe haché
            Long duplicatePasswordCount = em.createQuery(
                    "SELECT COUNT(o) FROM OfficierEtatCivil o WHERE o.motDePasse = :pwd", Long.class)
                    .setParameter("pwd", motDePasseHache)
                    .getSingleResult();

            if (duplicatePasswordCount > 0) {
                throw new IllegalStateException(
                        "Erreur de sécurité critique : Ce mot de passe est déjà utilisé par un autre officier.");
            }

            // 3. Construction de l'objet avec les informations et gestion du matricule
            OfficierEtatCivil o = new OfficierEtatCivil();

            if (matricule == null || matricule.trim().isEmpty()) {
                o.setMatricule(generateMatricule());
            } else {
                o.setMatricule(matricule.trim().toUpperCase());
            }

            o.setNom(nom);
            o.setPrenom(prenom);
            o.setTelephone(tel);
            o.setTitre(titre);
            o.setService(service);
            o.setMotDePasse(motDePasseHache); // Enregistrement du mot de passe HACHÉ

            o.setEmail(email != null ? email : (prenom.toLowerCase() + "." + nom.toLowerCase() + "@mairie.cm"));
            o.setDatePriseFonction(datePriseFonct != null ? datePriseFonct : LocalDate.of(2020, 1, 15));
            o.setRole(role != null ? role : Role.OFFICIER);

            // 4. Appel à la méthode parente pour persister
            return super.create(o);
        }
    }

    public class ActeEtatCivilDelegate extends JPADelegate<ActeEtatCivil> {
        public ActeEtatCivilDelegate() {
            super(ActeEtatCivil.class);
        }

        /**
         * Méthode de commodité pour créer un Acte avec gestion des paramètres
         * optionnels.
         */
        public ActeEtatCivil create(String numero, TypeActe type, Citoyen principal, OfficierEtatCivil officier,
                LocalDate dateEv, LocalDate dateEt, String lieu, StatutActe statut, String pdf) {
            ActeEtatCivil acte = new ActeEtatCivil();
            acte.setNumeroActe(numero);
            acte.setTypeActe(type);
            acte.setCitoyenPrincipal(principal);
            acte.setOfficierSignataire(officier);
            acte.setFichierPdf(pdf);

            acte.setDateEvenement(dateEv != null ? dateEv : LocalDate.now().minusDays(2));
            acte.setDateEtablissement(dateEt != null ? dateEt : LocalDate.now());
            acte.setLieuEvenement(lieu != null ? lieu : "Yaoundé III");
            acte.setStatut(statut != null ? statut : StatutActe.EN_COURS);

            return super.create(acte);
        }
    }

    public class DemandeAdministrativeDelegate extends JPADelegate<DemandeAdministrative> {

        public DemandeAdministrativeDelegate() {
            super(DemandeAdministrative.class);
        }

        private static final java.security.SecureRandom SECURE_RANDOM = new java.security.SecureRandom();

        // 💡 Ajout du générateur de numéro de demande
        private String generateNumeroSuivi() {
            int annee = java.time.LocalDate.now().getYear();
            int randomDigits = 10000 + SECURE_RANDOM.nextInt(90000);
            return "DM-" + annee + "-" + randomDigits;
        }

        /**
         * Méthode de création optimisée pour le jeu d'essai et les formulaires.
         */
        public DemandeAdministrative create(String numeroSuivi, TypeDemande typeDemande, Citoyen citoyenRequerant,
                LocalDateTime dateSoumission, StatutDemande statut,
                PrioriteDemande priorite, String documentFinal) {

            DemandeAdministrative d = new DemandeAdministrative();

            // 💡 Utilisation du générateur si null
            if (numeroSuivi == null || numeroSuivi.trim().isEmpty()) {
                d.setNumeroSuivi(generateNumeroSuivi());
            } else {
                d.setNumeroSuivi(numeroSuivi.trim().toUpperCase());
            }

            d.setTypeDemande(typeDemande);
            d.setCitoyenRequerant(citoyenRequerant);
            d.setDateSoumission(dateSoumission);
            d.setStatut(statut != null ? statut : StatutDemande.SOUMISE);
            d.setPriorite(priorite != null ? priorite : PrioriteDemande.NORMALE);
            d.setDocumentFinal(documentFinal);
            d.setDescription("Demande pour : " + (typeDemande != null ? typeDemande.name() : "Non spécifié"));

            return super.create(d);
        }

        /**
         * Fait passer une demande à l'état EN_COURS (ex: après paiement).
         */
        public void marquerEnCours(DemandeAdministrative demande) {
            if (demande.getStatut() == StatutDemande.SOUMISE) {
                demande.setStatut(StatutDemande.EN_COURS);
                update(demande);
                System.out.println("→ [Action] Demande " + demande.getNumeroSuivi() + " marquée EN_COURS.");
            } else {
                throw new IllegalStateException("Seule une demande SOUMISE peut passer EN COURS.");
            }
        }

        /**
         * Valide une demande.
         */
        public void valider(DemandeAdministrative demande) {
            if (demande.getStatut() == StatutDemande.EN_COURS || demande.getStatut() == StatutDemande.SOUMISE) {
                demande.setStatut(StatutDemande.VALIDEE);
                update(demande);
                System.out.println("→ [Action] Demande " + demande.getNumeroSuivi() + " VALIDEE.");
            } else {
                throw new IllegalStateException("Statut actuel invalide pour une validation.");
            }
        }

        /**
         * Rejette une demande.
         */
        public void rejeter(DemandeAdministrative demande) {
            if (demande.getStatut() != StatutDemande.CLOTUREE && demande.getStatut() != StatutDemande.VALIDEE) {
                demande.setStatut(StatutDemande.REJETEE);
                update(demande);
                System.out.println("→ [Action] Demande " + demande.getNumeroSuivi() + " REJETEE.");
            } else {
                throw new IllegalStateException("Impossible de rejeter une demande déjà validée ou clôturée.");
            }
        }

        /**
         * Clôture une demande et y associe le document final généré.
         */
        public void cloturer(DemandeAdministrative demande, String documentFinal) {
            if (demande.getStatut() == StatutDemande.VALIDEE) {
                demande.setStatut(StatutDemande.CLOTUREE);
                demande.setDocumentFinal(documentFinal);
                update(demande);
                System.out.println("→ [Action] Demande " + demande.getNumeroSuivi() + " CLOTUREE avec succès.");
            } else {
                throw new IllegalStateException("La demande doit d'abord être VALIDEE avant d'être clôturée.");
            }
        }
    }

    public class PaiementDelegate extends JPADelegate<Paiement> {
        public PaiementDelegate() {
            super(Paiement.class);
        }

        private static final java.security.SecureRandom SECURE_RANDOM = new java.security.SecureRandom();

        /**
         * Génère une référence de reçu unique et sécurisée si elle n'est pas fournie.
         * Format : REC-YYYY-XXXXX (ex: REC-2026-74125)
         */
        private String generateReferenceRecu() {
            int annee = LocalDate.now().getYear();
            int randomDigits = 10000 + SECURE_RANDOM.nextInt(90000); // 5 chiffres aléatoires
            return "REC-" + annee + "-" + randomDigits;
        }

        /**
         * Méthode de commodité de style Prisma pour créer un Paiement
         * avec gestion des valeurs par défaut et validation.
         */
        public Paiement create(String referenceRecu, DemandeAdministrative demande, java.math.BigDecimal montant,
                ModePaiement modePaiement, OfficierEtatCivil officierCaissier, LocalDateTime datePaiement) {

            Paiement p = new Paiement();

            // Gestion de la référence unique obligatoire
            if (referenceRecu == null || referenceRecu.trim().isEmpty()) {
                p.setReferenceRecu(generateReferenceRecu());
            } else {
                p.setReferenceRecu(referenceRecu.trim().toUpperCase());
            }

            p.setDemande(demande); // Peut être null (ex: Recette Directe / Achat direct sans demande)

            // L'officier caissier est obligatoire (@JoinColumn(nullable = false))
            if (officierCaissier == null) {
                throw new IllegalArgumentException(
                        "Erreur Prisma-ORM : Un officier caissier valide doit être assigné au paiement.");
            }
            p.setOfficierCaissier(officierCaissier);

            // Valeurs financières et enums avec fallbacks sécurisés
            p.setMontant(montant != null ? montant : java.math.BigDecimal.ZERO);
            p.setModePaiement(modePaiement != null ? modePaiement : ModePaiement.ESPECES);
            p.setDatePaiement(datePaiement != null ? datePaiement : LocalDateTime.now());

            // Exécution de la persistance et de la validation par introspection du parent
            return super.create(p);
        }
    }
}