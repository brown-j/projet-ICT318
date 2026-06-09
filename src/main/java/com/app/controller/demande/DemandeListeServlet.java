package com.app.controller.demande;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.app.model.jpa.DemandeAdministrative;
import com.app.model.jpa.TypeDemande;
import com.app.model.jpa.Citoyen;
import com.app.model.jpa.enums.StatutDemande;
import com.app.model.jpa.enums.PrioriteDemande;
import com.app.model.viewmodel.DemandeRow;

@WebServlet("/demande/liste")
public class DemandeListeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Récupération des données du registre des demandes
        List<DemandeAdministrative> registreDemandes = gererRegistreDemandesMock();

        // 2. Mapping vers notre ViewModel allégé DemandeRow
        List<DemandeRow> demandesRows = registreDemandes.stream()
                .map(DemandeRow::new)
                .collect(Collectors.toList());

        // 3. Sérialisation Gson standardisée pour tableau.js
        Gson gson = new Gson();
        String demandesJson = gson.toJson(demandesRows);

        // 4. Injection des attributs indispensables pour le Layout Maître
        request.setAttribute("demandesJson", demandesJson);
        request.setAttribute("totalCount", demandesRows.size());

        // 5. Routage vers le fichier de vue dédié aux demandes
        request.setAttribute("view", "/WEB-INF/jsp/modules/demande/liste.jsp");
        request.getRequestDispatcher("/WEB-INF/jsp/layouts/base-layout.jsp").forward(request, response);
    }

    /**
     * Génère un jeu de données mockées de demandes administratives pour la commune
     * de Yaoundé III.
     */
    private List<DemandeAdministrative> gererRegistreDemandesMock() {
        // Déclaration des types de demandes récurrentes en mairie
        TypeDemande celibat = createMockTypeDemande("Certificat de Célibat", 2, 2500);
        TypeDemande legalisation = createMockTypeDemande("Légalisation de document", 1, 1000);
        TypeDemande batir = createMockTypeDemande("Permis de bâtir", 30, 75000);
        TypeDemande residence = createMockTypeDemande("Certificat de Résidence", 3, 1500);

        return Arrays.asList(
                // Nouvelles demandes (SOUMISE) - Priorités variables
                createMockDemande(1L, "DM-2026-0091", celibat, "Fouda", "Amina", LocalDateTime.of(2026, 6, 8, 9, 30),
                        StatutDemande.SOUMISE, PrioriteDemande.NORMALE, null),
                createMockDemande(2L, "DM-2026-0092", batir, "Nguemo", "Patrick", LocalDateTime.of(2026, 6, 8, 14, 15),
                        StatutDemande.SOUMISE, PrioriteDemande.HAUTE, null),
                createMockDemande(3L, "DM-2026-0093", legalisation, "Kamga", "Hubert",
                        LocalDateTime.of(2026, 6, 9, 8, 0), StatutDemande.SOUMISE, PrioriteDemande.URGENTE, null),

                // Demandes en cours de traitement par les agents
                createMockDemande(4L, "DM-2026-0075", residence, "Amougou", "Sylvain",
                        LocalDateTime.of(2026, 6, 5, 11, 0), StatutDemande.EN_COURS, PrioriteDemande.NORMALE, null),
                createMockDemande(5L, "DM-2026-0081", celibat, "Bella", "Chantal", LocalDateTime.of(2026, 6, 6, 16, 45),
                        StatutDemande.EN_COURS, PrioriteDemande.HAUTE, null),

                // Demandes terminées (Validées avec document généré ou Rejetées)
                createMockDemande(6L, "DM-2026-0012", residence, "Atangana", "Dieudonné",
                        LocalDateTime.of(2026, 5, 12, 10, 20), StatutDemande.VALIDEE, PrioriteDemande.BASSE,
                        "certif_res_0012.pdf"),
                createMockDemande(7L, "DM-2026-0044", batir, "Mbarga", "Jean-Paul",
                        LocalDateTime.of(2026, 5, 20, 15, 30), StatutDemande.REJETEE, PrioriteDemande.NORMALE, null),
                createMockDemande(8L, "DM-2026-0002", legalisation, "Mballa", "Jeanne",
                        LocalDateTime.of(2026, 5, 2, 9, 10), StatutDemande.CLOTUREE, PrioriteDemande.BASSE,
                        "legal_0002.pdf"));
    }

    /**
     * Helper pour instancier rapidement un TypeDemande.
     */
    private TypeDemande createMockTypeDemande(String libelle, int delai, double tarif) {
        TypeDemande td = new TypeDemande();
        td.setLibelle(libelle);
        td.setDelaiStandardJours(delai);
        td.setTarifFcfa(java.math.BigDecimal.valueOf(tarif));
        return td;
    }

    /**
     * Helper pour assembler une DemandeAdministrative complète.
     */
    private DemandeAdministrative createMockDemande(Long id, String code, TypeDemande type, String nom, String prenom,
            LocalDateTime dateSoumis, StatutDemande statut, PrioriteDemande priorite, String doc) {
        DemandeAdministrative da = new DemandeAdministrative();
        da.setId(id);
        da.setNumeroSuivi(code);
        da.setTypeDemande(type);

        Citoyen c = new Citoyen();
        c.setNom(nom);
        c.setPrenom(prenom);
        da.setCitoyenRequerant(c);

        da.setDateSoumission(dateSoumis);
        da.setStatut(statut);
        da.setPriorite(priorite);
        da.setDocumentFinal(doc);
        return da;
    }
}