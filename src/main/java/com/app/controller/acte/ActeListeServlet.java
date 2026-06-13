package com.app.controller.acte;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.app.jpa.config.JPAConfig;
import com.app.jpa.dao.JPADao;
import com.app.jpa.model.ActeEtatCivil;
import com.app.jpa.model.Citoyen;
import com.app.jpa.model.OfficierEtatCivil;
import com.app.jpa.model.JPAEnum.Sexe;
import com.app.jpa.model.JPAEnum.StatutActe;
import com.app.jpa.model.JPAEnum.TypeActe;
import com.app.model.viewmodel.ActeCivilRow;
import com.app.ui.ActeEtatCivilFormFactory;

@WebServlet(value = "/acte/liste", loadOnStartup = 1)
public class ActeListeServlet extends HttpServlet {
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
			// 1. Récupération et sérialisation des données pour la table
			List<ActeEtatCivil> registreActes = em.createQuery(
					"SELECT a FROM ActeEtatCivil a LEFT JOIN FETCH a.citoyenPrincipal",
					ActeEtatCivil.class).getResultList();

			List<ActeCivilRow> actesRows = registreActes.stream()
					.map(ActeCivilRow::new)
					.collect(Collectors.toList());

			Gson gson = new Gson();
			String actesJson = gson.toJson(actesRows);
			request.setAttribute("actesJson", actesJson);
			request.setAttribute("totalCount", actesRows.size());

			// 2. EXTRACTION DU PARAMÉTRAGE GÉNÉRIQUE D'AFFICHAGE (id & mode)
			String idStr = request.getParameter("id");
			String mode = request.getParameter("mode"); // attendu : "create", "edit", "preview"

			ActeEtatCivil actePourFormulaire = null;
			boolean isReadOnly = false;

			if (idStr != null && !idStr.trim().isEmpty()) {
				Long id = Long.parseLong(idStr);
				actePourFormulaire = em.find(ActeEtatCivil.class, id);

				if (actePourFormulaire != null) {
					// Ordre d'ouverture automatique de la modal pour le JavaScript (JSTL)
					request.setAttribute("autoOpenModal", true);

					// Contrainte du comportement selon le cas (mode de rendu)
					if ("preview".equalsIgnoreCase(mode)) {
						isReadOnly = true;
					}
				}
			}

			// Fallback d'initialisation en mode création si aucun acte n'est détecté
			if (actePourFormulaire == null) {
				actePourFormulaire = new ActeEtatCivil();
				// Si l'utilisateur clique explicitement sur "Ajouter", on force l'ouverture
				if ("create".equalsIgnoreCase(mode)) {
					request.setAttribute("autoOpenModal", true);
				}
			}

			// 3. Extraction des référentiels d'aide à la saisie
			List<TypeActe> listeTypes = java.util.Arrays.asList(TypeActe.values());
			List<Citoyen> listeCitoyens = em.createQuery("SELECT c FROM Citoyen c", Citoyen.class).getResultList();
			List<OfficierEtatCivil> listeOfficiers = em
					.createQuery("SELECT o FROM OfficierEtatCivil o", OfficierEtatCivil.class).getResultList();

			// 4. Génération du formulaire via TA Factory
			String formulaireHtml = ActeEtatCivilFormFactory.genererHtml(
					actePourFormulaire,
					listeTypes,
					listeCitoyens,
					listeOfficiers,
					request.getContextPath() + "/acte/formulaire",
					isReadOnly);

			// 🌟 NOUVEAU : Logique de la Modale Globale Centralisée 🌟
			// On définit le titre de façon intelligente selon l'action en cours
			String modalTitle;
			if (isReadOnly) {
				modalTitle = "Consulter l'acte n° "
						+ (actePourFormulaire.getNumeroActe() != null ? actePourFormulaire.getNumeroActe() : "");
			} else if (actePourFormulaire.getId() != null) {
				modalTitle = "Modifier l'acte n° " + actePourFormulaire.getNumeroActe();
			} else {
				modalTitle = "Dresser un nouvel acte civil";
			}

			// On injecte les variables attendues par le réceptacle dans base-layout.jsp
			request.setAttribute("modalTitle", modalTitle);
			request.setAttribute("modalContent", formulaireHtml);

			// On renvoie la vue
			request.setAttribute("view", "/WEB-INF/jsp/modules/acte/liste-acte.jsp");
			request.getRequestDispatcher("/WEB-INF/jsp/layouts/base-layout.jsp").forward(request, response);

		} finally {
			if (em != null && em.isOpen()) {
				em.close();
			}
		}
	}

	/**
	 * Insère un jeu de données de test cohérent si le registre des actes est vide
	 * en BDD.
	 */
	private void initialiserDonneesSiVide() {
		// [Ton code d'initialisation reste inchangé ici, il est parfait]
		EntityManager em = JPAConfig.getEntityManager();

		try {
			Long countActes = em.createQuery("SELECT COUNT(a) FROM ActeEtatCivil a", Long.class).getSingleResult();
			if (countActes > 0)
				return;

			EntityTransaction tx = em.getTransaction();
			try {
				tx.begin();

				System.out.println(">>> Insertion des données fictives via le mini-ORM JPADao...");
				JPADao prisma = new JPADao(em);

				Citoyen c1 = prisma.citoyen.create("Amougou", "Sylvain", "", "Melen", Sexe.M, LocalDate.of(1989, 2, 28),
						null, null, null);
				Citoyen c2 = prisma.citoyen.create("Atangana", "Dieudonné", "", "Bastos", Sexe.M,
						LocalDate.of(1963, 4, 12), null, null, null);
				Citoyen c3 = prisma.citoyen.create("Ona", "Samuel", "", "Bastos", Sexe.M, LocalDate.of(1940, 2, 14),
						null, null, null);
				Citoyen c4 = prisma.citoyen.create("Bella", "Chantal", "", "Nlongkak", Sexe.F,
						LocalDate.of(1988, 5, 18), null, null, null);

				OfficierEtatCivil officier = prisma.officier.create("", "Etoa", "Jean-Marie", "677177877",
						"2ème Adjoint", "Service Civil", null, "Etoa@1234", null, null);

				prisma.acte.create("NAI-2025-00418", TypeActe.NAISSANCE, c1, officier, LocalDate.of(2025, 2, 10),
						LocalDate.of(2025, 2, 28), "Mairie Ydé III", StatutActe.DELIVRE, "doc_00418.pdf");
				prisma.acte.create("MAR-2026-10552", TypeActe.MARIAGE, c2, officier, LocalDate.of(2026, 1, 15),
						LocalDate.of(2026, 1, 20), "Bastos", StatutActe.DELIVRE, "doc_10552.pdf");
				prisma.acte.create("DEC-2026-30119", TypeActe.DECES, c3, officier, LocalDate.of(2026, 5, 14),
						LocalDate.of(2026, 5, 18), "Hôpital Central", StatutActe.DELIVRE, "doc_30119.pdf");
				prisma.acte.create("NAI-2026-00984", TypeActe.NAISSANCE, c4, officier, LocalDate.of(2026, 6, 1),
						LocalDate.of(2026, 6, 5), "Clinique de Fouda", StatutActe.EN_COURS, null);

				tx.commit();
				System.out.println(">>> Initialisation terminée avec succès !");
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