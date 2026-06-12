package com.app.jpa.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class JPAConfig {
    private static EntityManagerFactory emf;

    static {
        try {
            // 1. Chargement du .env
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

            // 2. Récupération des variables avec valeurs de repli (Fallback) si null
            String url = dotenv.get("DB_URL") != null ? dotenv.get("DB_URL") : System.getenv("DB_URL");
            String user = dotenv.get("DB_USER") != null ? dotenv.get("DB_USER") : System.getenv("DB_USER");
            String password = dotenv.get("DB_PASSWORD") != null ? dotenv.get("DB_PASSWORD")
                    : System.getenv("DB_PASSWORD");

            // 💡 CONFIGURATION DE SECOURS POUR TOMCAT LOCAL
            // Puisque Tomcat ne voit pas ton .env, on écrit tes vrais identifiants ici
            if (url == null) {
                System.out
                        .println("[JPAConfig] ⚠️ Fichier .env non détecté. Utilisation des identifiants de Wilfried.");
                url = "jdbc:mysql://localhost:3306/gestion_mairie?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
                user = "wilfried";
                password = "gestion_mairie_pwd";
            }

            // Petit log d'assurance pour voir ce qui est injecté dans Hibernate
            System.out.println("[JPAConfig] Connexion à la BDD : " + url);
            System.out.println("[JPAConfig] Utilisateur BDD : " + user);

            // 3. Injection dans les propriétés d'Hibernate
            Map<String, String> properties = new HashMap<>();
            properties.put("jakarta.persistence.jdbc.url", url);
            properties.put("jakarta.persistence.jdbc.user", user);
            properties.put("jakarta.persistence.jdbc.password", password);

            // 4. Initialisation
            emf = Persistence.createEntityManagerFactory("mairiePU", properties);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'initialisation de la base de données.", e);
        }
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
