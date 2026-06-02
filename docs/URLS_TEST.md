🧪 URLS DE TEST - ARCHITECTURE MODULAIRE
==========================================

## DÉMARRAGE DE L'APPLICATION

Après le déploiement du WAR (mon-premier-projet-1.0-SNAPSHOT.war) sur Tomcat:

```
http://localhost:8080/mon-premier-projet/
```

---

## ROUTES DISPONIBLES (IMPLÉMENTÉES)

### Dashboard
```
✅ GET  http://localhost:8080/mon-premier-projet/dashboard
   Affiche: KPIs, graphiques, demandes récentes
   View: /WEB-INF/jsp/modules/dashboard/index.jsp
```

### Citoyens
```
✅ GET  http://localhost:8080/mon-premier-projet/citoyen/liste
   Affiche: Tableau des citoyens avec pagination
   View: /WEB-INF/jsp/modules/citoyen/liste.jsp
   Params: ?search=nom&statut=ACTIF&page=1

🔄 GET  http://localhost:8080/mon-premier-projet/citoyen/formulaire
   Formulaire de création (TODO: implémentation)

🔄 GET  http://localhost:8080/mon-premier-projet/citoyen/detail?id=1
   Profil détaillé d'un citoyen (TODO: implémentation)
```

### Actes civils
```
🔄 GET  http://localhost:8080/mon-premier-projet/acte/liste
   Vue: /WEB-INF/jsp/modules/acte/liste.jsp
   (Contenu préparé, besoin données dynamiques)

🔄 GET  http://localhost:8080/mon-premier-projet/acte/formulaire
   Formulaire de création d'acte

🔄 GET  http://localhost:8080/mon-premier-projet/acte/pdf?id=1
   Télécharge le PDF de l'acte
```

### Demandes administratives
```
🔄 GET  http://localhost:8080/mon-premier-projet/demande/liste
   Vue: /WEB-INF/jsp/modules/demande/liste.jsp
   (Contenu préparé, besoin données dynamiques)

🔄 GET  http://localhost:8080/mon-premier-projet/demande/traitement?id=89
   Formulaire de traitement d'une demande

🔄 POST http://localhost:8080/mon-premier-projet/demande/traitement
   Valide/rejette une demande
```

### Paiements
```
🔄 GET  http://localhost:8080/mon-premier-projet/paiement/liste
   Vue: /WEB-INF/jsp/modules/paiement/historique.jsp
   (Contenu préparé, besoin données dynamiques)

🔄 GET  http://localhost:8080/mon-premier-projet/paiement/caisse
   Formulaire d'enregistrement de paiement

🔄 POST http://localhost:8080/mon-premier-projet/paiement/caisse
   Enregistre un paiement
```

### Agenda / Rendez-vous
```
🔄 GET  http://localhost:8080/mon-premier-projet/agenda/rendez-vous
   Vue calendrier des RDV

🔄 POST http://localhost:8080/mon-premier-projet/agenda/rendez-vous
   Crée un nouveau RDV

🔄 POST http://localhost:8080/mon-premier-projet/agenda/rendez-vous/statut?id=1
   Met à jour le statut d'un RDV
```

### Pièces jointes
```
🔄 POST http://localhost:8080/mon-premier-projet/piece/upload
   Télécharge un fichier (multipart)

🔄 GET  http://localhost:8080/mon-premier-projet/piece/download?id=1
   Télécharge un fichier depuis le serveur
```

### Officiers
```
🔄 GET  http://localhost:8080/mon-premier-projet/officier/liste
   Annuaire du personnel

🔄 GET  http://localhost:8080/mon-premier-projet/officier/formulaire
   Formulaire de création de compte

🔄 POST http://localhost:8080/mon-premier-projet/officier/formulaire
   Crée un compte officier
```

### Rapports
```
🔄 GET  http://localhost:8080/mon-premier-projet/rapport/export
   Sélection des paramètres d'export

🔄 POST http://localhost:8080/mon-premier-projet/rapport/export
   Génère et télécharge le rapport (Excel/PDF)
```

### Paramètres
```
🔄 GET  http://localhost:8080/mon-premier-projet/parametre/configuration
   Formulaire de configuration du système

🔄 POST http://localhost:8080/mon-premier-projet/parametre/configuration
   Met à jour les paramètres (types d'actes, tarifs, délais)
```

### Authentication
```
🔄 GET  http://localhost:8080/mon-premier-projet/login
   Affiche le formulaire d'authentification

🔄 POST http://localhost:8080/mon-premier-projet/login
   Traite la connexion d'un officier

🔄 GET  http://localhost:8080/mon-premier-projet/logout
   Déconnecte l'utilisateur et détruit la session
```

---

## RESSOURCES STATIQUES (ACCÈS PUBLIC)

```
✅ GET  http://localhost:8080/mon-premier-projet/resources/css/mairie-civic.css
   Feuille de style CSS (40 KB)

✅ GET  http://localhost:8080/mon-premier-projet/resources/js/app-core.js
   Logique UI: modales, toasts, thème (2.5 KB)

✅ GET  http://localhost:8080/mon-premier-projet/resources/js/charts-init.js
   Graphiques Chart.js (2.8 KB)
```

---

## TESTS FONCTIONNELS

### 1️⃣ TEST NAVIGATION
```
1. Accéder à http://localhost:8080/mon-premier-projet/
   ✓ Redirige vers /dashboard
   
2. Vérifier le chargement des ressources (F12 Network)
   ✓ mairie-civic.css (HTTP 200)
   ✓ app-core.js (HTTP 200)
   ✓ charts-init.js (HTTP 200)
   
3. Cliquer sur les liens de la sidebar
   ✓ URL change (pas de fragment # ou setPage())
   ✓ Page se recharge complètement
   ✓ Contenu change correctement
```

### 2️⃣ TEST GRAPHIQUES
```
GET http://localhost:8080/mon-premier-projet/dashboard
   ✓ Les 2 graphiques s'affichent (chartActes + chartTypes)
   ✓ Pas d'erreur console (F12 Console)
   ✓ Graphiques responsifs
```

### 3️⃣ TEST THÈME
```
1. Cliquer sur l'icône lune (topbar)
   ✓ Fond passe au thème sombre
   ✓ Texte reste lisible
   ✓ Graphiques se réajustent
   ✓ Icône change (lune → soleil)
   
2. Cliquer sur le soleil
   ✓ Fond revient au clair
   ✓ Icône revient à lune
```

### 4️⃣ TEST MODALES
```
1. GET http://localhost:8080/mon-premier-projet/dashboard
2. Cliquer sur "Nouveau citoyen"
   ✓ Modale s'ouvre
   ✓ Fond grisé
   ✓ Focus sur le premier input
   
3. Cliquer sur la croix
   ✓ Modale se ferme
   
4. Cliquer en dehors de la modale
   ✓ Modale se ferme
   
5. Appuyer sur ESC
   ✓ Modale se ferme
```

### 5️⃣ TEST TOASTS
```
Depuis la console du navigateur (F12):
   showToast('Test success', 'success');
   showToast('Test error', 'error');
   showToast('Test warning', 'warning');
   showToast('Test info', 'info');
   
   ✓ Toast s'affiche en haut à droite
   ✓ Disparaît après 3.5 secondes automatiquement
   ✓ Bouton X ferme manuellement
```

### 6️⃣ TEST TABLEAU CITOYENS
```
GET http://localhost:8080/mon-premier-projet/citoyen/liste
   ✓ Tableau s'affiche avec données (4 citoyens)
   ✓ Pagination fonctionne (boutons 1, 2, 3)
   ✓ Colonnes: NIN, Nom, Date, Quartier, Situation, Statut, Actions
   ✓ Boutons "Voir" et "Modifier" sont cliquables
```

### 7️⃣ TEST PAGINATION
```
GET http://localhost:8080/mon-premier-projet/citoyen/liste?page=2
   ✓ Affiche la page 2
   ✓ Paramètre ?page= dans l'URL
   ✓ Bouton page 2 surlighté
```

---

## CHECKLIST DE DÉPLOIEMENT

Avant de mettre en production:

- [ ] Compiler le projet sans erreurs
  ```
  mvn clean compile -DskipTests
  ```

- [ ] Générer le WAR
  ```
  mvn clean package -DskipTests
  ```

- [ ] Vérifier le contenu du WAR
  ```
  unzip -l target/mon-premier-projet-1.0-SNAPSHOT.war | grep jsp
  ```

- [ ] Copier le WAR vers Tomcat
  ```
  cp target/mon-premier-projet-1.0-SNAPSHOT.war /path/to/tomcat/webapps/
  ```

- [ ] Redémarrer Tomcat
  ```
  /path/to/tomcat/bin/shutdown.sh
  /path/to/tomcat/bin/startup.sh
  ```

- [ ] Vérifier les logs
  ```
  tail -f /path/to/tomcat/logs/catalina.out
  ```

- [ ] Tester l'accès
  ```
  curl -I http://localhost:8080/mon-premier-projet/
  ```

---

## DEBUGGING - ERREURS COUANTES

### 404 sur CSS/JS
**Symptôme**: Ressources CSS/JS ne chargent pas
**Cause**: Chemin incorrect dans head.jsp

**Solution**:
```jsp
<!-- ❌ FAUX -->
<link rel="stylesheet" href="/resources/css/mairie-civic.css">

<!-- ✅ CORRECT -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mairie-civic.css">
```

### Modales ne s'ouvrent pas
**Symptôme**: Clic sur bouton mais rien ne se passe
**Cause**: app-core.js non chargé ou identifiant de modale incorrect

**Solution**:
1. Vérifier que app-core.js est chargé (F12 Network)
2. Vérifier que `id="modal-citoyen"` existe dans footer.jsp
3. Vérifier que `openModal('citoyen')` est appelé (console.log())

### Graphiques ne s'affichent pas
**Symptôme**: Canvas vide, pas de graphique
**Cause**: charts-init.js non chargé ou Chart.js CDN indisponible

**Solution**:
1. Vérifier que charts-init.js est chargé (F12 Network)
2. Vérifier que https://cdn.jsdelivr.net/npm/chart.js@4.4.0 charge (F12 Network)
3. Vérifier la console pour les erreurs Chart.js

### URL ne change pas après clic sur sidebar
**Symptôme**: Clic mais URL reste la même
**Cause**: Lien mal configuré dans sidebar.jsp

**Solution**:
```jsp
<!-- ❌ FAUX (ancien système) -->
<button onclick="setPage('citoyens')">Citoyens</button>

<!-- ✅ CORRECT (HTTP navigation) -->
<a href="${pageContext.request.contextPath}/citoyen/liste">Citoyens</a>
```

---

## PERFORMANCE

Temps de charge moyen:
- Dashboard: ~150ms (avec graphiques)
- Liste citoyens: ~80ms
- CSS: ~40KB
- JS: ~5KB total

Recommandations:
- Minifier CSS/JS en production
- Ajouter un cache HTTP
- Utiliser CDN pour Chart.js
- Optimiser les images/avatars

---

## NOTES

- Toutes les routes `🔄` sont des squelettes prêts pour implémentation
- Les routes `✅` sont partiellement ou complètement implémentées
- Voir GUIDE_UTILISATION.md pour des exemples d'implémentation
