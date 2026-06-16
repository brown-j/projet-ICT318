<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.app.model.viewmodel.TypeDemandeViewModel" %>
<%@ page import="com.app.ui.PortalUiFactory" %>

<%
    // 1. Récupération des données brutes et du JSON issus de Gson dans la Servlet
    List<TypeDemandeViewModel> typesDemande = (List<TypeDemandeViewModel>) request.getAttribute("typesDemande");
    String typesDemandeJson = (String) request.getAttribute("typesDemandeJson");
    if (typesDemandeJson == null || typesDemandeJson.trim().isEmpty()) {
        typesDemandeJson = "[]"; // Sécurité anti-crash JS
    }
    
    String dossierHtml = (String) request.getAttribute("dossierHtml");
    String messageErreur = (String) request.getAttribute("erreurSuivi");
    
    // 2. Gestion de l'onglet actif synchrone
    String activeTab = request.getParameter("tab");
    if (activeTab == null) {
        activeTab = (dossierHtml != null || messageErreur != null) ? "suivi" : "demande";
    }
%>
<!DOCTYPE html>
<html lang="fr" data-theme="light">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Portail Citoyen — Mairie CAM</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=DM+Sans:ital,opsz,wght@0,9..40,300;0,9..40,400;0,9..40,500;0,9..40,600;0,9..40,700;1,9..40,400&family=DM+Mono:wght@400;500&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@3.0.0/dist/tabler-icons.min.css">

        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mairie-civic.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/commune.css">
    </head>
    <body class="portal-body">

        <header class="portal-topbar" role="banner">
            <a class="portal-topbar-brand" href="?tab=demande">
                <div class="portal-topbar-brand-icon">
                    <i class="ti ti-building-community" aria-hidden="true"></i>
                </div>
                <div>
                    <div class="portal-topbar-brand-text">Portail Citoyen</div>
                    <div class="portal-topbar-brand-sub">Mairie CAM</div>
                </div>
            </a>

            <div class="portal-topbar-actions">
                <button class="portal-topbar-btn ghost" onclick="toggleLocalTheme()" aria-label="Changer de thème" id="themeBtn">
                    <i class="ti ti-moon" aria-hidden="true" id="themeIcon"></i>
                    <span class="hide-mobile">Thème</span>
                </button>
                <a class="portal-topbar-btn <%= "suivi".equals(activeTab) ? "solid" : "ghost" %>" href="?tab=suivi">
                    <i class="ti ti-search" aria-hidden="true"></i>
                    <span class="hide-mobile">Suivi dossier</span>
                </a>
                <a class="portal-topbar-btn <%= "demande".equals(activeTab) ? "solid" : "ghost" %>" href="?tab=demande">
                    <i class="ti ti-plus" aria-hidden="true"></i>
                    <span class="hide-mobile">Nouvelle demande</span>
                </a>
            </div>
        </header>

        <% if ("demande".equals(activeTab)) { %>
        <div id="page-demande">
            <section class="portal-hero" role="banner" aria-label="En-tête portail">
                <div class="portal-hero-eyebrow">
                    <i class="ti ti-lock" aria-hidden="true"></i>
                    Service sécurisé · Mairie CAM
                </div>
                <h1 class="portal-hero-title">Soumettre une demande administrative</h1>
                <p class="portal-hero-sub">Remplissez le formulaire ci-dessous. Votre demande sera traitée dans les délais légaux et vous recevrez un numéro de suivi unique.</p>
            </section>

            <main class="portal-main" role="main">
                <div class="portal-card">

                    <div class="stepper" id="stepper" role="list" aria-label="Étapes du formulaire">
                        <div class="step-item" role="listitem">
                            <div class="step-dot-wrap">
                                <div class="step-dot active">1</div>
                                <div class="step-label active">Identité</div>
                            </div>
                        </div>
                        <div class="step-line active"></div>
                        <div class="step-item" role="listitem">
                            <div class="step-dot-wrap">
                                <div class="step-dot active">2</div>
                                <div class="step-label active">Dossier complet</div>
                            </div>
                        </div>
                    </div>

                    <% if (request.getAttribute("errorMsg") != null) { %>
                    <div class="error-banner" style="background: #fef2f2; color: #991b1b; padding: 1rem; border-radius: 8px; margin-bottom: 1rem; border: 1px solid #f87171;">
                        <i class="ti ti-alert-circle"></i> <%= request.getAttribute("errorMsg") %>
                    </div>
                    <% } %>

                    <form action="${pageContext.request.contextPath}/portal/commune" method="POST" enctype="multipart/form-data">

                        <div class="portal-card-title">Informations de la demande</div>
                        <div class="portal-card-subtitle">Veuillez renseigner votre Identifiant (NIN) et spécifier la prestation recherchée.</div>

                        <div class="portal-form-group">
                            <label class="portal-label" for="nin">
                                <i class="ti ti-id" aria-hidden="true"></i> Numéro d'Identification Nationale (NIN) <span class="req">*</span>
                            </label>
                            <div class="portal-input-group">
                                <div class="portal-input-prefix">CM —</div>
                                <input class="portal-input mono" id="nin" name="nin" type="text" placeholder="AAAA-XXXXX" maxlength="10" required autocomplete="off">
                            </div>
                            <div class="nin-info-box" role="note">
                                <i class="ti ti-info-circle" aria-hidden="true"></i>
                                Votre NIN figure sur votre Carte Nationale d'Identité. Il permet au système d'authentifier instantanément votre profil citoyen en base.
                            </div>
                        </div>

                        <div class="portal-form-group">
                            <label class="portal-label" for="typeDemandeSelector">
                                <i class="ti ti-file-description" aria-hidden="true"></i> Type de pièce ou document requis <span class="req">*</span>
                            </label>
                            <select class="portal-select" id="typeDemandeSelector" name="typeDemandeCode" onchange="updateDynamicMetadata()" required>
                                <option value="">— Sélectionnez un type de document —</option>
                                <% if (typesDemande != null) {
                                for (TypeDemandeViewModel type : typesDemande) { %>
                                <option value="<%= type.getCode() %>"><%= type.getLabel() %> [<%= type.getCategorie() %>]</option>
                                <%  } } %>
                            </select>
                        </div>

                        <div id="metadataBox" class="meta-box" style="display: none; margin-bottom: 1.5rem;">
                            <div style="font-weight: 600; color: var(--c-primary-600); margin-bottom: 0.5rem; display: flex; align-items: center; gap: 8px;">
                                <i id="metaIcon" class="ti" style="font-size: 18px;"></i>
                                <span>Informations sur la procédure</span>
                            </div>
                            <div style="font-size: 13px; color: var(--text-secondary); margin-bottom: 0.5rem;">
                                <strong>Délai estimé :</strong> <span id="metaDelai"></span>
                            </div>
                            <div style="font-size: 12px; color: var(--text-muted);">
                                <strong style="display: block; margin-bottom: 4px; color: var(--text-primary);">Pièces justificatives obligatoires à fournir (Scans) :</strong>
                                <ul id="metaPieces" style="margin: 0; padding-left: 1.25rem; list-style-type: disc;"></ul>
                            </div>
                        </div>

                        <div class="portal-form-group">
                            <label class="portal-label" for="priorite">
                                <i class="ti ti-flag" aria-hidden="true"></i> Niveau de priorité
                            </label>
                            <select class="portal-select" id="priorite" name="priorite">
                                <option value="NORMALE">Normale (Délai standard)</option>
                                <option value="URGENTE">Urgente (Justificatif d'urgence requis au guichet)</option>
                            </select>
                        </div>

                        <div class="portal-form-group">
                            <label class="portal-label" for="motif">
                                <i class="ti ti-message-circle" aria-hidden="true"></i> Motif de la demande <span class="req">*</span>
                            </label>
                            <textarea class="portal-input" id="motif" name="motif" rows="3" placeholder="Ex: Inscription scolaire, mariage, voyage à l'étranger..." required style="resize: vertical; min-height: 80px;"></textarea>
                        </div>

                        <div class="portal-form-group">
                            <label class="portal-label">
                                <i class="ti ti-paperclip" aria-hidden="true"></i>
                                Justificatifs numérisés (Document unique)
                                <span style="font-size:11px;font-weight:400;color:var(--text-muted);margin-left:4px">(Optionnel)</span>
                            </label>
                            <div class="upload-zone" onclick="document.getElementById('documentsJoints').click()" style="cursor: pointer;">
                                <input type="file" accept=".pdf,.jpg,.jpeg,.png" id="documentsJoints" name="documentsJoints" style="display:none">
                                <div class="upload-icon">
                                    <i class="ti ti-cloud-upload"></i>
                                </div>
                                <div class="upload-title">Cliquez pour ajouter un document justificatif</div>
                                <div class="upload-sub">Formats acceptés : <strong>PDF, JPG, PNG</strong> (Max 5 Mo)</div>
                            </div>
                        </div>

                        <button type="submit" class="portal-btn-primary" style="width: 100%; margin-top: 1rem;">
                            <span><i class="ti ti-shield-check" aria-hidden="true"></i> Transmettre ma demande au guichet</span>
                        </button>
                    </form>

                </div>
            </main>
        </div>
        <% } %>

        <% if ("suivi".equals(activeTab)) { %>
        <div id="page-suivi">
            <section class="portal-hero" role="banner" aria-label="En-tête portail">
                <div class="portal-hero-eyebrow">
                    <i class="ti ti-search" aria-hidden="true"></i>
                    Suivi en temps réel de vos demandes
                </div>
                <h1 class="portal-hero-title">Suivre un dossier</h1>
                <p class="portal-hero-sub">Entrez la référence unique de votre dossier fournie lors de votre dépôt pour consulter l'avancement de l'instruction.</p>
            </section>

            <main class="portal-main" role="main">
                <div class="portal-card">

                    <% if ("true".equals(request.getParameter("success")) && request.getParameter("numeroSuivi") != null) { %>
                    <div style="background-color: var(--c-success-50); border: 1px solid var(--c-success-200); border-radius: var(--radius-lg); padding: 1.5rem; margin-bottom: 2rem; color: var(--c-success-700); text-align: center;">
                        <div style="font-size: 28px; margin-bottom: 8px;"><i class="ti ti-circle-check-filled"></i></div>
                        <h3 style="margin: 0 0 8px 0; font-size: 1.15rem;">Demande soumise avec succès !</h3>
                        <p style="margin: 0 0 16px 0; font-size: 0.95rem;">Votre dossier a été transmis aux services de la mairie. Veuillez conserver précieusement votre numéro de suivi :</p>
                        <div style="background: var(--bg-card); border: 2px dashed var(--c-success-400); padding: 12px 24px; border-radius: var(--radius-md); display: inline-block;">
                            <strong style="font-family: var(--font-mono); font-size: 1.4rem; letter-spacing: 1.5px;"><%= request.getParameter("numeroSuivi") %></strong>
                        </div>
                    </div>
                    <% } %>

                    <div class="portal-card-title">Recherche par numéro de dossier</div>

                    <form action="${pageContext.request.contextPath}/portal/commune" method="GET">
                        <input type="hidden" name="tab" value="suivi">
                        <div class="suivi-search-wrap">
                            <input class="portal-input" type="text" name="numeroSuivi" placeholder="code de suivi (ex: DM-20XX-XXXXX)" value="<%= request.getParameter("numeroSuivi") != null ? request.getParameter("numeroSuivi") : "" %>" required>
                            <button type="submit" class="suivi-search-btn" aria-label="Lancer la recherche">
                                <i class="ti ti-search" aria-hidden="true"></i>
                                <span>Rechercher</span>
                            </button>
                        </div>
                    </form>

                    <div id="suiviResultContainer" style="margin-top: 2rem;">
                        <% if (dossierHtml != null) { %>
                        <%= dossierHtml %>
                        <% } else if (messageErreur != null) { %>
                        <div class="error-box" role="alert" style="padding: 1rem; background: var(--c-error-50); border: 1px solid var(--c-error-200); border-radius: 12px; color: var(--c-error-700); display: flex; align-items: center; gap: 10px;">
                            <i class="ti ti-alert-triangle" style="font-size: 20px;"></i>
                            <div><%= messageErreur %></div>
                        </div>
                        <% } else { %>
                        <div class="empty-state">
                            <div class="empty-icon">
                                <i class="ti ti-fingerprint" aria-hidden="true"></i>
                            </div>
                            <div class="empty-title">Aucune recherche en cours</div>
                            <div class="empty-sub">Saisissez une référence dans la barre ci-dessus pour afficher l'historique d'instruction de vos pièces d'état civil.</div>
                        </div>
                        <% } %>
                    </div>

                </div>
            </main>
        </div>
        <% } %>

        <script>
            // 1. Récupération brute du tableau JSON sérialisé par GSON dans la Servlet
            const typesDemandeRawArray = <%= typesDemandeJson %>;
            
            // 2. Conversion propre en dictionnaire indexé par 'code' pour un accès direct O(1)
            const typesDemandeRegistry = typesDemandeRawArray.reduce(function(map, item) {
                map[item.code] = {
                    icon: item.iconClass,
                    delai: item.delaiEstimation,
                    pieces: item.piecesRequises || [] // Propriété mappée depuis TypeDemandeViewModel
                };
                return map;
            }, {});
            
            /**
            * Met à jour dynamiquement la boîte d'informations sans aucun appel réseau (Zéro AJAX)
            */
            function updateDynamicMetadata() {
                const selector = document.getElementById('typeDemandeSelector');
                const box = document.getElementById('metadataBox');
                
                if (!selector || !box) return;
                
                const selectedCode = selector.value;
                
                if (!selectedCode || !typesDemandeRegistry[selectedCode]) {
                    box.style.display = 'none';
                    return;
                }
                
                const data = typesDemandeRegistry[selectedCode];
                
                // Appliquer l'icône issue de l'enum Icons
                const iconElem = document.getElementById('metaIcon');
                if (iconElem) {
                    iconElem.className = data.icon || 'ti ti-file-text';
                }
                
                // Appliquer le délai calculé
                document.getElementById('metaDelai').innerText = data.delai || 'Délai standard';
                
                // Reconstruire la liste des pièces obligatoires requises
                const piecesContainer = document.getElementById('metaPieces');
                piecesContainer.innerHTML = '';
                
                if (data.pieces && data.pieces.length > 0) {
                    data.pieces.forEach(function(piece) {
                        const li = document.createElement('li');
                        li.textContent = piece;
                        piecesContainer.appendChild(li);
                    });
                } else {
                    const li = document.createElement('li');
                    li.textContent = "Aucune pièce complémentaire n'est requise pour ce type de demande.";
                    piecesContainer.appendChild(li);
                }
                
                box.style.display = 'block';
            }
            
            /**
            * Gestionnaire local de thème (Clair / Sombre)
            */
            function toggleLocalTheme() {
                const htmlTag = document.documentElement;
                const currentTheme = htmlTag.getAttribute('data-theme') || 'light';
                const newTheme = currentTheme === 'light' ? 'dark' : 'light';
                
                htmlTag.setAttribute('data-theme', newTheme);
                
                const icon = document.getElementById('themeIcon');
                if (icon) {
                    icon.className = newTheme === 'light' ? 'ti ti-moon' : 'ti ti-sun';
                }
            }
            
            /**
            * Action de téléchargement synchrone simulée
            */
            function simulateDownload(path) {
                alert("Ouverture sécurisée du document officiel : " + path);
                window.open('${pageContext.request.contextPath}' + path, '_blank');
            }
        </script>
    </body>
</html>