package com.app.util;

import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class FileManager {
    public static final String TEMPLATE_SUB = "templates";
    public static final String SIGNATURE_SUB = "signatures";
    public static final String ACTE_SUB = "actes";

    // 1. Définition du chemin absolu indépendant de l'OS
    private static final String BASE_UPLOAD_DIR = System.getProperty("catalina.base") + File.separator
            + "mairie_uploads";

    /**
     * Bloc d'initialisation statique.
     * S'exécute une seule fois au chargement de la classe pour s'assurer que le
     * dossier racine existe.
     */
    static {
        try {
            Path baseDirPath = Paths.get(BASE_UPLOAD_DIR);
            if (!Files.exists(baseDirPath)) {
                Files.createDirectories(baseDirPath);
                System.out.println(">>> [FileManager] Dossier d'upload créé : " + baseDirPath.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println(">>> [FileManager] ERREUR CRITIQUE : Impossible de créer le dossier d'upload.");
            e.printStackTrace();
        }
    }

    /**
     * Sauvegarde un fichier provenant d'un formulaire multipart.
     * * @param filePart Le fichier extrait de la requête
     * (request.getPart("nomInput"))
     * 
     * @param sousDossier Optionnel (ex: "demandes", "templates"). Peut être null.
     * @return Le nouveau nom unique du fichier généré (UUID), ou null si échec.
     */
    public static String upload(Part filePart, String sousDossier) throws IOException {
        if (filePart == null || filePart.getSize() == 0 || filePart.getSubmittedFileName() == null) {
            return null;
        }

        // 1. Préparation du répertoire cible
        Path targetDir = Paths.get(BASE_UPLOAD_DIR, sousDossier != null ? sousDossier : "");
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        // 2. Extraction de l'extension du fichier d'origine
        String originalFileName = filePart.getSubmittedFileName();
        String extension = "";
        int dotIndex = originalFileName.lastIndexOf(".");
        if (dotIndex > 0) {
            extension = originalFileName.substring(dotIndex);
        }

        // 3. Génération d'un nom de fichier unique (UUID) pour éviter les écrasements
        String uniqueFileName = UUID.randomUUID().toString() + extension;
        Path filePath = targetDir.resolve(uniqueFileName);

        // 4. Écriture physique sur le disque dur
        try (InputStream input = filePart.getInputStream()) {
            Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        return uniqueFileName;
    }

    /**
     * ✨ NOUVEAU : Remplace un ancien fichier par un nouveau.
     * Supprime physiquement l'ancien du disque si présent, puis charge le nouveau.
     * * @param ancienNomFichier Le nom (UUID) de l'ancien fichier stocké en BDD
     * (peut être null)
     * 
     * @param nouveauFilePart Le nouveau fichier brut issu de request.getPart()
     * @param sousDossier     Le dossier concerné (ex: "templates")
     * @return Le nouvel UUID du fichier enregistré, ou l'ancien nom si aucun
     *         nouveau fichier n'a été soumis.
     */
    public static String replace(String ancienNomFichier, Part nouveauFilePart, String sousDossier) throws IOException {
        // Si aucun nouveau fichier n'est téléversé, on ne change rien et on garde
        // l'existant
        if (nouveauFilePart == null || nouveauFilePart.getSize() == 0) {
            return ancienNomFichier;
        }

        // 1. Suppression physique de l'ancien fichier s'il existait
        if (ancienNomFichier != null && !ancienNomFichier.trim().isEmpty()) {
            delete(ancienNomFichier, sousDossier);
        }

        // 2. Upload du nouveau fichier et retour de son nouvel UUID
        return upload(nouveauFilePart, sousDossier);
    }

    /**
     * Récupère le chemin physique complet d'un fichier existant.
     */
    public static File get(String nomFichier, String sousDossier) {
        if (nomFichier == null || nomFichier.trim().isEmpty()) {
            return null;
        }

        Path targetDir = Paths.get(BASE_UPLOAD_DIR, sousDossier != null ? sousDossier : "");
        Path filePath = targetDir.resolve(nomFichier);
        File file = filePath.toFile();

        return (file.exists() && file.isFile()) ? file : null;
    }

    /**
     * Supprime un fichier physiquement du disque dur.
     */
    public static boolean delete(String nomFichier, String sousDossier) {
        if (nomFichier == null || nomFichier.trim().isEmpty()) {
            return false;
        }

        Path targetDir = Paths.get(BASE_UPLOAD_DIR, sousDossier != null ? sousDossier : "");
        Path filePath = targetDir.resolve(nomFichier);

        try {
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println(">>> [FileManager] Impossible de supprimer le fichier : " + filePath.toString());
            e.printStackTrace();
            return false;
        }
    }
}