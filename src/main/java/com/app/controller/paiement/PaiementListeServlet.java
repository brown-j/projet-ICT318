package com.app.controller.paiement;

import jakarta.persistence.EntityManager;
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
import com.app.jpa.model.Paiement;
import com.app.jpa.model.JPAEnum.ModePaiement;
import com.app.model.viewmodel.PaiementRow;

@WebServlet(value = "/paiement/liste", loadOnStartup = 1)
public class PaiementListeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        EntityManager em = JPAConfig.getEntityManager();

        try {
            // 1. Récupération via les bonnes propriétés de l'entité (demande et
            // officierCaissier)
            List<Paiement> registrePaiements = em.createQuery(
                    "SELECT p FROM Paiement p LEFT JOIN FETCH p.demande LEFT JOIN FETCH p.officierCaissier",
                    Paiement.class).getResultList();

            List<PaiementRow> paiementsRows = registrePaiements.stream()
                    .map(PaiementRow::new)
                    .collect(Collectors.toList());

            // Calcul de la caisse du jour (Filtre sur la date du jour)
            long caisseDuJour = registrePaiements.stream()
                    .filter(p -> p.getDatePaiement() != null
                            && p.getDatePaiement().toLocalDate().equals(LocalDate.now()))
                    .mapToLong(p -> p.getMontant() != null ? p.getMontant().longValue() : 0L)
                    .sum();

            Gson gson = new Gson();
            String paiementsJson = gson.toJson(paiementsRows);

            // On pousse les données vers la JSP
            request.setAttribute("paiementsJson", paiementsJson);
            request.setAttribute("totalCount", paiementsRows.size());
            request.setAttribute("caisseDuJour", String.format("%,d", caisseDuJour));

            // 2. Extraction du référentiel des modes pour le filtre HTML dynamique de la
            // JSP
            List<ModePaiement> listeModes = java.util.Arrays.asList(ModePaiement.values());
            request.setAttribute("listeModes", listeModes);

            // 3. Routage vers le layout global
            request.setAttribute("view", "/WEB-INF/jsp/modules/paiement/liste-paiement.jsp");
            request.getRequestDispatcher("/WEB-INF/jsp/layouts/base-layout.jsp").forward(request, response);

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}