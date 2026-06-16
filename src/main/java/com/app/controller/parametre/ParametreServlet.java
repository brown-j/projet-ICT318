package com.app.controller.parametre;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.app.jpa.config.JPAConfig;
import com.app.jpa.dao.JPADao;
import com.app.jpa.model.TypeActe;
import com.app.jpa.model.JPAEnum.CategorieActe;
import com.app.ui.TypeActeFormFactory;

@WebServlet(value = "/parametre/configuration", loadOnStartup = 1)
public class ParametreServlet extends HttpServlet {
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
        JPADao jpa = new JPADao(em);

        try {
            // 1. Extraction et sérialisation du catalogue pour le tableau dynamique
            List<TypeActe> catalogue = new ArrayList<>(jpa.typeActe.getCache().values());

            Gson gson = new Gson();
            String catalogueJson = gson.toJson(catalogue);
            request.setAttribute("catalogueJson", catalogueJson);
            request.setAttribute("totalCount", catalogue.size());

            // 2. EXTRACTION DU PARAMÉTRAGE GÉNÉRIQUE (code & mode)
            String code = request.getParameter("code");
            String mode = request.getParameter("mode");

            TypeActe typeActePourFormulaire = null;
            boolean isReadOnly = false;

            if (code != null && !code.trim().isEmpty()) {
                String codeCle = code.trim().toUpperCase();
                typeActePourFormulaire = em.find(TypeActe.class, codeCle);

                if (typeActePourFormulaire != null) {
                    request.setAttribute("autoOpenModal", true);
                    if ("preview".equalsIgnoreCase(mode)) {
                        isReadOnly = true;
                    }
                }
            }

            if (typeActePourFormulaire == null) {
                typeActePourFormulaire = new TypeActe();
                if ("create".equalsIgnoreCase(mode)) {
                    request.setAttribute("autoOpenModal", true);
                }
            }

            // 3. Génération dynamique du formulaire via sa Factory dédiée
            String actionUrl = request.getContextPath() + "/parametre/formulaire";
            String formHtml = TypeActeFormFactory.genererHtml(
                    typeActePourFormulaire,
                    actionUrl,
                    isReadOnly);

            // 4. Logique d'affichage de la Modale Globale Centralisée
            String modalTitle;
            if (isReadOnly) {
                modalTitle = "Détails de la configuration : " + (typeActePourFormulaire.getLibelle() != null
                        ? typeActePourFormulaire.getLibelle()
                        : "");
            } else if (typeActePourFormulaire.getCode() != null) {
                modalTitle = "Modifier la prestation : " + typeActePourFormulaire.getLibelle();
            } else {
                modalTitle = "Ajouter une nouvelle prestation au catalogue";
            }

            // Injection dans le réceptacle de base-layout.jsp
            request.setAttribute("modalTitle", modalTitle);
            request.setAttribute("modalContent", formHtml);

            // 5. Routage vers le layout maître de l'application
            request.setAttribute("view", "/WEB-INF/jsp/modules/parametre/liste-configuration.jsp");
            request.getRequestDispatcher("/WEB-INF/jsp/layouts/base-layout.jsp").forward(request, response);

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Initialise le catalogue de configuration de la mairie si la table est vide.
     */
    private void initialiserDonneesSiVide() {
        EntityManager em = JPAConfig.getEntityManager();
        JPADao jpa = new JPADao(em);

        try {
            if (jpa.typeActe.count() > 0)
                return;

            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                System.out.println(
                        "[ParametreServlet] Catalogue vide. Initialisation des prestations témoins de base...");

                insererViaDAO(jpa);

                tx.commit();
                System.out.println("[ParametreServlet] Initialisation du catalogue des paramètres terminée.");
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
     * Utilise le délégué JPADao pour charger le catalogue initial.
     * Ordre attendu du saveOrUpdate : code, libelle, tarif, templatePath,
     * categorieParent, description
     */
    private void insererViaDAO(JPADao jpa) {
        // --- SECTEUR NAISSANCE ---
        jpa.typeActe.saveOrUpdate("ACTE_NAISS", "Acte de Naissance (Souche)", 2000,
                "naissance_souche.jsp", CategorieActe.ACTE,
                "Enregistrement initial et conservation de la souche.");

        jpa.typeActe.saveOrUpdate("EXTR_NAISS", "Extrait d'Acte de Naissance", 1000,
                "naissance_extrait.jsp", CategorieActe.EXTRAIT,
                "Copie d'extrait certifiée conforme pour démarches courantes.");

        // --- SECTEUR MARIAGE ---
        jpa.typeActe.saveOrUpdate("ACTE_MARI", "Acte de Mariage (Livret)", 5000,
                "mariage_souche.jsp", CategorieActe.ACTE,
                "Établissement officiel de l'union civile.");

        jpa.typeActe.saveOrUpdate("CERT_CELIBAT", "Certificat de Célibat", 1500,
                "celibat.jsp", CategorieActe.CERTIFICAT,
                "Attestation de non-engagement matrimonial valide.");

        // --- SECTEUR DÉCÈS ---
        jpa.typeActe.saveOrUpdate("ACTE_DECES", "Acte de Décès", 0,
                "deces_souche.jsp", CategorieActe.ACTE,
                "Déclaration officielle de décès (Prestation municipale gratuite).");
    }
}