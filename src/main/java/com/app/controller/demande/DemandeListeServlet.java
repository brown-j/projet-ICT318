package com.app.controller.demande;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.app.jpa.config.JPAConfig;
import com.app.jpa.dao.JPADao;
import com.app.jpa.model.Citoyen;
import com.app.jpa.model.DemandeAdministrative;
import com.app.jpa.model.JPAEnum.PrioriteDemande;
import com.app.jpa.model.JPAEnum.Sexe;
import com.app.jpa.model.JPAEnum.SituationMatrimoniale;
import com.app.jpa.model.JPAEnum.StatutCitoyen;
import com.app.jpa.model.JPAEnum.StatutDemande;
import com.app.jpa.model.JPAEnum.TypeDemande;
import com.app.model.viewmodel.DemandeRow;
import com.app.ui.DemandeFormFactory;

@WebServlet(value = "/demande/liste", loadOnStartup = 1)
public class DemandeListeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		super.init();
		initialiserDonneesSiVide();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		EntityManager em = JPAConfig.getEntityManager();

		try {
			// 1. Récupération et sérialisation des requêtes avec jointure optimisée (FETCH)
			List<DemandeAdministrative> registreDemandes = em.createQuery(
					"SELECT d FROM DemandeAdministrative d LEFT JOIN FETCH d.citoyenRequerant",
					DemandeAdministrative.class).getResultList();

			List<DemandeRow> demandesRows = registreDemandes.stream()
					.map(DemandeRow::new)
					.collect(Collectors.toList());

			Gson gson = new Gson();
			String demandesJson = gson.toJson(demandesRows);
			request.setAttribute("demandesJson", demandesJson);
			request.setAttribute("totalCount", demandesRows.size());

			// 2. EXTRACTION DU PARAMÉTRAGE GÉNÉRIQUE D'AFFICHAGE (id & mode)
			String idStr = request.getParameter("id");
			String mode = request.getParameter("mode"); // attendu : "create", "edit", "preview"

			DemandeAdministrative demandePourFormulaire = null;
			boolean isReadOnly = false;

			if (idStr != null && !idStr.trim().isEmpty()) {
				Long id = Long.parseLong(idStr);
				demandePourFormulaire = em.find(DemandeAdministrative.class, id);

				if (demandePourFormulaire != null) {
					request.setAttribute("autoOpenModal", true);
					if ("preview".equalsIgnoreCase(mode)) {
						isReadOnly = true;
					}
				}
			}

			// Fallback d'initialisation en mode création si aucun ID n'est détecté
			if (demandePourFormulaire == null) {
				demandePourFormulaire = new DemandeAdministrative();
				if ("create".equalsIgnoreCase(mode)) {
					request.setAttribute("autoOpenModal", true);
				}
			}

			// 3. Extraction des référentiels d'aide à la saisie pour les ComboBox
			List<TypeDemande> listeTypes = Arrays.asList(TypeDemande.values());
			List<PrioriteDemande> listePriorites = Arrays.asList(PrioriteDemande.values());
			List<Citoyen> listeCitoyens = em.createQuery("SELECT c FROM Citoyen c", Citoyen.class)
					.getResultList();

			// 4. Génération du formulaire via sa Factory dédiée
			String formulaireHtml = DemandeFormFactory.genererHtml(
					demandePourFormulaire,
					listeTypes,
					listePriorites,
					listeCitoyens,
					request.getContextPath() + "/demande/formulaire",
					isReadOnly);

			// 🌟 NOUVEAU : Logique de la Modale Globale Centralisée 🌟
			String modalTitle;
			if (isReadOnly) {
				modalTitle = "Détails de la demande administrative";
			} else if (demandePourFormulaire.getId() != null) {
				modalTitle = "Modifier la demande administrative";
			} else {
				modalTitle = "Nouvelle demande administrative";
			}

			// Injection des variables pour le réceptacle dans base-layout.jsp
			request.setAttribute("modalTitle", modalTitle);
			request.setAttribute("modalContent", formulaireHtml);

			// 5. Expédition vers le layout maître global
			request.setAttribute("view", "/WEB-INF/jsp/modules/demande/liste-demande.jsp");
			request.getRequestDispatcher("/WEB-INF/jsp/layouts/base-layout.jsp").forward(request, response);

		} finally {
			if (em != null && em.isOpen()) {
				em.close();
			}
		}
	}

	/**
	 * Insère un jeu de données de test cohérent si le registre des demandes est
	 * vide en BDD, en utilisant le JPADao.
	 */
	private void initialiserDonneesSiVide() {
		EntityManager em = JPAConfig.getEntityManager();

		try {
			Long countDemandes = em.createQuery("SELECT COUNT(d) FROM DemandeAdministrative d", Long.class)
					.getSingleResult();
			if (countDemandes > 0)
				return;

			EntityTransaction tx = em.getTransaction();
			try {
				tx.begin();
				System.out.println(
						">>> [DemandeListeServlet] Table vide. Génération du jeu d'essai via JPADao...");

				// 💡 Instanciation du DAO
				JPADao dao = new JPADao(em);

				// 1. Initialisation de quelques citoyens témoins
				Citoyen c1 = dao.citoyen.create("Fouda", "Amina", "", "Yaoundé", Sexe.F,
						LocalDate.of(1990, 1, 1), null, SituationMatrimoniale.CELIBATAIRE,
						StatutCitoyen.ACTIF);
				Citoyen c2 = dao.citoyen.create("Nguemo", "Patrick", "", "Yaoundé", Sexe.M,
						LocalDate.of(1990, 1, 1), null, SituationMatrimoniale.CELIBATAIRE,
						StatutCitoyen.ACTIF);
				Citoyen c3 = dao.citoyen.create("Kamga", "Hubert", "", "Yaoundé", Sexe.M,
						LocalDate.of(1990, 1, 1), null, SituationMatrimoniale.CELIBATAIRE,
						StatutCitoyen.ACTIF);
				Citoyen c4 = dao.citoyen.create("Amougou", "Sylvain", "", "Yaoundé",
						Sexe.M, LocalDate.of(1990, 1, 1), null,
						SituationMatrimoniale.CELIBATAIRE, StatutCitoyen.ACTIF);
				Citoyen c5 = dao.citoyen.create("Bella", "Chantal", "", "Yaoundé", Sexe.F,
						LocalDate.of(1990, 1, 1), null, SituationMatrimoniale.CELIBATAIRE,
						StatutCitoyen.ACTIF);
				Citoyen c6 = dao.citoyen.create("Atangana", "Dieudonné", "", "Yaoundé",
						Sexe.M, LocalDate.of(1990, 1, 1), null,
						SituationMatrimoniale.CELIBATAIRE, StatutCitoyen.ACTIF);
				Citoyen c7 = dao.citoyen.create("Mbarga", "Jean-Paul", "", "Yaoundé",
						Sexe.M, LocalDate.of(1990, 1, 1), null,
						SituationMatrimoniale.CELIBATAIRE, StatutCitoyen.ACTIF);
				Citoyen c8 = dao.citoyen.create("Mballa", "Jeanne", "", "Yaoundé", Sexe.F,
						LocalDate.of(1990, 1, 1), null, SituationMatrimoniale.CELIBATAIRE,
						StatutCitoyen.ACTIF);

				// 2. Insertion du registre des demandes connectées aux citoyens
				dao.demande.create("DM-2026-0091", TypeDemande.CERTIFICAT_CELIBAT, c1,
						LocalDateTime.of(2026, 6, 8, 9, 30), StatutDemande.SOUMISE,
						PrioriteDemande.NORMALE, null);
				dao.demande.create("DM-2026-0092", TypeDemande.AUTORISATION_CONSTRUIRE, c2,
						LocalDateTime.of(2026, 6, 8, 14, 15), StatutDemande.SOUMISE,
						PrioriteDemande.HAUTE, null);
				dao.demande.create("DM-2026-0093", TypeDemande.LEGALISTION_SIGNATURE, c3,
						LocalDateTime.of(2026, 6, 9, 8, 0), StatutDemande.SOUMISE,
						PrioriteDemande.URGENTE, null);
				dao.demande.create("DM-2026-0075", TypeDemande.LEGALISTION_SIGNATURE, c4,
						LocalDateTime.of(2026, 6, 5, 11, 0), StatutDemande.EN_COURS,
						PrioriteDemande.NORMALE, null);
				dao.demande.create("DM-2026-0081", TypeDemande.CERTIFICAT_CELIBAT, c5,
						LocalDateTime.of(2026, 6, 6, 16, 45), StatutDemande.EN_COURS,
						PrioriteDemande.HAUTE, null);
				dao.demande.create("DM-2026-0012", TypeDemande.AUTORISATION_CONSTRUIRE, c6,
						LocalDateTime.of(2026, 5, 12, 10, 20), StatutDemande.VALIDEE,
						PrioriteDemande.BASSE, "certif_res_0012.pdf");
				dao.demande.create("DM-2026-0044", TypeDemande.AUTORISATION_CONSTRUIRE, c7,
						LocalDateTime.of(2026, 5, 20, 15, 30), StatutDemande.REJETEE,
						PrioriteDemande.NORMALE, null);
				dao.demande.create("DM-2026-0002", TypeDemande.LEGALISTION_SIGNATURE, c8,
						LocalDateTime.of(2026, 5, 2, 9, 10), StatutDemande.CLOTUREE,
						PrioriteDemande.BASSE, "legal_0002.pdf");

				tx.commit();
				System.out.println(
						">>> [DemandeListeServlet] Seeding du registre terminé avec succès.");
			} catch (Exception e) {
				if (tx.isActive())
					tx.rollback();
				e.printStackTrace();
			}
		} finally {
			if (em != null && em.isOpen())
				em.close();
		}
	}
}