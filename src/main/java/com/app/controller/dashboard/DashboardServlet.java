package com.app.controller.dashboard;

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

import com.app.model.icon.Icons;
import com.app.model.jpa.Citoyen;
import com.app.model.jpa.DemandeAdministrative;
import com.app.model.jpa.JournalAudit;
import com.app.model.jpa.TypeDemande;
import com.app.model.jpa.enums.StatutDemande;
import com.app.model.theme.ThemeColor;
import com.app.model.viewmodel.KpiData;
import com.app.model.viewmodel.EvolutionActesData;
import com.app.model.viewmodel.ActiviteData;
import com.app.model.viewmodel.DemandeData;
import com.google.gson.Gson;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Sérialiseur json
		Gson gson = new Gson();

		// 1. KPIs (Existant)
		List<KpiData> kpis = List.of(
				new KpiData(ThemeColor.PRIMARY, Icons.USERS.toString(), "1 284", "Citoyens enregistrés", true,
						"+34 ce mois"),
				new KpiData(ThemeColor.SECONDARY, Icons.FILE_CERTIFICATE.toString(), "47", "Actes ce mois", true,
						"+12% vs N-1"),
				new KpiData(ThemeColor.ACCENT, Icons.CLIPBOARD_LIST.toString(), "12", "Demandes en attente", false,
						"3 urgentes"),
				new KpiData(ThemeColor.SUCCESS, Icons.CURRENCY_FRANC.toString(), "842 K", "Recettes FCFA (mois)", true,
						"+8% objectif"));
		request.setAttribute("kpis", kpis);

		// 2. Évolution des actes (Graphique en barres)
		List<Integer> naissances = Arrays.asList(14, 18, 12, 16, 20, 15, 17, 13, 19, 22, 16, 18);
		List<Integer> mariages = Arrays.asList(8, 10, 6, 9, 12, 8, 11, 7, 10, 14, 9, 11);
		List<Integer> deces = Arrays.asList(5, 4, 7, 5, 6, 8, 5, 6, 4, 7, 5, 4);

		EvolutionActesData evolutionData = new EvolutionActesData(naissances, mariages, deces);
		request.setAttribute("evolutionDataJson", gson.toJson(evolutionData));
		request.setAttribute("repartitionTypes", evolutionData.getRepartitionTypes());

		// 3. Demandes Récentes (Mockées)
		List<DemandeAdministrative> rawDemandes = Arrays.asList(
				createMockDemande("DEM-2026-0089", "Cert. de résidence", "Amina", "Fouda", StatutDemande.EN_COURS,
						LocalDateTime.now().minusHours(2)),
				createMockDemande("DEM-2026-0088", "Acte de naissance", "Jean-Paul", "Mbarga", StatutDemande.SOUMISE,
						LocalDateTime.now().minusHours(4)),
				createMockDemande("DEM-2026-0087", "Autorisation construire", "Société", "BTP Cam",
						StatutDemande.REJETEE, LocalDateTime.now().minusDays(1)),
				createMockDemande("DEM-2026-0086", "Legalisation signature", "Paul", "Essama", StatutDemande.VALIDEE,
						LocalDateTime.now().minusDays(1)),
				createMockDemande("DEM-2026-0085", "Extrait naissance", "Odile", "Biyong", StatutDemande.CLOTUREE,
						LocalDateTime.now().minusDays(2)));
		// Transformation stricte des entités brutes en ViewModels "stupides"
		List<DemandeData> recentDemandesList = rawDemandes.stream()
				.map(DemandeData::new)
				.collect(Collectors.toList());

		request.setAttribute("recentDemandesList", recentDemandesList);

		// Dans doGet(), section 4. Activités Récentes

		// Création d'entités brutes simulées
		List<JournalAudit> rawAudits = Arrays.asList(
				createMockAudit("CREE", "acte_etat_civil", LocalDateTime.now().minusMinutes(45)),
				createMockAudit("ENREGISTRE", "citoyen", LocalDateTime.now().minusHours(1).minusMinutes(12)),
				createMockAudit("ENCAISSE", "paiement", LocalDateTime.now().minusHours(2).minusMinutes(5)));

		// Transformation en ViewModels "stupides"
		List<ActiviteData> recentActivitesList = rawAudits.stream()
				.map(ActiviteData::new)
				.collect(Collectors.toList());

		request.setAttribute("recentActivitesList", recentActivitesList);

		// Forward
		request.setAttribute("view", "/WEB-INF/jsp/modules/dashboard/index.jsp");
		request.getRequestDispatcher("/WEB-INF/jsp/layouts/base-layout.jsp").forward(request, response);
	}

	/**
	 * Méthode utilitaire pour générer de fausses entités JPA rapidement
	 */
	private DemandeAdministrative createMockDemande(String numero, String libelleType, String prenom, String nom,
			StatutDemande statut, LocalDateTime dateSoumission) {
		TypeDemande type = new TypeDemande();
		type.setLibelle(libelleType);

		Citoyen citoyen = new Citoyen();
		citoyen.setPrenom(prenom);
		citoyen.setNom(nom);

		DemandeAdministrative demande = new DemandeAdministrative();
		demande.setNumeroSuivi(numero);
		demande.setTypeDemande(type);
		demande.setCitoyenRequerant(citoyen);
		demande.setStatut(statut);
		demande.setDateSoumission(dateSoumission); // Injecte la date relative simulée

		return demande;
	}

	private JournalAudit createMockAudit(String action, String table, LocalDateTime dateAction) {
		JournalAudit audit = new JournalAudit();
		audit.setAction(action);
		audit.setTableAffectee(table);
		audit.setDateAction(dateAction);

		// On pourrait simuler un OfficierEtatCivil ici si nécessaire
		return audit;
	}
}