🎓 GUIDE D'UTILISATION - ARCHITECTURE MODULAIRE JAKARTA EE
=========================================================

## EXEMPLE 1 : Ajouter une nouvelle page

### Étape 1 : Créer le module JSP
```jsp
<!-- src/main/webapp/WEB-INF/jsp/modules/rapport/statistiques.jsp -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div class="page-header">
  <h1>Rapports & Statistiques</h1>
  <p>Analyses et exports</p>
</div>

<!-- Contenu spécifique -->
<div class="card">
  <div class="card-title">Rapport mensuel</div>
  <!-- ... -->
</div>
```

### Étape 2 : Créer la Servlet
```java
// src/main/java/com/app/controller/rapport/RapportExportServlet.java
package com.app.controller.rapport;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/rapport/statistiques")
public class RapportExportServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO: Charger les statistiques depuis les DAOs
        // request.setAttribute("stats", rapportDAO.getMonthlyStats());
        
        // Injecter la vue
        request.setAttribute("view", "/WEB-INF/jsp/modules/rapport/statistiques.jsp");
        
        // Transférer au layout maître
        request.getRequestDispatcher("/WEB-INF/jsp/layouts/base-layout.jsp")
               .forward(request, response);
    }
}
```

### Étape 3 : Ajouter le lien dans la sidebar
```jsp
<!-- WEB-INF/jsp/includes/sidebar.jsp -->
<a class="nav-item" href="${pageContext.request.contextPath}/rapport/statistiques">
  <i class="ti ti-chart-bar" aria-hidden="true"></i>
  Rapports
</a>
```

✅ Terminé ! L'URL `/rapport/statistiques` est maintenant accessible avec layout complet.

---

## EXEMPLE 2 : Implémenter une Servlet complète (avec DAO)

### Scenario : Afficher la liste des citoyens avec pagination

```java
// CitoyenListeServlet.java
@WebServlet("/citoyen/liste")
public class CitoyenListeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Récupérer les paramètres
        int page = Integer.parseInt(request.getParameter("page") != null ? 
                                    request.getParameter("page") : "1");
        String search = request.getParameter("search");
        String statut = request.getParameter("statut");
        
        // 2. Appeler le DAO
        CitoyenDAO citoyenDAO = new CitoyenDAO();  // Injecter via CDI en prod
        List<Citoyen> citoyens = citoyenDAO.findWithPagination(
            page, 10, search, statut
        );
        long totalCount = citoyenDAO.count(search, statut);
        
        // 3. Ajouter les données à la requête
        request.setAttribute("citoyens", citoyens);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", 10);
        request.setAttribute("totalPages", (int) Math.ceil(totalCount / 10.0));
        
        // 4. Injecter la vue et transférer
        request.setAttribute("view", "/WEB-INF/jsp/modules/citoyen/liste.jsp");
        request.getRequestDispatcher("/WEB-INF/jsp/layouts/base-layout.jsp")
               .forward(request, response);
    }
}
```

### JSP correspondante avec données dynamiques
```jsp
<!-- citoyen/liste.jsp -->
<%@ page import="java.util.List" %>
<%@ page import="com.app.model.Citoyen" %>

<div class="page-header">
  <h1>Gestion des citoyens</h1>
  <p><%= request.getAttribute("totalCount") %> citoyens enregistrés</p>
</div>

<!-- Tableau avec données dynamiques -->
<table>
  <tbody>
    <% 
      List<Citoyen> citoyens = (List<Citoyen>) request.getAttribute("citoyens");
      for (Citoyen citoyen : citoyens) {
    %>
    <tr>
      <td><%= citoyen.getNin() %></td>
      <td><strong><%= citoyen.getNom() %> <%= citoyen.getPrenom() %></strong></td>
      <td><%= citoyen.getDateNaissance() %></td>
      <td><a href="${pageContext.request.contextPath}/citoyen/detail?id=<%= citoyen.getId() %>">
            <i class="ti ti-eye"></i>
          </a></td>
    </tr>
    <% } %>
  </tbody>
</table>

<!-- Pagination -->
<div style="display:flex;gap:6px">
  <% int currentPage = (Integer) request.getAttribute("currentPage");
     int totalPages = (Integer) request.getAttribute("totalPages"); %>
  <% for (int i = 1; i <= totalPages; i++) { %>
    <a href="?page=<%= i %>" class="btn <%= i == currentPage ? "btn-primary" : "btn-ghost" %> btn-sm">
      <%= i %>
    </a>
  <% } %>
</div>
```

---

## EXEMPLE 3 : Utiliser les modales globales

### Ouvrir une modale depuis un bouton
```jsp
<!-- Dans n'importe quel module JSP -->
<button class="btn btn-primary" onclick="openModal('citoyen')">
  <i class="ti ti-plus"></i> Nouveau citoyen
</button>
```

### Ajouter une nouvelle modale
```jsp
<!-- Ajouter dans footer.jsp -->
<div class="modal-backdrop" id="modal-demande" style="display:none" role="dialog">
  <div class="modal">
    <div class="modal-header">
      <h2 class="modal-title">Soumettre une demande</h2>
      <button class="icon-btn" onclick="closeModal('demande')">
        <i class="ti ti-x"></i>
      </button>
    </div>
    <div class="modal-body">
      <!-- Formulaire -->
    </div>
    <div class="modal-footer">
      <button class="btn btn-ghost" onclick="closeModal('demande')">Annuler</button>
      <button class="btn btn-primary" onclick="saveDemande()">Soumettre</button>
    </div>
  </div>
</div>

<script>
function saveDemande() {
  // Récupérer les données du formulaire
  const typeDemande = document.getElementById('fTypeDemande').value;
  // ... valider ...
  
  // Fermer la modale
  closeModal('demande');
  
  // Afficher un toast
  showToast('Demande soumise avec succès', 'success');
}
</script>
```

---

## EXEMPLE 4 : Afficher des toasts (notifications)

```javascript
// Dans n'importe quel code JavaScript ou JSP

// Toast success
showToast('Opération réussie!', 'success');

// Toast error
showToast('Une erreur est survenue', 'error');

// Toast warning
showToast('Attention: données modifiées', 'warning');

// Toast info
showToast('Opération en cours...', 'info');
```

---

## EXEMPLE 5 : Toggler le thème (clair/sombre)

```javascript
// Bouton dans topbar.jsp
<button onclick="toggleTheme()">
  <i id="themeIcon" class="ti ti-moon"></i>
</button>

// Fonction dans app-core.js (déjà implémentée)
function toggleTheme() {
  const html = document.documentElement;
  const isDark = html.getAttribute('data-theme') === 'dark';
  html.setAttribute('data-theme', isDark ? 'light' : 'dark');
  document.getElementById('themeIcon').className = isDark ? 'ti ti-moon' : 'ti ti-sun';
  if (typeof initCharts === 'function') initCharts();
}
```

---

## EXEMPLE 6 : Initialiser les graphiques au chargement

```jsp
<!-- Dans dashboard/index.jsp -->
<script>
  // Les graphiques se chargent automatiquement si charts-init.js est présent
  document.addEventListener('DOMContentLoaded', () => {
    if (typeof initCharts === 'function') {
      initCharts();
    }
  });
</script>

<!-- Les données sont injectées via Chart.js -->
<canvas id="chartActes" aria-label="Graphique des actes"></canvas>
<canvas id="chartTypes" aria-label="Répartition des types"></canvas>
```

---

## EXEMPLE 7 : Créer un formulaire POST

### JSP du formulaire
```jsp
<!-- citoyen/formulaire.jsp -->
<div class="page-header">
  <h1>Enregistrer un citoyen</h1>
</div>

<div class="card">
  <form method="POST" action="${pageContext.request.contextPath}/citoyen/formulaire">
    <div class="form-group">
      <label class="form-label">Nom *</label>
      <input class="form-control" type="text" name="nom" required>
    </div>
    
    <div class="form-group">
      <label class="form-label">Prénom *</label>
      <input class="form-control" type="text" name="prenom" required>
    </div>
    
    <div class="form-group">
      <label class="form-label">Date de naissance *</label>
      <input class="form-control" type="date" name="dateNaissance" required>
    </div>
    
    <div class="form-actions">
      <button type="submit" class="btn btn-primary">Enregistrer</button>
      <button type="reset" class="btn btn-ghost">Réinitialiser</button>
    </div>
  </form>
</div>
```

### Servlet correspondante
```java
@WebServlet("/citoyen/formulaire")
public class CitoyenFormServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Afficher le formulaire
        request.setAttribute("view", "/WEB-INF/jsp/modules/citoyen/formulaire.jsp");
        request.getRequestDispatcher("/WEB-INF/jsp/layouts/base-layout.jsp")
               .forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Traiter le formulaire
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String dateNaissance = request.getParameter("dateNaissance");
        
        // Validation
        if (nom == null || nom.isEmpty()) {
            request.setAttribute("error", "Le nom est obligatoire");
            doGet(request, response);
            return;
        }
        
        // Créer l'entité
        Citoyen citoyen = new Citoyen();
        citoyen.setNom(nom);
        citoyen.setPrenom(prenom);
        citoyen.setDateNaissance(LocalDate.parse(dateNaissance));
        
        // Persister
        CitoyenDAO dao = new CitoyenDAO();
        dao.save(citoyen);
        
        // Rediriger vers la liste
        response.sendRedirect(request.getContextPath() + "/citoyen/liste");
    }
}
```

---

## PATTERN RECOMMANDÉ : Service + DAO

```java
// 1. Interface DAO
public interface ICitoyenDAO {
    Citoyen findById(Long id);
    List<Citoyen> findWithPagination(int page, int size, String search, String statut);
    long count(String search, String statut);
    void save(Citoyen citoyen);
    void update(Citoyen citoyen);
    void delete(Long id);
}

// 2. Implémentation DAO (avec JPA)
@Stateless
public class CitoyenDAO implements ICitoyenDAO {
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public Citoyen findById(Long id) {
        return em.find(Citoyen.class, id);
    }
    
    @Override
    public List<Citoyen> findWithPagination(int page, int size, String search, String statut) {
        TypedQuery<Citoyen> query = em.createQuery(
            "SELECT c FROM Citoyen c WHERE (" +
            "c.nom LIKE :search OR c.prenom LIKE :search OR c.nin LIKE :search) " +
            "AND (:statut IS NULL OR c.statut = :statut) " +
            "ORDER BY c.nom", Citoyen.class);
        
        query.setParameter("search", "%" + (search != null ? search : "") + "%");
        query.setParameter("statut", statut != null ? StatutCitoyen.valueOf(statut) : null);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        
        return query.getResultList();
    }
    
    @Override
    public void save(Citoyen citoyen) {
        em.persist(citoyen);
    }
}

// 3. Service (logique métier)
@Stateless
public class CitoyenService {
    @Inject
    private CitoyenDAO citoyenDAO;
    
    public List<Citoyen> rechercherCitoyens(String search, String statut, int page) {
        // Validation métier
        if (search != null && search.length() < 2) {
            throw new IllegalArgumentException("Recherche trop courte");
        }
        
        // Appel DAO
        return citoyenDAO.findWithPagination(page, 10, search, statut);
    }
}

// 4. Servlet (contrôleur)
@WebServlet("/citoyen/liste")
public class CitoyenListeServlet extends HttpServlet {
    @Inject
    private CitoyenService citoyenService;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String search = request.getParameter("search");
        String statut = request.getParameter("statut");
        int page = Integer.parseInt(request.getParameter("page") != null ? 
                                    request.getParameter("page") : "1");
        
        List<Citoyen> citoyens = citoyenService.rechercherCitoyens(search, statut, page);
        
        request.setAttribute("citoyens", citoyens);
        request.setAttribute("view", "/WEB-INF/jsp/modules/citoyen/liste.jsp");
        request.getRequestDispatcher("/WEB-INF/jsp/layouts/base-layout.jsp")
               .forward(request, response);
    }
}
```

---

## CHECKLIST POUR AJOUTER UN NOUVEAU MODULE

- [ ] Créer la Servlet dans `src/main/java/com/app/controller/{module}/`
- [ ] Implémenter `doGet()` et/ou `doPost()`
- [ ] Créer les fichiers JSP dans `src/main/webapp/WEB-INF/jsp/modules/{module}/`
- [ ] Mapper la route avec `@WebServlet("/path")`
- [ ] Ajouter le lien dans `sidebar.jsp` si nécessaire
- [ ] Utiliser `base-layout.jsp` via `setAttribute("view", "...")`
- [ ] Tester la compilation : `mvn clean compile`
- [ ] Tester l'accès URL : `http://localhost:8080/app/path`
- [ ] Vérifier la console (F12) pour les erreurs 404

---

🎯 Cette architecture est prête pour la croissance!
Ajouter des modules est devenu simple et systématique.
