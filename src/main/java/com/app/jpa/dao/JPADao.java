package com.app.jpa.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.app.jpa.model.*;
import com.app.jpa.model.JPAEnum.*;
import com.app.util.AuditContext;
import com.app.util.FileManager;
import com.app.util.HashUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;

import java.io.File;
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
    public final JournalAuditDelegate audit;
    public final TypeActeDelegate typeActe;

    public JPADao(EntityManager em) {
        this.em = em;
        this.citoyen = new CitoyenDelegate();
        this.officier = new OfficierEtatCivilDelegate();
        this.acte = new ActeEtatCivilDelegate();
        this.demande = new DemandeAdministrativeDelegate();
        this.paiement = new PaiementDelegate();
        this.audit = new JournalAuditDelegate();
        this.typeActe = new TypeActeDelegate();
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

        /**
         * 🌟 MOTEUR D'AUDIT AUTOMATIQUE
         * Intercepte le contexte et écrit dans journal_audit de manière sécurisée.
         */
        protected void saveAudit(String actionType, E entity) {
            OfficierEtatCivil acteur = AuditContext.getOfficier();

            // On n'exécute l'audit que si un utilisateur est authentifié (évite les crashs
            // hors-session)
            if (acteur != null && !(entity instanceof JournalAudit)) {
                try {
                    JournalAudit audit = new JournalAudit();
                    audit.setOfficier(acteur);
                    audit.setAction(actionType);
                    audit.setTableAffectee(entityName);
                    audit.setAdresseIp(AuditContext.getIp());
                    audit.setDonneesJson(convertEntityToJsonBytes(entity));

                    // Persiste directement à l'aide de l'EntityManager commun
                    em.persist(audit);
                } catch (Exception e) {
                    System.err.println("⚠️ Échec de l'écriture de l'audit : " + e.getMessage());
                }
            }
        }

        /**
         * Convertisseur Réflexif en JSON pour l'audit.
         * Évite les dépendances tierces et ignore les objets complexes / Lazy pour ne
         * pas planter.
         */
        private String convertEntityToJsonBytes(E entity) {
            Map<String, String> jsonMap = new LinkedHashMap<>();
            for (Field field : entityClass.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Object val = field.get(entity);
                    if (val != null) {
                        // On n'enregistre que les types primitifs, chaînes, enums et dates pour l'audit
                        if (val instanceof Number || val instanceof String || val instanceof Boolean
                                || val instanceof Enum || val instanceof LocalDate || val instanceof LocalDateTime) {
                            jsonMap.put(field.getName(), val.toString());
                        } else {
                            // Pour les jointures (@ManyToOne), on se contente d'écrire qu'un lien existe
                            jsonMap.put(field.getName(), "[Relation/Objet Complex]");
                        }
                    }
                } catch (Exception e) {
                    // Ignore les champs illisibles
                }
            }
            // Reconstruction rapide au format JSON brut
            StringBuilder sb = new StringBuilder("{");
            jsonMap.forEach(
                    (k, v) -> sb.append("\"").append(k).append("\":\"").append(v.replace("\"", "\\\"")).append("\","));
            if (sb.length() > 1)
                sb.setLength(sb.length() - 1); // Retire la dernière virgule
            sb.append("}");
            return sb.toString();
        }

        private void validateRequiredFields(E entity) {
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
            validateRequiredFields(entity);

            em.persist(entity);
            System.out.println("→ [JPADao] " + entityName + " créé avec succès.");

            // ⚡ PATCH : Déclenchement de l'audit pour la création
            saveAudit("CREATE", entity);

            return entity;
        }

        /**
         * Met à jour l'état d'une entité en BDD (Merge). (Prisma: prisma.model.update)
         */
        public E update(E entity) {
            E merged = em.merge(entity);
            System.out.println("→ [JPADao] " + entityName + " mis à jour avec succès.");

            // ⚡ PATCH : Déclenchement de l'audit pour la modification
            // On passe "merged" car c'est l'entité fraîchement rattachée au contexte JPA
            saveAudit("UPDATE", merged);

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

                // ⚡ PATCH : Déclenchement de l'audit pour la suppression
                saveAudit("DELETE", entity);
            }
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
         * Compte le nombre total d'enregistrements. (Prisma: prisma.model.count)
         */
        public long count() {
            return em.createQuery("SELECT COUNT(e) FROM " + entityName + " e", Long.class).getSingleResult();
        }

        /**
         * Récupère les "N" derniers enregistrements triés par un champ de date.
         * Ex: jpa.demande.findRecent("dateSoumission", 5)
         */
        public List<E> findRecent(String dateFieldName, int limit) {
            return em.createQuery(
                    "SELECT e FROM " + entityName + " e ORDER BY e." + dateFieldName + " DESC", entityClass)
                    .setMaxResults(limit)
                    .getResultList();
        }

        /**
         * Compte de manière générique avec une clause WHERE.
         * Ex: jpa.demande.countWithCondition("e.statut = ?1", StatutDemande.EN_COURS)
         */
        public long countWithCondition(String whereClause, Object... params) {
            var query = em.createQuery(
                    "SELECT COUNT(e) FROM " + entityName + " e WHERE " + whereClause, Long.class);
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
            return query.getSingleResult();
        }
    }

    // ===================================================================================
    // CLIENT DELEGATES - IMPLEMENTATIONS SPECIFIQUES
    // ===================================================================================

    // Nouvelle implémentation spécifique pour requêter le journal au besoin
    public class JournalAuditDelegate extends JPADelegate<JournalAudit> {
        public JournalAuditDelegate() {
            super(JournalAudit.class);
        }

        /**
         * Récupère les X derniers audits en chargeant immédiatement l'officier lié
         * (Anti-N+1)
         */
        public List<JournalAudit> findRecentWithOfficier(int limit) {
            return em.createQuery(
                    "SELECT a FROM JournalAudit a LEFT JOIN FETCH a.officier ORDER BY a.dateAction DESC",
                    JournalAudit.class)
                    .setMaxResults(limit)
                    .getResultList();
        }
    }

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
            String numeroFinal;
            if (numero == null || numero.trim().isEmpty()) {
                numeroFinal = generateNumeroActe(em, type.getCategorieParent().getShortName());
            } else {
                numeroFinal = numero.trim();
            }
            acte.setNumeroActe(numeroFinal);
            acte.setTypeActe(type);
            acte.setCitoyenPrincipal(principal);
            acte.setOfficierSignataire(officier);
            acte.setFichierPdf(pdf);

            acte.setDateEvenement(dateEv != null ? dateEv : LocalDate.of(0, 1, 1));
            acte.setDateEtablissement(dateEt != null ? dateEt : LocalDate.of(0, 1, 1));
            acte.setLieuEvenement(lieu != null ? lieu : "");
            acte.setStatut(statut != null ? statut : StatutActe.EN_COURS);

            return super.create(acte);
        }

        private String generateNumeroActe(EntityManager em, String prefix) {
            int anneeCourante = LocalDate.now().getYear(); // Est configuré sur 2026
            java.util.Random random = new java.util.Random();
            String numeroGenere;
            boolean existe;

            do {
                int uniqueId = 10000 + random.nextInt(90000); // Nombre à 5 chiffres
                numeroGenere = prefix + "-" + anneeCourante + "-" + uniqueId;

                Long count = em
                        .createQuery("SELECT COUNT(a) FROM ActeEtatCivil a WHERE a.numeroActe = :num", Long.class)
                        .setParameter("num", numeroGenere)
                        .getSingleResult();
                existe = count > 0;
            } while (existe);

            return numeroGenere;
        }

        @Override
        public ActeEtatCivil update(ActeEtatCivil entity) {
            // empecher la mise à jours du num
            String num = acte.findUnique(entity.getId()).getNumeroActe();
            entity.setNumeroActe(num);
            return super.update(entity);
        }

        /**
         * Récupère la répartition mensuelle d'un type d'acte pour une année donnée
         * (Graphique).
         * Renvoie une liste de 12 entiers (Janvier à Décembre).
         */
        public List<Integer> getEvolutionMensuelle(TypeActe type, int annee) {
            // EXTRACT() est une fonction standard JPQL très performante
            String jpql = "SELECT EXTRACT(MONTH FROM a.dateEtablissement), COUNT(a) FROM ActeEtatCivil a " +
                    "WHERE a.typeActe = :type AND EXTRACT(YEAR FROM a.dateEtablissement) = :annee " +
                    "GROUP BY EXTRACT(MONTH FROM a.dateEtablissement)";

            List<Object[]> results = em.createQuery(jpql, Object[].class)
                    .setParameter("type", type)
                    .setParameter("annee", annee)
                    .getResultList();

            // Initialiser un tableau de 12 mois à zéro
            Integer[] mois = new Integer[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            for (Object[] row : results) {
                int monthIndex = ((Number) row[0]).intValue() - 1; // 1-12 devient 0-11
                mois[monthIndex] = ((Number) row[1]).intValue();
            }

            return java.util.Arrays.asList(mois);
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
        public DemandeAdministrative create(String numeroSuivi, TypeActe type, Citoyen citoyenRequerant,
                LocalDateTime dateSoumission, StatutDemande statut,
                PrioriteDemande priorite, String documentFinal) {

            DemandeAdministrative d = new DemandeAdministrative();

            // type requis throw
            // Sécurité : le type d'acte (catalogue) est strictement requis
            if (type == null) {
                throw new IllegalArgumentException(
                        "Erreur: Le type d'acte est requis pour créer une demande.");
            }

            // 💡 Utilisation du générateur si null
            if (numeroSuivi == null || numeroSuivi.trim().isEmpty()) {
                d.setNumeroSuivi(generateNumeroSuivi());
            } else {
                d.setNumeroSuivi(numeroSuivi.trim().toUpperCase());
            }

            d.setTypeActe(type);
            d.setCitoyenRequerant(citoyenRequerant);
            d.setDateSoumission(dateSoumission);
            d.setStatut(statut != null ? statut : StatutDemande.SOUMISE);
            d.setPriorite(priorite != null ? priorite : PrioriteDemande.NORMALE);
            d.setDocumentFinal(documentFinal);
            d.setDescription("Demande pour : " + type.getDescription());

            return super.create(d);
        }

        /**
         * Compte les demandes correspondant à une liste de statuts (IN clause).
         */
        public long countByStatuts(List<StatutDemande> statuts) {
            return em.createQuery(
                    "SELECT COUNT(d) FROM DemandeAdministrative d WHERE d.statut IN :statuts", Long.class)
                    .setParameter("statuts", statuts)
                    .getSingleResult();
        }

        /**
         * Fait passer une demande à l'état EN_COURS (ex: après paiement).
         */
        public void marquerEnCours(DemandeAdministrative d, OfficierEtatCivil o) {
            if (d.getStatut() == StatutDemande.SOUMISE) {
                d.setStatut(StatutDemande.EN_COURS);
                // creation de l'acte correspondant
                ActeEtatCivil a = acte.create(
                        null,
                        d.getTypeActe(),
                        d.getCitoyenRequerant(),
                        o,
                        null,
                        null,
                        null,
                        StatutActe.EN_COURS,
                        null);
                d.setDocumentFinal(a.getNumeroActe()); // ne doit plus etre modifier
                update(d);
                System.out.println("→ [Action] Demande " + d.getNumeroSuivi() + " marquée EN_COURS.");
            } else {
                throw new IllegalStateException("Seule une demande SOUMISE peut passer EN COURS.");
            }
        }

        /**
         * Valide une demande.
         */
        public void valider(DemandeAdministrative demande) {
            if (demande.getStatut() == StatutDemande.EN_COURS || demande.getStatut() == StatutDemande.SOUMISE) {
                // doit posseder un acte final
                ActeEtatCivil acteRef = acte.findFirst("e.numeroActe = ?1", demande.getDocumentFinal());

                if (acteRef == null) {
                    throw new IllegalStateException("ACTE FINAL introuvable: Absent en BDD");
                }

                File f = FileManager.get(acteRef.getFichierPdf(), FileManager.ACTE_SUB);

                if (f == null)
                    throw new IllegalStateException("Fichier de l' ACTE FINAL introuvable");

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
                ActeEtatCivil acteRef = acte.findFirst("e.documentFinal = ?1", demande.getDocumentFinal());
                if (acteRef != null) {
                    acteRef.setStatut(StatutActe.ANNULE); // ne doit plus etre modifier
                    acte.update(acteRef);
                }
                System.out.println("→ [Action] Demande " + demande.getNumeroSuivi() + " REJETEE.");
            } else {
                throw new IllegalStateException("Impossible de rejeter une demande déjà validée ou clôturée.");
            }
        }

        /**
         * Clôture une demande, y associe le document final et facture la prestation en
         * base.
         * 
         * @param demande      La demande à clôturer.
         * @param caissier     L'officier qui valide l'encaissement (Obligatoire).
         * @param modePaiement Le moyen de paiement utilisé (si null, passera en
         *                     ESPECES).
         */
        public void cloturer(DemandeAdministrative demande, OfficierEtatCivil caissier,
                ModePaiement modePaiement) {
            if (demande.getStatut() == StatutDemande.VALIDEE) {

                ActeEtatCivil acteRef = acte.findFirst("e.numeroActe = ?1", demande.getDocumentFinal());

                if (acteRef == null) {
                    throw new IllegalStateException("ACTE FINAL introuvable: Absent en BDD");
                }

                File f = FileManager.get(acteRef.getFichierPdf(), FileManager.ACTE_SUB);

                if (f == null)
                    throw new IllegalStateException("Fichier de l' ACTE FINAL introuvable");

                // 1. Mise à jour de l'état de la demande
                demande.setStatut(StatutDemande.CLOTUREE);
                update(demande);

                // 2. Récupération automatique du tarif via le catalogue TypeActe en mémoire
                java.math.BigDecimal montant = (demande.getTypeActe() != null)
                        ? java.math.BigDecimal.valueOf(demande.getTypeActe().getTarifFCFA())
                        : java.math.BigDecimal.ZERO;

                // 3. Création et persistance du paiement associé via le delegate existant
                paiement.create(
                        null, // La référence de reçu sera autogénérée
                        demande,
                        montant,
                        modePaiement != null ? modePaiement : ModePaiement.ESPECES,
                        caissier,
                        LocalDateTime.now());

                System.out.println("→ [Action] Demande " + demande.getNumeroSuivi() + " CLOTUREE et Paiement de "
                        + montant + " FCFA enregistré avec succès.");
            } else {
                throw new IllegalStateException(
                        "La demande doit d'abord être VALIDEE avant d'être clôturée et facturée.");
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

        /**
         * Calcule le total encaissé sur une période donnée (Mois, Année, Jour).
         */
        public java.math.BigDecimal sumMontantByPeriode(LocalDateTime debut, LocalDateTime fin) {
            java.math.BigDecimal total = em.createQuery(
                    "SELECT SUM(p.montant) FROM Paiement p WHERE p.datePaiement BETWEEN :debut AND :fin",
                    java.math.BigDecimal.class)
                    .setParameter("debut", debut)
                    .setParameter("fin", fin)
                    .getSingleResult();

            return total != null ? total : java.math.BigDecimal.ZERO;
        }

    }

    // ===================================================================================
    // DELEGATE DE CONFIGURATION : CATALOGUE CACHÉ EN MÉMOIRE
    // ===================================================================================
    public class TypeActeDelegate extends JPADelegate<TypeActe> {

        // 💡 Cache thread-safe partagé par toutes les instances de requêtes
        private static final java.util.concurrent.ConcurrentHashMap<String, TypeActe> cache = new java.util.concurrent.ConcurrentHashMap<>();

        public TypeActeDelegate() {
            super(TypeActe.class);
            // Si le cache est vide lors d'une requête, on force son chargement initial
            // depuis la BDD
            if (cache.isEmpty()) {
                refreshCache();
            }
        }

        /**
         * Recharge complètement les données depuis la base de données vers la mémoire.
         */
        public synchronized void refreshCache() {
            cache.clear();
            List<TypeActe> tousLesTypes = findMany(); // Appelle le findMany() générique
            for (TypeActe t : tousLesTypes) {
                cache.put(t.getCode().toUpperCase(), t);
            }
            System.out.println("⚡ [Prisma Cache] Catalogue synchronisé en mémoire (" + cache.size() + " éléments).");
        }

        /**
         * 🌟 Style Prisma : Récupère l'élément instantanément depuis la mémoire.
         * Évite totalement le findUnique en BDD !
         */
        public TypeActe findInCache(String code) {
            if (code == null)
                return null;
            return cache.get(code.trim().toUpperCase());
        }

        /**
         * 🌟 Style Prisma : Retourne la Map complète pour les composants Select de
         * l'UI.
         */
        public java.util.Map<String, TypeActe> getCache() {
            return cache;
        }

        /**
         * Sauvegarde ou met à jour en base de données ET synchronise immédiatement le
         * cache.
         */
        public TypeActe saveOrUpdate(String code, String libelle, int tarif, String templatePath,
                CategorieActe categorie, String description) {

            String codeCle = code.trim().toUpperCase();

            // 💡 On cherche d'abord dans le cache au lieu de faire un findUnique BDD
            TypeActe existant = findInCache(codeCle);
            TypeActe resultat;

            if (existant == null) {
                // Mode CREATION
                TypeActe nouveau = new TypeActe();
                nouveau.setCode(codeCle);
                nouveau.setLibelle(libelle);
                nouveau.setTarifFCFA(tarif);
                nouveau.setTemplatePath(templatePath);
                nouveau.setCategorieParent(categorie);
                nouveau.setDescription(description);

                resultat = create(nouveau);
            } else {
                // Mode MODIFICATION (On doit rattacher l'objet au contexte JPA actif via merge)
                existant.setLibelle(libelle);
                existant.setTarifFCFA(tarif);
                existant.setTemplatePath(templatePath);
                existant.setCategorieParent(categorie);
                existant.setDescription(description);

                resultat = update(existant);
            }

            // ⚡ MISE À JOUR DU CACHE EN MÉMOIRE IMMÉDIATE
            cache.put(codeCle, resultat);

            return resultat;
        }
    }
}