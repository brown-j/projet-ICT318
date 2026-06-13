package com.app.controller.citoyen;

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
import com.app.jpa.model.Citoyen;
import com.app.jpa.model.JPAEnum.Sexe;
import com.app.jpa.model.JPAEnum.SituationMatrimoniale;
import com.app.jpa.model.JPAEnum.StatutCitoyen;
import com.app.model.viewmodel.CitoyenRow;
import com.app.ui.CitoyenFormFactory;

@WebServlet(value = "/citoyen/liste", loadOnStartup = 1)
public class CitoyenListeServlet extends HttpServlet {
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
			// 1. Extraction et sérialisation de la liste synchrone
			List<Citoyen> registreCommunal = em.createQuery("SELECT c FROM Citoyen c", Citoyen.class)
					.getResultList();

			List<CitoyenRow> citoyensRows = registreCommunal.stream()
					.map(CitoyenRow::new)
					.collect(Collectors.toList());

			Gson gson = new Gson();
			String citoyensJson = gson.toJson(citoyensRows);
			request.setAttribute("citoyensJson", citoyensJson);
			request.setAttribute("totalCount", citoyensRows.size());

			// 2. EXTRACTION DU PARAMÉTRAGE GÉNÉRIQUE (id & mode)
			String idStr = request.getParameter("id");
			String mode = request.getParameter("mode");

			Citoyen citoyenPourFormulaire = null;
			boolean isReadOnly = false;

			if (idStr != null && !idStr.trim().isEmpty()) {
				Long id = Long.parseLong(idStr);
				citoyenPourFormulaire = em.find(Citoyen.class, id);

				if (citoyenPourFormulaire != null) {
					request.setAttribute("autoOpenModal", true);
					if ("preview".equalsIgnoreCase(mode)) {
						isReadOnly = true;
					}
				}
			}

			if (citoyenPourFormulaire == null) {
				citoyenPourFormulaire = new Citoyen();
				if ("create".equalsIgnoreCase(mode)) {
					request.setAttribute("autoOpenModal", true);
				}
			}

			// 3. Génération dynamique du formulaire
			String actionUrl = request.getContextPath() + "/citoyen/formulaire";
			String formHtml = CitoyenFormFactory.genererHtml(
					citoyenPourFormulaire,
					actionUrl,
					isReadOnly);

			// 🌟 NOUVEAU : Logique de la Modale Globale Centralisée 🌟
			String modalTitle;
			if (isReadOnly) {
				modalTitle = "Détails du citoyen : " + (citoyenPourFormulaire.getNom() != null
						? citoyenPourFormulaire.getNom() + " " + citoyenPourFormulaire.getPrenom()
						: "");
			} else if (citoyenPourFormulaire.getId() != null) {
				modalTitle = "Modifier le citoyen : " + citoyenPourFormulaire.getNom() + " "
						+ citoyenPourFormulaire.getPrenom();
			} else {
				modalTitle = "Enregistrer un nouveau citoyen";
			}

			// Injection des variables pour le réceptacle dans base-layout.jsp
			request.setAttribute("modalTitle", modalTitle);
			request.setAttribute("modalContent", formHtml);

			// 4. Routage vers le layout maître
			request.setAttribute("view", "/WEB-INF/jsp/modules/citoyen/liste-citoyen.jsp");
			request.getRequestDispatcher("/WEB-INF/jsp/layouts/base-layout.jsp").forward(request, response);

		} finally {
			if (em != null && em.isOpen()) {
				em.close();
			}
		}
	}

	/**
	 * Initialise le registre communal en utilisant le JPADao.
	 */
	private void initialiserDonneesSiVide() {
		EntityManager em = JPAConfig.getEntityManager();

		try {
			Long countCitoyens = em.createQuery("SELECT COUNT(c) FROM Citoyen c", Long.class)
					.getSingleResult();

			if (countCitoyens > 0)
				return;

			EntityTransaction tx = em.getTransaction();
			try {
				tx.begin();
				System.out.println(
						"[CitoyenListeServlet] Table vide. Génération des 25 citoyens témoins via Prisma ORM...");

				// Initialisation de ton DAO avec l'EntityManager actif
				JPADao jpa = new JPADao(em);

				// Appel de la méthode d'insertion directe
				insererMocksViaDAO(jpa);

				tx.commit();
				System.out.println("[CitoyenListeServlet] Initialisation du registre communal terminée.");
			} catch (Exception e) {
				if (tx.isActive())
					tx.rollback();
				e.printStackTrace();
			}
		} finally {
			if (em != null && em.isOpen()) {
				em.close();
			}
		}
	}

	/**
	 * Utilise le JPADao pour créer et persister directement les citoyens.
	 * Ordre des paramètres du DAO : nom, prenom, nin, adresse, sexe, dateNaissance,
	 * lieuNaissance, situationMatr, statut
	 */
	private void insererMocksViaDAO(JPADao jpa) {
		// --- Bastos ---
		jpa.citoyen.create("Mbarga", "Jean-Paul", "", "Bastos", Sexe.M, LocalDate.of(1985, 11, 7), null,
				SituationMatrimoniale.CELIBATAIRE, StatutCitoyen.ACTIF);
		jpa.citoyen.create("Atangana", "Dieudonné", "", "Bastos", Sexe.M, LocalDate.of(1963, 4, 12), null,
				SituationMatrimoniale.MARIE, StatutCitoyen.ACTIF);
		jpa.citoyen.create("Ndi", "Therese", "", "Bastos", Sexe.F, LocalDate.of(1975, 9, 23), null,
				SituationMatrimoniale.MARIE, StatutCitoyen.ACTIF);
		jpa.citoyen.create("Etoa", "Marc", "", "Bastos", Sexe.M, LocalDate.of(1992, 1, 30), null,
				SituationMatrimoniale.DIVORCE, StatutCitoyen.ARCHIVE);
		jpa.citoyen.create("Bella", "Chantal", "", "Bastos", Sexe.F, LocalDate.of(1988, 5, 18), null,
				SituationMatrimoniale.CELIBATAIRE, StatutCitoyen.ACTIF);
		jpa.citoyen.create("Ona", "Samuel", "", "Bastos", Sexe.M, LocalDate.of(1940, 2, 14), null,
				SituationMatrimoniale.VEUF, StatutCitoyen.DECEDE);

		// --- Biyem-Assi ---
		jpa.citoyen.create("Abena Zoa", "Marie", "", "Biyem-Assi", Sexe.F, LocalDate.of(1990, 3, 14), null,
				SituationMatrimoniale.MARIE, StatutCitoyen.ACTIF);
		jpa.citoyen.create("Nguemo", "Patrick", "", "Biyem-Assi", Sexe.M, LocalDate.of(1983, 7, 25), null,
				SituationMatrimoniale.MARIE, StatutCitoyen.ACTIF);
		jpa.citoyen.create("Kamga", "Hubert", "", "Biyem-Assi", Sexe.M, LocalDate.of(1995, 12, 5), null,
				SituationMatrimoniale.CELIBATAIRE, StatutCitoyen.ACTIF);
		jpa.citoyen.create("Fotso", "Emilie", "", "Biyem-Assi", Sexe.F, LocalDate.of(1979, 10, 8), null,
				SituationMatrimoniale.DIVORCE, StatutCitoyen.ACTIF);
		jpa.citoyen.create("Tchakounté", "Arthur", "", "Biyem-Assi", Sexe.M, LocalDate.of(1991, 6, 29),
				null, SituationMatrimoniale.CELIBATAIRE, StatutCitoyen.ACTIF);
		jpa.citoyen.create("Ngassa", "Monique", "", "Biyem-Assi", Sexe.F, LocalDate.of(1935, 8, 17), null,
				SituationMatrimoniale.VEUF, StatutCitoyen.DECEDE);

		// --- Melen ---
		jpa.citoyen.create("Essama", "Paul", "", "Melen", Sexe.M, LocalDate.of(1972, 5, 22), null,
				SituationMatrimoniale.MARIE, StatutCitoyen.ARCHIVE);
		jpa.citoyen.create("Amougou", "Sylvain", "", "Melen", Sexe.M, LocalDate.of(1989, 2, 28), null,
				SituationMatrimoniale.CELIBATAIRE, StatutCitoyen.ACTIF);
		jpa.citoyen.create("Mballa", "Jeanne", "", "Melen", Sexe.F, LocalDate.of(1994, 11, 11), null,
				SituationMatrimoniale.CELIBATAIRE, StatutCitoyen.ACTIF);
		jpa.citoyen.create("Biloa", "Pascaline", "", "Melen", Sexe.F, LocalDate.of(1968, 3, 21), null,
				SituationMatrimoniale.MARIE, StatutCitoyen.ACTIF);
		jpa.citoyen.create("Owona", "Christian", "", "Melen", Sexe.M, LocalDate.of(2001, 7, 19), null,
				SituationMatrimoniale.CELIBATAIRE, StatutCitoyen.ACTIF);
		jpa.citoyen.create("Zang", "Pierre", "", "Melen", Sexe.M, LocalDate.of(1950, 9, 30), null,
				SituationMatrimoniale.DIVORCE, StatutCitoyen.ARCHIVE);

		// --- Nlongkak ---
		jpa.citoyen.create("Fouda", "Amina", "", "Nlongkak", Sexe.F, LocalDate.of(1999, 8, 30), null,
				SituationMatrimoniale.CELIBATAIRE, StatutCitoyen.ACTIF);
		jpa.citoyen.create("Mvondo", "Alain", "", "Nlongkak", Sexe.M, LocalDate.of(1981, 4, 3), null,
				SituationMatrimoniale.MARIE, StatutCitoyen.ACTIF);
		jpa.citoyen.create("Bikolo", "Bernadette", "", "Nlongkak", Sexe.F, LocalDate.of(1974, 10, 15),
				null, SituationMatrimoniale.DIVORCE, StatutCitoyen.ACTIF);
		jpa.citoyen.create("Nanga", "Alice", "", "Nlongkak", Sexe.F, LocalDate.of(1996, 1, 1), null,
				SituationMatrimoniale.CELIBATAIRE, StatutCitoyen.ACTIF);
		jpa.citoyen.create("Assoumou", "Felix", "", "Nlongkak", Sexe.M, LocalDate.of(1987, 6, 14), null,
				SituationMatrimoniale.MARIE, StatutCitoyen.ACTIF);
		jpa.citoyen.create("Kotto", "Emmanuel", "", "Nlongkak", Sexe.M, LocalDate.of(1993, 12, 25), null,
				SituationMatrimoniale.CELIBATAIRE, StatutCitoyen.ACTIF);
		jpa.citoyen.create("Mekongo", "Luc", "", "Nlongkak", Sexe.M, LocalDate.of(1948, 5, 2), null,
				SituationMatrimoniale.VEUF, StatutCitoyen.ARCHIVE);
	}
}