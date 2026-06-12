package com.app.util;

import org.mindrot.jbcrypt.BCrypt;

public class HashUtil {

    /**
     * Hache un mot de passe en clair avec un "salt" généré aléatoirement.
     */
    public static String hashPassword(String plainTextPassword) {
        // Le "salt" de 12 est un excellent compromis entre sécurité et performance
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
    }

    /**
     * Vérifie si un mot de passe en clair correspond au mot de passe haché en BDD.
     */
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Se déclenche si le hash en BDD n'est pas au format BCrypt valide
            return false;
        }
    }
}