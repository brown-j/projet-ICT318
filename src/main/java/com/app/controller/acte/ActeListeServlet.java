package com.app.controller.acte;

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

import com.google.gson.Gson;
import com.app.model.jpa.ActeEtatCivil;
import com.app.model.jpa.TypeActe;
import com.app.model.jpa.Citoyen;
import com.app.model.jpa.enums.StatutActe;
import com.app.model.viewmodel.ActeCivilRow;

@WebServlet("/acte/liste")
public class ActeListeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Récupération des données d'actes d'état civil
        List<ActeEtatCivil> registreActes = gererRegistreActesMock();

        // 2. Transformation de la couche JPA vers la couche ViewModel épurée
        List<ActeCivilRow> actesRows = registreActes.stream()
                .map(ActeCivilRow::new)
                .collect(Collectors.toList());

        // 3. Sérialisation propre en JSON pour le composant tableau.js
        Gson gson = new Gson();
        String actesJson = gson.toJson(actesRows);

        // 4. Injection des attributs requis pour le Layout Maître
        request.setAttribute("actesJson", actesJson);
        request.setAttribute("totalCount", actesRows.size());

        // 5. Redirection ciblée vers la vue des actes civils
        request.setAttribute("view", "/WEB-INF/jsp/modules/acte/liste.jsp");
        request.getRequestDispatcher("/WEB-INF/jsp/layouts/base-layout.jsp").forward(request, response);
    }

    /**
     * Génère un jeu d'essai réaliste d'actes civils pour la Mairie de Yaoundé III.
     */
    private List<ActeEtatCivil> gererRegistreActesMock() {
        // Préparation des types d'actes récurrents
        TypeActe naissance = createMockTypeActe("Naissance");
        TypeActe mariage = createMockTypeActe("Mariage");
        TypeActe deces = createMockTypeActe("Décès");

        return Arrays.asList(
                // Actes Délivrés avec PDF valide
                createMockActe(1L, "NAI-2025-00418", naissance, "Amougou", "Sylvain", LocalDate.of(2025, 2, 10),
                        LocalDate.of(2025, 2, 28), "Mairie Ydé III", StatutActe.DELIVRE, "doc_00418.pdf"),
                createMockActe(2L, "MAR-2026-10552", mariage, "Atangana", "Dieudonné", LocalDate.of(2026, 1, 15),
                        LocalDate.of(2026, 1, 20), "Bastos", StatutActe.DELIVRE, "doc_10552.pdf"),
                createMockActe(3L, "DEC-2026-30119", deces, "Ona", "Samuel", LocalDate.of(2026, 5, 14),
                        LocalDate.of(2026, 5, 18), "Hôpital Central", StatutActe.DELIVRE, "doc_30119.pdf"),

                // Actes En cours (En attente de traitement ou signature) - Pas de PDF attaché
                createMockActe(4L, "NAI-2026-00984", naissance, "Bella", "Chantal", LocalDate.of(2026, 6, 1),
                        LocalDate.of(2026, 6, 5), "Clinique de Fouda", StatutActe.EN_COURS, null),
                createMockActe(5L, "MAR-2026-01102", mariage, "Mbarga", "Jean-Paul", LocalDate.of(2026, 5, 29),
                        LocalDate.of(2026, 6, 2), "Mairie Ydé III", StatutActe.EN_COURS, null),

                // Actes Archivés ou Annulés
                createMockActe(6L, "NAI-1990-88412", naissance, "Abena Zoa", "Marie", LocalDate.of(1990, 3, 14),
                        LocalDate.of(1990, 3, 20), "Melen", StatutActe.ARCHIVE, "archive_90.pdf"),
                createMockActe(7L, "MAR-2024-00142", mariage, "Essama", "Paul", LocalDate.of(2024, 4, 11),
                        LocalDate.of(2024, 4, 12), "Mairie Ydé III", StatutActe.ANNULE, null));
    }

    /**
     * Méthode utilitaire pour générer à la volée un TypeActe mocké.
     */
    private TypeActe createMockTypeActe(String libelle) {
        TypeActe type = new TypeActe();
        // Si ta propriété s'appelle setNom() au lieu de setLibelle(), ajuste ici :
        type.setLibelle(libelle);
        return type;
    }

    /**
     * Méthode utilitaire pour assembler rapidement un ActeEtatCivil complet.
     */
    private ActeEtatCivil createMockActe(Long id, String numero, TypeActe type, String nomCitoyen, String prenomCitoyen,
            LocalDate dateEvenement, LocalDate dateEtabli, String lieu, StatutActe statut, String pdf) {
        ActeEtatCivil acte = new ActeEtatCivil();
        acte.setId(id);
        acte.setNumeroActe(numero);
        acte.setTypeActe(type);

        // Citoyen minimaliste lié à l'acte
        Citoyen citoyen = new Citoyen();
        citoyen.setNom(nomCitoyen);
        citoyen.setPrenom(prenomCitoyen);
        acte.setCitoyenPrincipal(citoyen);

        acte.setDateEvenement(dateEvenement);
        acte.setDateEtablissement(dateEtabli);
        acte.setLieuEvenement(lieu);
        acte.setStatut(statut);
        acte.setFichierPdf(pdf);

        return acte;
    }
}