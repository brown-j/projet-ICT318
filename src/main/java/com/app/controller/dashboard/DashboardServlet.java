package com.app.controller.dashboard;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.app.jpa.config.JPAConfig;
import com.app.jpa.dao.JPADao;
import com.app.jpa.model.DemandeAdministrative;
import com.app.jpa.model.JournalAudit;
import com.app.jpa.model.TypeActe;
import com.app.jpa.model.Citoyen; // NOUVEAU : Import du modèle Citoyen
import com.app.jpa.model.JPAEnum.PrioriteDemande;
import com.app.jpa.model.JPAEnum.StatutDemande;
import com.app.model.icon.Icons;
import com.app.model.theme.ThemeColor;
import com.app.model.viewmodel.KpiData;
import com.app.model.viewmodel.EvolutionActesData;
import com.app.model.viewmodel.ActiviteData;
import com.app.model.viewmodel.DemandeData;
import com.app.ui.CitoyenFormFactory; // NOUVEAU : Import de la Factory (à adapter si besoin)
import com.google.gson.Gson;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        EntityManager em = JPAConfig.getEntityManager();
        JPADao dao = new JPADao(em);
        Gson gson = new Gson();

        try {
            // --- Préparation des dates ---
            // 1. Pour les requêtes nécessitant Date + Heure (Citoyen, Paiement, etc.)
            LocalDateTime debutMoisDateTime = java.time.LocalDate.now().withDayOfMonth(1).atStartOfDay();
            LocalDateTime finMoisDateTime = java.time.LocalDate.now().plusMonths(1).withDayOfMonth(1).atStartOfDay();

            // 2. Pour les requêtes nécessitant UNIQUEMENT la Date (ActeEtatCivil)
            LocalDate debutMoisDateOnly = java.time.LocalDate.now().withDayOfMonth(1);

            int anneeEnCours = java.time.LocalDate.now().getYear();

            // --- 1. KPIs REELS ---
            long totalCitoyens = dao.citoyen.count();
            long citoyensCeMois = dao.citoyen.countWithCondition("e.dateInscription >= ?1", debutMoisDateTime);

            long actesCeMois = dao.acte.countWithCondition("e.dateEtablissement >= ?1", debutMoisDateOnly);

            long demandesAttente = dao.demande.countByStatuts(List.of(StatutDemande.SOUMISE, StatutDemande.EN_COURS));
            long demandesUrgentes = dao.demande.countWithCondition("e.priorite = ?1 AND e.statut = ?2",
                    PrioriteDemande.URGENTE, StatutDemande.SOUMISE);

            java.math.BigDecimal recettesMois = dao.paiement.sumMontantByPeriode(debutMoisDateTime, finMoisDateTime);

            List<KpiData> kpis = List.of(
                    new KpiData(ThemeColor.PRIMARY, Icons.USERS.toString(), String.valueOf(totalCitoyens),
                            "Citoyens enregistrés", true, "+" + citoyensCeMois + " ce mois"),
                    new KpiData(ThemeColor.SECONDARY, Icons.FILE_CERTIFICATE.toString(), String.valueOf(actesCeMois),
                            "Actes ce mois", true, ""),
                    new KpiData(ThemeColor.ACCENT, Icons.CLIPBOARD_LIST.toString(), String.valueOf(demandesAttente),
                            "Demandes en attente", false, demandesUrgentes + " urgentes"),
                    new KpiData(ThemeColor.SUCCESS, Icons.CURRENCY_FRANC.toString(), recettesMois.longValue() + " F",
                            "Recettes (mois)", true, ""));
            request.setAttribute("kpis", kpis);

            // --- Évolution des actes (Graphique REEL) ---
            // 1. On récupère les instances depuis le cache mémoire (Ultra-rapide)
            TypeActe tNaissance = dao.typeActe.findInCache("ACTE_NAISS");
            TypeActe tMariage = dao.typeActe.findInCache("ACTE_MARI");
            TypeActe tDeces = dao.typeActe.findInCache("ACTE_DECES");

            // 2. On passe les entités à la méthode DAO
            List<Integer> naissances = dao.acte.getEvolutionMensuelle(tNaissance, anneeEnCours);
            List<Integer> mariages = dao.acte.getEvolutionMensuelle(tMariage, anneeEnCours);
            List<Integer> deces = dao.acte.getEvolutionMensuelle(tDeces, anneeEnCours);

            EvolutionActesData evolutionData = new EvolutionActesData(naissances, mariages, deces);
            request.setAttribute("evolutionDataJson", gson.toJson(evolutionData));
            request.setAttribute("repartitionTypes", evolutionData.getRepartitionTypes());

            // --- 3. Demandes Récentes (Générique via le parent) ---
            List<DemandeAdministrative> rawDemandes = dao.demande.findRecent("dateSoumission", 5);
            List<DemandeData> recentDemandesList = rawDemandes.stream()
                    .map(DemandeData::new)
                    .collect(Collectors.toList());
            request.setAttribute("recentDemandesList", recentDemandesList);

            //// --- 4. Activités Récentes (Style Prisma ultra-propre) ---
            List<JournalAudit> rawAudits = dao.audit.findRecentWithOfficier(5);
            List<ActiviteData> recentActivitesList = rawAudits.stream()
                    .map(ActiviteData::new)
                    .collect(Collectors.toList());
            request.setAttribute("recentActivitesList", recentActivitesList);

            // 🌟 NOUVEAU : Interception du raccourci pour la modale globale 🌟
            String action = request.getParameter("action");
            if ("createCitoyen".equals(action)) {
                // Instanciation d'un citoyen vide
                Citoyen nouveauCitoyen = new Citoyen();

                // 3. Génération dynamique du formulaire
                String actionUrl = request.getContextPath() + "/citoyen/formulaire";
                String formHtml = CitoyenFormFactory.genererHtml(
                        nouveauCitoyen,
                        actionUrl,
                        false);
                request.setAttribute("formulaireHtml", formHtml);

                // On passe les données au réceptacle dynamique dans base-layout.jsp
                request.setAttribute("modalTitle", "Ajout rapide d'un citoyen");
                request.setAttribute("modalContent", formHtml);
                request.setAttribute("autoOpenModal", true);
            }

        } finally {
            if (em.isOpen())
                em.close();
        }

        // Forward
        request.setAttribute("view", "/WEB-INF/jsp/modules/dashboard/index.jsp");
        request.getRequestDispatcher("/WEB-INF/jsp/layouts/base-layout.jsp").forward(request, response);
    }
}