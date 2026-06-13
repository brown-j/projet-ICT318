package com.app.controller.officier;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;

import com.app.jpa.config.JPAConfig;
import com.app.jpa.dao.JPADao;
import com.app.jpa.model.OfficierEtatCivil;
import com.app.jpa.model.JPAEnum.Role;
import com.app.jpa.model.JPAEnum.StatutOfficier;
import com.app.util.HashUtil;

@WebServlet("/officier/formulaire")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1, // 1 Mo
        maxFileSize = 1024 * 1024 * 5, // 5 Mo maximum par fichier
        maxRequestSize = 1024 * 1024 * 10 // 10 Mo maximum par requête globale
)
public class OfficierFormServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Récupération des paramètres du formulaire
        String idParam = request.getParameter("id");
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String titre = request.getParameter("titre");
        String service = request.getParameter("service");
        String matricule = request.getParameter("matricule");
        String datePriseFonctionParam = request.getParameter("datePriseFonction");
        String email = request.getParameter("email");
        String telephone = request.getParameter("telephone");
        String motDePasse = request.getParameter("motDePasse");
        String roleParam = request.getParameter("role");
        String statutParam = request.getParameter("statut");

        boolean isModification = (idParam != null && !idParam.trim().isEmpty());
        EntityManager em = null;
        EntityTransaction tx = null;

        try {
            em = JPAConfig.getEntityManager();
            tx = em.getTransaction();
            tx.begin();

            // Initialisation de ton mini-ORM Prisma-like
            JPADao jpa = new JPADao(em);

            // Variable qui contiendra notre entité finale (créée ou récupérée) pour le
            // traitement du fichier
            OfficierEtatCivil officier;

            if (isModification) {
                // 🔄 MODE MODIFICATION : On utilise le delegate pour charger l'entité managée
                officier = jpa.officier.findUnique(Long.parseLong(idParam));
                if (officier == null) {
                    throw new ServletException("Officier introuvable pour l'ID : " + idParam);
                }

                // Hydratation des modifications
                officier.setNom(nom.trim().toUpperCase());
                officier.setPrenom(prenom.trim());
                officier.setTitre(titre.trim());
                officier.setService(service.trim());
                officier.setMatricule(matricule.trim().toUpperCase());
                officier.setDatePriseFonction(LocalDate.parse(datePriseFonctionParam));
                officier.setEmail(email.trim().toLowerCase());
                officier.setTelephone(telephone != null && !telephone.trim().isEmpty() ? telephone.trim() : null);
                officier.setRole(Role.valueOf(roleParam));
                officier.setStatut(StatutOfficier.valueOf(statutParam));

                // Gestion intelligente du mot de passe en modification (uniquement si modifié)
                if (motDePasse != null && !motDePasse.trim().isEmpty()) {
                    officier.setMotDePasse(HashUtil.hashPassword(motDePasse.trim()));
                }

                // Mise à jour via le delegate parent (déclenche la validation réflexive et
                // l'audit)
                jpa.officier.update(officier);

            } else {
                // ✨ MODE CRÉATION : On délègue tout à la méthode métier blindée de ton Delegate
                // Elle valide l'unicité du mot de passe, l'absence d'un second super-admin et
                // hache le mot de passe.
                officier = jpa.officier.create(
                        matricule,
                        nom.trim().toUpperCase(),
                        prenom.trim(),
                        telephone,
                        titre.trim(),
                        service.trim(),
                        email.trim().toLowerCase(),
                        motDePasse, // Passé en clair, ton delegate s'occupe du hachage !
                        LocalDate.parse(datePriseFonctionParam),
                        Role.valueOf(roleParam));

                // On applique manuellement le statut puisque ton delegate.create() l'omettait
                if (statutParam != null) {
                    officier.setStatut(StatutOfficier.valueOf(statutParam));
                }
            }

            // 4. Traitement du fichier de signature numérique (Commun à la création et
            // modification)
            Part filePart = request.getPart("signatureFile");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String extension = fileName.substring(fileName.lastIndexOf("."));
                String uniqueImageName = "sig_" + officier.getMatricule() + "_" + System.currentTimeMillis()
                        + extension;
                officier.setSignatureNumerique(uniqueImageName);

                // Si on vient de le créer et qu'on modifie un champ post-persist (le nom du
                // fichier),
                // l'update garantit la bonne synchronisation de l'état.
                if (!isModification) {
                    jpa.officier.update(officier);
                }
            }

            tx.commit();

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            throw new ServletException("Erreur lors de l'enregistrement de l'officier via JPADao : " + e.getMessage(),
                    e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        // Redirection PRG (Post-Redirect-Get)
        response.sendRedirect(request.getContextPath() + "/officier/liste");
    }
}