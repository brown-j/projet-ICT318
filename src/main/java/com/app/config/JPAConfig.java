package com.app.config;

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
            // 1. On charge le fichier .env (s'il existe, sinon il prend les variables
            // système du serveur)
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

            // 2. On récupère les variables (soit du .env local, soit du serveur en ligne)
            String url = dotenv.get("DB_URL") != null ? dotenv.get("DB_URL") : System.getenv("DB_URL");
            String user = dotenv.get("DB_USER") != null ? dotenv.get("DB_USER") : System.getenv("DB_USER");
            String password = dotenv.get("DB_PASSWORD") != null ? dotenv.get("DB_PASSWORD")
                    : System.getenv("DB_PASSWORD");

            // 3. On crée une carte de propriétés dynamiques à injecter dans Hibernate
            Map<String, String> properties = new HashMap<>();
            properties.put("jakarta.persistence.jdbc.url", url);
            properties.put("jakarta.persistence.jdbc.user", user);
            properties.put("jakarta.persistence.jdbc.password", password);

            // 4. On initialise l'EntityManagerFactory avec le nom de notre unité défini
            // dans persistence.xml
            emf = Persistence.createEntityManagerFactory("mairiePU", properties);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'initialisation de la base de données.");
        }
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
