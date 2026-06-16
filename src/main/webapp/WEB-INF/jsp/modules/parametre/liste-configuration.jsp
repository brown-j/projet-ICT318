<%-- Paramètres Catalogue View: Managed by DynamicTable --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="page-header">
    <div class="page-header-left">
        <h1>Configuration du catalogue des prestations</h1>
        <p><strong id="param-total-count">0</strong> prestation(s) filtrée(s) / <span style="color:var(--text-muted)">${totalCount} au total</span></p>
    </div>
    <div class="page-header-actions">
        <button id="btn-exporter-catalogue" class="btn btn-ghost btn-sm">
            <i class="ti ti-download" aria-hidden="true"></i> Exporter
        </button>
        <button class="btn btn-outline btn-sm" onclick="openModal('global')" title="Afficher le formulaire actuellement chargé">
            <i class="ti ti-eye" aria-hidden="true"></i> Formulaire
        </button>
        <a href="${pageContext.request.contextPath}/parametre/configuration?mode=create" class="btn btn-primary btn-sm">
            <i class="ti ti-plus" aria-hidden="true"></i> Ajouter une prestation
        </a>
    </div>
</div>

<%-- Filtres connectés dynamiquement à l'instance DynamicTable --%>
<div class="card mb-4" style="padding:var(--space-4)">
    <div style="display:flex;gap:var(--space-3);flex-wrap:wrap;align-items:flex-end">

        <div style="flex:1;min-width:200px">
            <label class="form-label" for="searchParam">Rechercher</label>
            <div class="input-group">
                <span class="input-group-icon"><i class="ti ti-search" aria-hidden="true"></i></span>
                <input class="form-control" id="searchParam" type="search" placeholder="Code unique, nom de la prestation…">
            </div>
        </div>

        <%-- 💡 DYNAMISATION DU SECTEUR : Options injectées par JS --%>
        <div style="min-width:200px">
            <label class="form-label" for="filterCategorieParent">Secteur / Registre</label>
            <select class="form-control" id="filterCategorieParent">
                <option value="">Tous les registres</option>
            </select>
        </div>
    </div>
</div>

<div class="card" style="padding:0">
    <div class="table-wrapper">
        <table>
            <thead>
                <tr>
                    <th>Code Prestation</th>
                    <th>Libellé Officiel</th>
                    <th>Secteur Parent</th>
                    <th>Tarif Municipal</th>
                    <th style="text-align:right">Actions</th>
                </tr>
            </thead>
            <tbody id="param-table-body">
                <%-- Géré dynamiquement par le tableau.js --%>
            </tbody>
        </table>
    </div>

    <%-- Footer de table & Pagination dynamique --%>
    <div style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4);border-top:1px solid var(--border-divider)">
        <span id="param-pagination-info" style="font-size:var(--text-sm);color:var(--text-muted)"></span>
        <div id="param-pagination-buttons" style="display:flex;gap:6px"></div>
    </div>
</div>

<script>
    document.addEventListener("DOMContentLoaded", () => {
        // 1. Sécurisation Null-Safe du JSON via JSP Expression Language
        const catalogueData = ${empty catalogueJson ? '[]' : catalogueJson};
        
        // 💡 1.B PEUPLEMENT DYNAMIQUE DES FILTRES DEPUIS LES DONNÉES DU TABLEAU
        const filterCategorieSelect = document.getElementById('filterCategorieParent');
        
        // Extraction des secteurs / registres uniques (gestion de l'éventuelle différence code/libellé)
        const categoriesMap = new Map();
        catalogueData.forEach(p => {
            if (p.categorieParent) {
                // Si l'objet contient un libellé dédié utilisez-le, sinon utilisez la valeur brute
                const label = p.categorieParentLabel || p.categorieParent;
                categoriesMap.set(p.categorieParent, label);
            }
        });
        
        // Tri alphabétique sur le libellé affiché et injection
        [...categoriesMap.entries()]
        .sort((a, b) => a[1].localeCompare(b[1]))
        .forEach(([code, label]) => {
            const option = document.createElement('option');
            option.value = code;
            option.textContent = label;
            filterCategorieSelect.appendChild(option);
        });
        
        // 2. Initialisation et configuration de la table dynamique pour les configurations
        const paramTable = new DynamicTable({
            data: catalogueData,
            tbodyId: 'param-table-body',
            infoId: 'param-pagination-info',
            pagerId: 'param-pagination-buttons',
            totalCountId: 'param-total-count',
            pageSize: 8,
            
            // Gabarit HTML d'une ligne (Template String)
            renderRow: (p) => {
                const cat = (p.categorieParent || '').toLowerCase();
                let iconClass = 'ti-file'; // Icône générique par défaut
                
                if (cat.includes('naissance')) iconClass = 'ti-baby-carriage';
                else if (cat.includes('mariage')) iconClass = 'ti-rings';
                else if (cat.includes('deces') || cat.includes('décès')) iconClass = 'ti-candle';
                
                return `
                <tr>
                <td><code style="font-family:var(--font-mono);font-size:11px;color:var(--text-muted);font-weight:var(--fw-medium)">\${p.code || ''}</code></td>
                <td><strong style="font-weight:var(--fw-medium)">\${p.libelle || ''}</strong></td>
                <td>
                <span style="display:inline-flex;align-items:center;gap:6px">
                <i class="ti \${iconClass}" style="color:var(--text-muted);font-size:14px" aria-hidden="true"></i>
                \${p.categorieParentLabel || p.categorieParent || ''}
                </span>
                </td>
                <td>
                <span style="font-weight:var(--fw-semibold);color:\${p.tarifFCFA === 0 ? 'var(--c-success-600)' : 'var(--text-main)'}">
                \${p.tarifFCFA === 0 ? 'Gratuit' : (p.tarifFCFA + ' FCFA')}
                </span>
                </td>
                <td style="text-align:right">
                <div style="display:flex;gap:6px;justify-content:flex-end">
                
                <%-- Bouton de téléchargement conditionnel --%>
                \${p.templatePath ? `
                <a href="${pageContext.request.contextPath}/telecharger?fichier=\${p.templatePath}&dossier=templates" class="btn btn-ghost btn-sm btn-icon" title="Télécharger le gabarit officiel" target="_blank">
                <i class="ti ti-download" aria-hidden="true"></i>
                </a>
                ` : `
                <button class="btn btn-ghost btn-sm btn-icon" style="color:var(--text-muted)" title="Aucun gabarit de document associé" disabled>
                <i class="ti ti-file-off" aria-hidden="true"></i>
                </button>
                `}
                
                <a href="${pageContext.request.contextPath}/parametre/configuration?code=\${p.code}&mode=preview" class="btn btn-ghost btn-sm btn-icon" title="Consulter la configuration">
                <i class="ti ti-eye" aria-hidden="true"></i>
                </a>
                
                <a href="${pageContext.request.contextPath}/parametre/configuration?code=\${p.code}&mode=edit" class="btn btn-ghost btn-sm btn-icon" title="Modifier les paramètres">
                <i class="ti ti-edit" aria-hidden="true"></i>
                </a>
                
                </div>
                </td>
                </tr>
                `;
            },
            
            // Prédicat de filtrage multicritère sécurisé contre le Null/Undefined
            filterFn: (item, filters) => {
                const query = (filters.query || '').toLowerCase().trim();
                const matchQuery = !query ||
                (item.code || '').toLowerCase().includes(query) ||
                (item.libelle || '').toLowerCase().includes(query);
                
                const matchCategorie = !filters.categorie || item.categorieParent === filters.categorie;
                
                return matchQuery && matchCategorie;
            }
        });
        
        // 3. Liaison des contrôles de l'UI aux critères de filtrage
        document.getElementById('searchParam').addEventListener('input', (e) => {
            paramTable.setFilter('query', e.target.value);
        });
        
        document.getElementById('filterCategorieParent').addEventListener('change', (e) => {
            paramTable.setFilter('categorie', e.target.value);
        });
        
        // 4. Premier rendu du tableau au chargement de l'écran
        paramTable.render(1);
        
        // Activation de l'ouverture automatique du popup global si demandé par la servlet
        <c:if test="${autoOpenModal}">
        openModal('global');
        </c:if>
        
        // Configuration et exécution de l'exportation CSV globale
        const mappingColonnesCatalogue = {
            code: "Code Prestation",
            libelle: "Libellé Officiel",
            categorieParent: "Code Secteur Parent",
            tarifFCFA: "Tarif (FCFA)"
        };
        
        document.getElementById('btn-exporter-catalogue').addEventListener('click', (e) => {
            e.preventDefault();
            exportCSV(catalogueData, mappingColonnesCatalogue, 'catalogue_prestations_municipales');
        });
    });
</script>