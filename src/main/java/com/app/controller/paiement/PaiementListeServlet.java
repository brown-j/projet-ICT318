package com.app.controller.paiement;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.app.jpa.config.JPAConfig;
import com.app.jpa.dao.JPADao;
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

        // 💡 Instanciation de ton DAO
        JPADao dao = new JPADao(em);

        try {
            // 1. Récupération optimisée avec JOIN FETCH
            // (On utilise l'EM directement ici pour forcer le Fetch et éviter le problème
            // des requêtes N+1)
            List<Paiement> registrePaiements = em.createQuery(
                    "SELECT p FROM Paiement p LEFT JOIN FETCH p.demande LEFT JOIN FETCH p.officierCaissier",
                    Paiement.class).getResultList();

            List<PaiementRow> paiementsRows = registrePaiements.stream()
                    .map(PaiementRow::new)
                    .collect(Collectors.toList());

            // 2. Calcul de la caisse du jour via le DAO (Optimisé en SQL directement)
            LocalDateTime debutJournee = LocalDate.now().atStartOfDay(); // Aujourd'hui à 00:00:00
            LocalDateTime finJournee = LocalDate.now().atTime(LocalTime.MAX); // Aujourd'hui à 23:59:59

            BigDecimal caisseDuJour = dao.paiement.sumMontantByPeriode(debutJournee, finJournee);

            Gson gson = new Gson();
            String paiementsJson = gson.toJson(paiementsRows);

            // On pousse les données vers la JSP
            request.setAttribute("paiementsJson", paiementsJson);
            request.setAttribute("totalCount", paiementsRows.size());

            // Formatage du montant (Si caisseDuJour est null, cela devient 0)
            request.setAttribute("caisseDuJour", String.format("%,d", caisseDuJour.longValue()));

            // 3. Extraction du référentiel des modes pour le filtre HTML dynamique de la
            // JSP
            List<ModePaiement> listeModes = java.util.Arrays.asList(ModePaiement.values());
            request.setAttribute("listeModes", listeModes);

            // 4. Routage vers le layout global
            request.setAttribute("view", "/WEB-INF/jsp/modules/paiement/liste-paiement.jsp");
            request.getRequestDispatcher("/WEB-INF/jsp/layouts/base-layout.jsp").forward(request, response);

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}