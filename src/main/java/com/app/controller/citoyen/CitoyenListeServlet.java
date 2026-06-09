package com.app.controller.citoyen;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson; // Import de Gson

import com.app.model.jpa.Citoyen;
import com.app.model.jpa.enums.SituationMatrimoniale;
import com.app.model.jpa.enums.StatutCitoyen;
import com.app.model.jpa.enums.Sexe;
import com.app.model.viewmodel.CitoyenRow;

@WebServlet("/citoyen/liste")
public class CitoyenListeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Récupération des paramètres (si on veut garder un pré-filtrage serveur
        // optionnel)
        String search = request.getParameter("search");
        String statutParam = request.getParameter("statut");
        String quartierParam = request.getParameter("quartier");

        // 2. Récupération des données mockées
        List<Citoyen> registreCommunal = gérerRegistreMock();

        // 3. Transformation en ViewModels (on pourrait envoyer toute la liste et
        // laisser JS tout filtrer,
        // mais on garde le Stream au cas où tu as un premier filtre serveur)
        List<CitoyenRow> citoyensRows = registreCommunal.stream()
                .map(CitoyenRow::new)
                .collect(Collectors.toList());

        // 4. Sérialisation propre de la liste en JSON avec Gson
        Gson gson = new Gson();
        String citoyensJson = gson.toJson(citoyensRows);

        // 5. Envoi du JSON et des variables de contexte à la JSP
        request.setAttribute("citoyensJson", citoyensJson);
        request.setAttribute("totalCount", citoyensRows.size());

        // 6. Routage vers le layout maître
        request.setAttribute("view", "/WEB-INF/jsp/modules/citoyen/liste.jsp");
        request.getRequestDispatcher("/WEB-INF/jsp/layouts/base-layout.jsp").forward(request, response);
    }

    /**
     * Génère un jeu d'essai de 25 citoyens camerounais pour valider l'UI.
     */
    private List<Citoyen> gérerRegistreMock() {
        return Arrays.asList(
                // --- Bastos ---
                createMockCitoyen(1L, "CM-2025-10831", "Mbarga", "Jean-Paul", LocalDate.of(1985, 11, 7), "Bastos",
                        SituationMatrimoniale.CELIBATAIRE, StatutCitoyen.ACTIF),
                createMockCitoyen(2L, "CM-2026-20411", "Atangana", "Dieudonné", LocalDate.of(1963, 4, 12), "Bastos",
                        SituationMatrimoniale.MARIE, StatutCitoyen.ACTIF),
                createMockCitoyen(3L, "CM-2024-11002", "Ndi", "Therese", LocalDate.of(1975, 9, 23), "Bastos",
                        SituationMatrimoniale.MARIE, StatutCitoyen.ACTIF),
                createMockCitoyen(4L, "CM-2023-08541", "Etoa", "Marc", LocalDate.of(1992, 1, 30), "Bastos",
                        SituationMatrimoniale.DIVORCE, StatutCitoyen.ARCHIVE),
                createMockCitoyen(5L, "CM-2026-30221", "Bella", "Chantal", LocalDate.of(1988, 5, 18), "Bastos",
                        SituationMatrimoniale.CELIBATAIRE, StatutCitoyen.ACTIF),
                createMockCitoyen(6L, "CM-2022-07412", "Ona", "Samuel", LocalDate.of(1940, 2, 14), "Bastos",
                        SituationMatrimoniale.VEUF, StatutCitoyen.DECEDE),

                // --- Biyem-Assi ---
                createMockCitoyen(7L, "CM-2025-10847", "Abena Zoa", "Marie", LocalDate.of(1990, 3, 14), "Biyem-Assi",
                        SituationMatrimoniale.MARIE, StatutCitoyen.ACTIF),
                createMockCitoyen(8L, "CM-2025-11562", "Nguemo", "Patrick", LocalDate.of(1983, 7, 25), "Biyem-Assi",
                        SituationMatrimoniale.MARIE, StatutCitoyen.ACTIF),
                createMockCitoyen(9L, "CM-2024-03418", "Kamga", "Hubert", LocalDate.of(1995, 12, 5), "Biyem-Assi",
                        SituationMatrimoniale.CELIBATAIRE, StatutCitoyen.ACTIF),
                createMockCitoyen(10L, "CM-2023-09471", "Fotso", "Emilie", LocalDate.of(1979, 10, 8), "Biyem-Assi",
                        SituationMatrimoniale.DIVORCE, StatutCitoyen.ACTIF),
                createMockCitoyen(11L, "CM-2026-40112", "Tchakounté", "Arthur", LocalDate.of(1991, 6, 29), "Biyem-Assi",
                        SituationMatrimoniale.CELIBATAIRE, StatutCitoyen.ACTIF),
                createMockCitoyen(12L, "CM-2021-00241", "Ngassa", "Monique", LocalDate.of(1935, 8, 17), "Biyem-Assi",
                        SituationMatrimoniale.VEUF, StatutCitoyen.DECEDE),

                // --- Melen ---
                createMockCitoyen(13L, "CM-2024-09412", "Essama", "Paul", LocalDate.of(1972, 5, 22), "Melen",
                        SituationMatrimoniale.MARIE, StatutCitoyen.ARCHIVE),
                createMockCitoyen(14L, "CM-2025-12994", "Amougou", "Sylvain", LocalDate.of(1989, 2, 28), "Melen",
                        SituationMatrimoniale.CELIBATAIRE, StatutCitoyen.ACTIF),
                createMockCitoyen(15L, "CM-2023-01452", "Mballa", "Jeanne", LocalDate.of(1994, 11, 11), "Melen",
                        SituationMatrimoniale.CELIBATAIRE, StatutCitoyen.ACTIF),
                createMockCitoyen(16L, "CM-2024-05841", "Biloa", "Pascaline", LocalDate.of(1968, 3, 21), "Melen",
                        SituationMatrimoniale.MARIE, StatutCitoyen.ACTIF),
                createMockCitoyen(17L, "CM-2026-00481", "Owona", "Christian", LocalDate.of(2001, 7, 19), "Melen",
                        SituationMatrimoniale.CELIBATAIRE, StatutCitoyen.ACTIF),
                createMockCitoyen(18L, "CM-2022-09441", "Zang", "Pierre", LocalDate.of(1950, 9, 30), "Melen",
                        SituationMatrimoniale.DIVORCE, StatutCitoyen.ARCHIVE),

                // --- Nlongkak ---
                createMockCitoyen(19L, "CM-2024-08801", "Fouda", "Amina", LocalDate.of(1999, 8, 30), "Nlongkak",
                        SituationMatrimoniale.CELIBATAIRE, StatutCitoyen.ACTIF),
                createMockCitoyen(20L, "CM-2025-14782", "Mvondo", "Alain", LocalDate.of(1981, 4, 3), "Nlongkak",
                        SituationMatrimoniale.MARIE, StatutCitoyen.ACTIF),
                createMockCitoyen(21L, "CM-2023-06951", "Bikolo", "Bernadette", LocalDate.of(1974, 10, 15), "Nlongkak",
                        SituationMatrimoniale.DIVORCE, StatutCitoyen.ACTIF),
                createMockCitoyen(22L, "CM-2024-01142", "Nanga", "Alice", LocalDate.of(1996, 1, 1), "Nlongkak",
                        SituationMatrimoniale.CELIBATAIRE, StatutCitoyen.ACTIF),
                createMockCitoyen(23L, "CM-2025-19884", "Assoumou", "Felix", LocalDate.of(1987, 6, 14), "Nlongkak",
                        SituationMatrimoniale.MARIE, StatutCitoyen.ACTIF),
                createMockCitoyen(24L, "CM-2026-95842", "Kotto", "Emmanuel", LocalDate.of(1993, 12, 25), "Nlongkak",
                        SituationMatrimoniale.CELIBATAIRE, StatutCitoyen.ACTIF),
                createMockCitoyen(25L, "CM-2021-03221", "Mekongo", "Luc", LocalDate.of(1948, 5, 2), "Nlongkak",
                        SituationMatrimoniale.VEUF, StatutCitoyen.ARCHIVE));
    }

    /**
     * Méthode utilitaire interne pour l'instanciation rapide d'un Citoyen.
     */
    private Citoyen createMockCitoyen(Long id, String nin, String nom, String prenom, LocalDate dateNaiss,
            String quartier, SituationMatrimoniale situation, StatutCitoyen statut) {
        Citoyen citoyen = new Citoyen();
        citoyen.setId(id);
        citoyen.setNin(nin);
        citoyen.setNom(nom);
        citoyen.setPrenom(prenom);
        citoyen.setDateNaissance(dateNaiss);
        citoyen.setLieuNaissance("Yaoundé");
        citoyen.setSexe(Sexe.M);
        citoyen.setAdresse(quartier);
        citoyen.setSituationMatrimoniale(situation);
        citoyen.setStatut(statut);
        return citoyen;
    }
}