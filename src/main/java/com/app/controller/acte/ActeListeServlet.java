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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.app.jpa.config.JPAConfig;
import com.app.jpa.dao.JPADao;
import com.app.jpa.model.ActeEtatCivil;
import com.app.jpa.model.Citoyen;
import com.app.jpa.model.OfficierEtatCivil;
import com.app.jpa.model.TypeActe;
import com.app.jpa.model.JPAEnum.CategorieActe;
import com.app.jpa.model.JPAEnum.Sexe;
import com.app.jpa.model.JPAEnum.StatutActe;
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
        // Initialisation du DAO pour bénéficier du cache
        JPADao jpa = new JPADao(em);

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
            String mode = request.getParameter("mode");

            ActeEtatCivil actePourFormulaire = null;
            boolean isReadOnly = false;

            if (idStr != null && !idStr.trim().isEmpty()) {
                Long id = Long.parseLong(idStr);
                actePourFormulaire = em.find(ActeEtatCivil.class, id);

                if (actePourFormulaire != null) {
                    request.setAttribute("autoOpenModal", true);
                    if ("preview".equalsIgnoreCase(mode)) {
                        isReadOnly = true;
                    }
                }
            }

            if (actePourFormulaire == null) {
                actePourFormulaire = new ActeEtatCivil();
                if ("create".equalsIgnoreCase(mode)) {
                    request.setAttribute("autoOpenModal", true);
                }
            }

            // 3. Extraction des référentiels d'aide à la saisie (Inversion BDD -> RAM pour
            // TypeActe)
            List<TypeActe> listeTypes = new ArrayList<>(jpa.typeActe.getCache().values());
            List<Citoyen> listeCitoyens = em.createQuery("SELECT c FROM Citoyen c", Citoyen.class).getResultList();
            List<OfficierEtatCivil> listeOfficiers = em
                    .createQuery("SELECT o FROM OfficierEtatCivil o", OfficierEtatCivil.class).getResultList();

            // 4. Génération du formulaire via la Factory mise à jour
            String formulaireHtml = ActeEtatCivilFormFactory.genererHtml(
                    actePourFormulaire,
                    listeTypes,
                    listeCitoyens,
                    listeOfficiers,
                    request.getContextPath() + "/acte/formulaire",
                    isReadOnly);

            // 5. Logique de la Modale Globale Centralisée
            String modalTitle;
            if (isReadOnly) {
                modalTitle = "Consulter l'acte n° "
                        + (actePourFormulaire.getNumeroActe() != null ? actePourFormulaire.getNumeroActe() : "");
            } else if (actePourFormulaire.getId() != null) {
                modalTitle = "Modifier l'acte n° " + actePourFormulaire.getNumeroActe();
            } else {
                modalTitle = "Dresser un nouvel acte civil";
            }

            request.setAttribute("modalTitle", modalTitle);
            request.setAttribute("modalContent", formulaireHtml);

            // Routage
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
        EntityManager em = JPAConfig.getEntityManager();

        try {
            JPADao jpa = new JPADao(em);
            if (jpa.acte.count() > 0)
                return;

            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();

                System.out.println(">>> Insertion des données fictives via le mini-ORM JPADao...");

                // 1. Création des citoyens mocks
                Citoyen c1 = jpa.citoyen.create("Amougou", "Sylvain", "", "Melen", Sexe.M, LocalDate.of(1989, 2, 28),
                        null, null, null);
                Citoyen c2 = jpa.citoyen.create("Atangana", "Dieudonné", "", "Bastos", Sexe.M,
                        LocalDate.of(1963, 4, 12), null, null, null);
                Citoyen c3 = jpa.citoyen.create("Ona", "Samuel", "", "Bastos", Sexe.M, LocalDate.of(1940, 2, 14),
                        null, null, null);
                Citoyen c4 = jpa.citoyen.create("Bella", "Chantal", "", "Nlongkak", Sexe.F,
                        LocalDate.of(1988, 5, 18), null, null, null);

                // 2. Création de l'officier mock
                OfficierEtatCivil officier = jpa.officier.create("", "Etoa", "Jean-Marie", "677177877",
                        "2ème Adjoint", "Service Civil", null, "Etoa@1234", null, null);

                // 3. Récupération des configurations typées depuis ton cache mémoire global
                TypeActe tNaissance = jpa.typeActe.findInCache("ACTE_NAISS");
                TypeActe tMariage = jpa.typeActe.findInCache("ACTE_MARI");
                TypeActe tDeces = jpa.typeActe.findInCache("ACTE_DECES");

                // Fallback de sécurité au cas où le servlet de configuration n'a pas encore
                // fini son init()
                if (tNaissance == null) {
                    tNaissance = jpa.typeActe.saveOrUpdate("ACTE_NAISS", "Acte de Naissance (Souche)", 2000,
                            "naissance_souche.jsp", CategorieActe.ACTE, "");
                    tMariage = jpa.typeActe.saveOrUpdate("EXTR_MARI", "Acte de Mariage", 5000, "mariage_souche.jsp",
                            CategorieActe.EXTRAIT, "");
                    tDeces = jpa.typeActe.saveOrUpdate("ACTE_DECES", "Acte de Décès", 0, "deces_souche.jsp",
                            CategorieActe.ACTE, "");
                }

                // 4. Dressage des actes civils d'essais connectés aux entités
                jpa.acte.create("NAI-2025-00418", tNaissance, c1, officier, LocalDate.of(2025, 2, 10),
                        LocalDate.of(2025, 2, 28), "Mairie Ydé III", StatutActe.DELIVRE, "doc_00418.pdf");
                jpa.acte.create("MAR-2026-10552", tMariage, c2, officier, LocalDate.of(2026, 1, 15),
                        LocalDate.of(2026, 1, 20), "Bastos", StatutActe.DELIVRE, "doc_10552.pdf");
                jpa.acte.create("DEC-2026-30119", tDeces, c3, officier, LocalDate.of(2026, 5, 14),
                        LocalDate.of(2026, 5, 18), "Hôpital Central", StatutActe.DELIVRE, "doc_30119.pdf");
                jpa.acte.create("NAI-2026-00984", tNaissance, c4, officier, LocalDate.of(2026, 6, 1),
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