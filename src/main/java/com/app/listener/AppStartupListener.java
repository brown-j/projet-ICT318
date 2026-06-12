package com.app.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.persistence.EntityManager;

import com.app.jpa.config.JPAConfig;
import com.app.jpa.dao.JPADao;
import com.app.jpa.model.JPAEnum.Role;
import java.time.LocalDate;

@WebListener
public class AppStartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("🚀 [STARTUP] Initialisation du système Mairie...");

        EntityManager em = null;
        try {
            em = JPAConfig.getEntityManager();
            JPADao jpa = new JPADao(em); // Initialise ton mini-Prisma ORM

            // 1. Vérification de l'existence du Super-Admin via l'abstraction Prisma-like
            // Utilise findFirst() hérité pour faire une recherche simple et lisible
            boolean hasSuperAdmin = jpa.officier.findFirst("e.role = ?1", Role.SUPER_ADMIN) != null;

            // 2. Création automatique s'il est absent
            if (!hasSuperAdmin) {
                System.out.println("👤 [STARTUP] Aucun Super-Admin trouvé. Génération du compte racine...");

                em.getTransaction().begin();

                // Utilisation directe de la méthode de commodité de ton Delegate
                // (Le mot de passe sera haché automatiquement grâce à ton code existant)
                jpa.officier.create(
                        "SU-ADMIN-01", // matricule
                        "SYSTEM", // nom
                        "SuperAdmin", // prenom
                        "+237600000000", // telephone
                        "Administrateur Général", // titre
                        "Service Informatique", // service
                        "admin@mairie.cm", // email
                        "Admin@1234", // mot de passe en clair (sera chiffré par le delegate !)
                        LocalDate.now(), // date de prise de fonction
                        Role.SUPER_ADMIN // Le rôle racine
                );

                em.getTransaction().commit();
                System.out.println("✅ [STARTUP] Compte Super-Admin initialisé : admin@mairie.cm / Admin@1234");
            } else {
                System.out.println("ℹ️ [STARTUP] Le Super-Admin est déjà configuré en base de données.");
            }

        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("❌ [STARTUP] Erreur critique lors de l'amorçage : " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("🛑 [SHUTDOWN] Arrêt de l'application Mairie.");
    }
}