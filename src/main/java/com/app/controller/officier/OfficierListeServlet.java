package com.app.controller.officier;

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
import com.app.jpa.model.OfficierEtatCivil;
import com.app.jpa.model.JPAEnum.Role;
import com.app.jpa.model.JPAEnum.StatutOfficier;
import com.app.model.viewmodel.OfficierRow;
import com.app.ui.OfficierFormFactory;

@WebServlet(value = "/officier/liste", loadOnStartup = 1)
public class OfficierListeServlet extends HttpServlet {
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
            // 1. Extraction et sérialisation de la liste des officiers
            List<OfficierEtatCivil> personnelMairie = em
                    .createQuery("SELECT o FROM OfficierEtatCivil o", OfficierEtatCivil.class)
                    .getResultList();

            // Transformation vers un ViewModel scannable par ton tableau JS
            List<OfficierRow> officiersRows = personnelMairie.stream()
                    .map(OfficierRow::new)
                    .collect(Collectors.toList());

            Gson gson = new Gson();
            String officiersJson = gson.toJson(officiersRows);
            request.setAttribute("officiersJson", officiersJson);
            request.setAttribute("totalCount", officiersRows.size());

            // 2. EXTRACTION DU PARAMÉTRAGE GÉNÉRIQUE (id & mode)
            String idStr = request.getParameter("id");
            String mode = request.getParameter("mode");

            OfficierEtatCivil officierPourFormulaire = null;
            boolean isReadOnly = false;

            if (idStr != null && !idStr.trim().isEmpty()) {
                Long id = Long.parseLong(idStr);
                officierPourFormulaire = em.find(OfficierEtatCivil.class, id);

                if (officierPourFormulaire != null) {
                    request.setAttribute("autoOpenModal", true);
                    if ("preview".equalsIgnoreCase(mode)) {
                        isReadOnly = true;
                    }
                }
            }

            if (officierPourFormulaire == null) {
                officierPourFormulaire = new OfficierEtatCivil();
                if ("create".equalsIgnoreCase(mode)) {
                    request.setAttribute("autoOpenModal", true);
                }
            }

            // 3. Génération dynamique du formulaire d'officier
            String actionUrl = request.getContextPath() + "/officier/formulaire";
            String formHtml = OfficierFormFactory.genererHtml(
                    officierPourFormulaire,
                    actionUrl,
                    isReadOnly);
            request.setAttribute("formulaireHtml", formHtml);

            // 4. Routage vers le layout maître
            request.setAttribute("view", "/WEB-INF/jsp/modules/officier/liste-officier.jsp");
            request.getRequestDispatcher("/WEB-INF/jsp/layouts/base-layout.jsp").forward(request, response);

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Initialise un jeu d'essai si la table est vide.
     * Note : Ton AppStartupListener crée déjà le SUPER_ADMIN,
     * donc cette méthode ajoutera simplement des collègues pour les tests.
     */
    private void initialiserDonneesSiVide() {
        EntityManager em = JPAConfig.getEntityManager();

        try {
            // On compte s'il y a d'autres agents en plus du Super-Admin racine
            Long countOfficiers = em.createQuery("SELECT COUNT(o) FROM OfficierEtatCivil o", Long.class)
                    .getSingleResult();

            // Si on a uniquement le super-admin (count == 1) ou plus, on n'ajoute pas de
            // doublons
            if (countOfficiers > 1)
                return;

            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                System.out.println("[OfficierListeServlet] Génération du personnel communal témoin...");

                JPADao jpa = new JPADao(em);
                insererMocksViaDAO(jpa);

                tx.commit();
                System.out.println("[OfficierListeServlet] Initialisation du personnel terminée.");
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
     * Ajoute des officiers et agents fictifs via ton JPADao
     */
    private void insererMocksViaDAO(JPADao jpa) {
        jpa.officier.create("", "Kondo", "Emilie", "699887766", "Mme l'Officier",
                "Service État Civil", "e.kondo@mairie.cm", "password121", LocalDate.of(2020, 1, 15), Role.OFFICIER);

        jpa.officier.create("", "Ewane", "Jean", "677554433", "M. l'Agent", "Bureau des Naissances",
                "j.ewane@mairie.cm", "password12", LocalDate.of(2022, 6, 1), Role.AGENT_SAISIE);

        jpa.officier.create("ADM-2026-003", "Tchakounté", "Paul", "655443322", "M. l'Administrateur",
                "Direction Informatique", "p.tchako@mairie.cm", "password123", LocalDate.of(2021, 3, 10), Role.ADMIN);
    }
}