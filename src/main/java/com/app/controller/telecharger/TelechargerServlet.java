package com.app.controller.telecharger;

import com.app.util.FileManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@WebServlet(name = "TelechargerServlet", urlPatterns = { "/telecharger" })
public class TelechargerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Récupération des paramètres de l'URL (?fichier=...&dossier=...)
        String nomFichier = request.getParameter("fichier");
        String dossier = request.getParameter("dossier");

        // 2. Validation de sécurité basique
        if (nomFichier == null || nomFichier.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Le nom du fichier est requis.");
            return;
        }

        // 3. Appel du FileManager pour récupérer l'objet File
        File fichierATelecharger = FileManager.get(nomFichier, dossier);

        // 4. Vérification de l'existence physique du fichier
        if (fichierATelecharger == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Le fichier demandé est introuvable sur le serveur.");
            return;
        }

        // 5. Détection du type de fichier (MIME type)
        // Tomcat va essayer de deviner si c'est un PDF, une image PNG, un Word, etc.
        String mimeType = getServletContext().getMimeType(fichierATelecharger.getName());
        if (mimeType == null) {
            // Type générique par défaut si l'extension est inconnue
            mimeType = "application/octet-stream";
        }

        // 6. Configuration de la réponse HTTP
        response.setContentType(mimeType);
        response.setContentLengthLong(fichierATelecharger.length());

        // 💡 ASTUCE :
        // "attachment" = Force le navigateur à télécharger le fichier.
        // "inline" = Demande au navigateur d'afficher le fichier directement (pratique
        // pour les PDF).
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fichierATelecharger.getName() + "\"");

        // 7. Lecture du fichier sur le disque et écriture dans la réponse HTTP (Stream)
        try {
            Files.copy(fichierATelecharger.toPath(), response.getOutputStream());
            response.getOutputStream().flush();
        } catch (IOException e) {
            System.err.println(">>> [TelechargerServlet] Erreur lors du transfert du fichier : " + e.getMessage());
            // On ne peut plus faire de sendError() ici si le flux a déjà commencé à être
            // écrit
        }
    }
}