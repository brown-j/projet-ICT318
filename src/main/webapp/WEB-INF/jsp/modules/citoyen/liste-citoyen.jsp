<%-- Citizens List View: Managed by DynamicTable --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="page-header">
    <div class="page-header-left">
        <h1>Gestion des citoyens</h1>
        <p><strong id="citoyen-total-count">0</strong> citoyen(s) filtré(s) / <span style="color:var(--text-muted)">${totalCount} au total</span></p>
    </div>
    <div class="page-header-actions">
        <button id="btn-exporter-citoyens" class="btn btn-ghost btn-sm">
            <i class="ti ti-download" aria-hidden="true"></i> Exporter
        </button>
        <button class="btn btn-outline btn-sm" onclick="openModal('global')" title="Afficher le formulaire actuellement chargé">
            <i class="ti ti-eye" aria-hidden="true"></i> Formulaire
        </button>
        <%-- 💡 ALIGNEMENT : Redirection vers le contrôleur pour initialiser un contexte de création propre --%>
        <a href="${pageContext.request.contextPath}/citoyen/liste?mode=create" class="btn btn-primary btn-sm">
            <i class="ti ti-plus" aria-hidden="true"></i> Nouveau
        </a>
    </div>
</div>

<%-- Filtres connectés dynamiquement à l'instance DynamicTable --%>
<div class="card mb-4" style="padding:var(--space-4)">
    <div style="display:flex;gap:var(--space-3);flex-wrap:wrap;align-items:flex-end">

        <div style="flex:1;min-width:200px">
            <label class="form-label" for="searchCitoyen">Rechercher</label>
            <div class="input-group">
                <span class="input-group-icon"><i class="ti ti-search" aria-hidden="true"></i></span>
                <input class="form-control" id="searchCitoyen" type="search" placeholder="Nom, prénom, NIN…">
            </div>
        </div>

        <%-- 💡 DYNAMISATION DU STATUT : Options injectées par JS --%>
        <div style="min-width:160px">
            <label class="form-label" for="filterStatut">Statut</label>
            <select class="form-control" id="filterStatut">
                <option value="">Tous les statuts</option>
            </select>
        </div>

        <%-- 💡 DYNAMISATION DU QUARTIER : Options injectées par JS --%>
        <div style="min-width:160px">
            <label class="form-label" for="filterQuartier">Quartier</label>
            <select class="form-control" id="filterQuartier">
                <option value="">Tous les quartiers</option>
            </select>
        </div>
    </div>
</div>

<div class="card" style="padding:0">
    <div class="table-wrapper">
        <table>
            <thead>
                <tr>
                    <th>NIN</th>
                    <th>Nom complet</th>
                    <th>Date naiss.</th>
                    <th>Quartier</th>
                    <th>Situation</th>
                    <th>Statut</th>
                    <th style="text-align:right">Actions</th>
                </tr>
            </thead>
            <tbody id="citoyen-table-body">
                <%-- Géré par dynamique tableau.js --%>
            </tbody>
        </table>
    </div>

    <%-- Footer de table & Pagination dynamique --%>
    <div style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4);border-top:1px solid var(--border-divider)">
        <span id="citoyen-pagination-info" style="font-size:var(--text-sm);color:var(--text-muted)"></span>
        <div id="citoyen-pagination-buttons" style="display:flex;gap:6px"></div>
    </div>
</div>

<script>
    document.addEventListener("DOMContentLoaded", () => {
        // 1. Sécurisation Null-Safe du JSON via Expression Language JSP
        const citoyensData = ${empty citoyensJson ? '[]' : citoyensJson};
        
        // 💡 1.B PEUPLEMENT DYNAMIQUE DES FILTRES DEPUIS LES DONNÉES DU TABLEAU
        
        // Extraction et injection des STATUTS réels
        const filterStatutSelect = document.getElementById('filterStatut');
        const statutsUniques = [...new Set(citoyensData.map(c => c.statutLabel).filter(Boolean))];
        statutsUniques.sort().forEach(statut => {
            const option = document.createElement('option');
            option.value = statut;
            option.textContent = statut;
            filterStatutSelect.appendChild(option);
        });
        
        // Extraction et injection des QUARTIERS réels
        const filterQuartierSelect = document.getElementById('filterQuartier');
        const quartiersUniques = [...new Set(citoyensData.map(c => c.quartier).filter(Boolean))];
        quartiersUniques.sort().forEach(quartier => {
            const option = document.createElement('option');
            option.value = quartier;
            option.textContent = quartier;
            filterQuartierSelect.appendChild(option);
        });
        
        // 2. Initialisation et configuration de la table dynamique pour les citoyens
        const citoyenTable = new DynamicTable({
            data: citoyensData,
            tbodyId: 'citoyen-table-body',
            infoId: 'citoyen-pagination-info',
            pagerId: 'citoyen-pagination-buttons',
            totalCountId: 'citoyen-total-count',
            pageSize: 8,
            
            // Gabarit HTML d'une ligne (Template String)
            renderRow: (c) => `
            <tr>
            <td><code style="font-family:var(--font-mono);font-size:11px;color:var(--text-muted)">\${c.nin || ''}</code></td>
            <td><strong style="font-weight:var(--fw-medium)">\${c.nomComplet || ''}</strong></td>
            <td style="color:var(--text-muted)">\${c.dateNaissanceFormatee || ''}</td>
            <td>\${c.quartier || ''}</td>
            <td><span class="badge badge-\${c.situationColorClass || 'default'}">\${c.situationLabel || ''}</span></td>
            <td>
            <span class="badge badge-\${c.statutColorClass || 'default'}">
            \${c.afficherPointStatut ? '<i class="ti ti-point-filled" style="font-size:10px" aria-hidden="true"></i> ' : ''}
            \${c.statutLabel || ''}
            </span>
            </td>
            <td style="text-align:right">
            <div style="display:flex;gap:6px;justify-content:flex-end">
            
            <%-- 💡 ALIGNEMENT : Bouton Preview uniforme --%>
            <a href="${pageContext.request.contextPath}/citoyen/liste?id=\${c.id}&mode=preview" class="btn btn-ghost btn-sm btn-icon" title="Consulter le dossier historique">
            <i class="ti ti-eye" aria-hidden="true"></i>
            </a>
            
            <%-- 💡 ALIGNEMENT : Bouton Édition uniforme utilisant le paramètre standardisé 'id' et 'mode' --%>
            <a href="${pageContext.request.contextPath}/citoyen/liste?id=\${c.id}&mode=edit" class="btn btn-ghost btn-sm btn-icon" title="Modifier la fiche">
            <i class="ti ti-edit" aria-hidden="true"></i>
            </a>
            </div>
            </td>
            </tr>
            `,
            
            // Prédicat de filtrage multicritère sécurisé contre le Null/Undefined
            filterFn: (item, filters) => {
                const query = (filters.query || '').toLowerCase().trim();
                const matchQuery = !query ||
                (item.nomComplet || '').toLowerCase().includes(query) ||
                (item.nin || '').toLowerCase().includes(query);
                
                const matchStatut = !filters.statut || item.statutLabel === filters.statut;
                const matchQuartier = !filters.quartier || item.quartier === filters.quartier;
                
                return matchQuery && matchStatut && matchQuartier;
            }
        });
        
        // 3. Liaison des événements UI aux filtres de la table
        document.getElementById('searchCitoyen').addEventListener('input', (e) => {
            citoyenTable.setFilter('query', e.target.value);
        });
        
        document.getElementById('filterStatut').addEventListener('change', (e) => {
            citoyenTable.setFilter('statut', e.target.value);
        });
        
        document.getElementById('filterQuartier').addEventListener('change', (e) => {
            citoyenTable.setFilter('quartier', e.target.value);
        });
        
        // 4. Premier affichage au chargement de l'écran
        citoyenTable.render(1);
        
        // Activation de l'ouverture automatique du popup global si demandé par la servlet
        <c:if test="${autoOpenModal}">
        openModal('global');
        </c:if>
        
        // Configuration et exécution de l'exportation CSV globale
        const mappingColonnesCitoyens = {
            nin: "NIN",
            nomComplet: "Nom Complet",
            dateNaissanceFormatee: "Date de Naissance",
            quartier: "Quartier",
            situationLabel: "Situation Administrative",
            statutLabel: "Statut"
        };
        
        document.getElementById('btn-exporter-citoyens').addEventListener('click', (e) => {
            e.preventDefault();
            exportCSV(citoyensData, mappingColonnesCitoyens, 'registre_citoyens');
        });
    });
</script>