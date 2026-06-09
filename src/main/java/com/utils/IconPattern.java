package com.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class IconPattern {

    public static String upperSnakeCase(String str) {
        StringBuilder bd = new StringBuilder(str);
        int strLen = str.length();

        for (int i = 0, bdIndex = 0; i < strLen; i++, bdIndex++) {
            char current = str.charAt(i);

            if (current == '-') {
                bd.setCharAt(bdIndex, '_');
            } else if (i > 0) {
                char prev = str.charAt(i - 1);
                boolean isUpperAfterCharOrDigit = Character.isUpperCase(current) && Character.isLetterOrDigit(prev);
                boolean isDigitAfterLetter = Character.isDigit(current) && Character.isLetter(prev);

                if (isUpperAfterCharOrDigit || isDigitAfterLetter) {
                    bd.insert(bdIndex, '_');
                    bdIndex++;
                }
            }
        }
        return bd.toString().toUpperCase();
    }

    /**
     * Extrait les icônes basées sur un préfixe dynamique et retire ce préfixe du
     * résultat.
     */
    public static List<String> extractIcons(String text, String prefix) {
        List<String> icons = new ArrayList<>();

        if (text == null || text.isEmpty() || prefix == null || prefix.isEmpty()) {
            return icons;
        }

        // Pattern.quote échappe les caractères spéciaux potentiels dans le préfixe
        String regex = "\\b" + Pattern.quote(prefix) + "[-a-z]+\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String fullMatch = matcher.group(); // ex: "ti ti-layout-dashboard"
            // On découpe la chaîne pour ne garder que ce qui suit le préfixe
            String withoutPrefix = fullMatch.substring(prefix.length()); // ex: "layout-dashboard"
            icons.add(withoutPrefix);
        }

        return icons;
    }

    /**
     * Lit un fichier, extrait les icônes selon un préfixe, les formate et les
     * joint.
     */
    public static String processIconsFromFile(Path filePath, String prefix) throws IOException {
        String content = Files.readString(filePath);

        List<String> extractedIcons = extractIcons(content, prefix);

        return extractedIcons.stream()
                .map(IconPattern::upperSnakeCase)
                .collect(Collectors.joining(","));
    }

    public static void main(String[] args) {
        String prefix = "ti ti-";
        // Ton chemin absolu vers le fichier JSP
        String path = "/home/wilfried/my_progs/jakarta_ee/projet-ICT318/src/main/webapp/WEB-INF/jsp/modules/dashboard/index.jsp";

        try {
            // 1. Convertir la chaîne de caractères en objet Path (Nécessite l'import
            // java.nio.file.Path)
            Path filePath = Path.of(path);

            // 2. Appeler ta méthode de traitement
            String result = processIconsFromFile(filePath, prefix);

            // 3. Afficher le résultat généré
            System.out.println("=== Icônes extraites et formatées ===");
            System.out.println(result);

        } catch (IOException e) {
            // Gérer le cas où le fichier est introuvable ou illisible
            System.err.println("Erreur : Impossible de lire le fichier à l'emplacement spécifié.");
            System.err.println("Détail de l'erreur : " + e.getMessage());
        }
    }
}