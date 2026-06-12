<%-- Officier Etat Civil List View: Managed by DynamicTable --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="page-header">
    <div class="page-header-left">
        <h1>Gestion du Personnel et des Accès</h1>
        <p><strong id="officier-total-count">0</strong> agent(s) filtré(s) / <span style="color:var(--text-muted)">${totalCount} au total</span></p>
    </div>
    <div class="page-header-actions">
        <button class="btn btn-ghost btn-sm"><i class="ti ti-download" aria-hidden="true"></i> Exporter</button>
        <button class="btn btn-outline btn-sm" onclick="openModal('officier')" title="Afficher le formulaire actuellement chargé">
            <i class="ti ti-eye" aria-hidden="true"></i> Formulaire
        </button>
        <%-- Redirection vers le contrôleur pour initialiser un contexte de création propre d'officier --%>
        <a href="${pageContext.request.contextPath}/officier/liste?mode=create" class="btn btn-primary btn-sm">
            <i class="ti ti-user-plus" aria-hidden="true"></i> Nouvel Agent
        </a>
    </div>
</div>

<%-- Filtres connectés dynamiquement à l'instance DynamicTable --%>
<div class="card mb-4" style="padding:var(--space-4)">
    <div style="display:flex;gap:var(--space-3);flex-wrap:wrap;align-items:flex-end">

        <div style="flex:1;min-width:200px">
            <label class="form-label" for="searchOfficier">Rechercher</label>
            <div class="input-group">
                <span class="input-group-icon"><i class="ti ti-search" aria-hidden="true"></i></span>
                <input class="form-control" id="searchOfficier" type="search" placeholder="Nom, prénom, matricule, email…">
            </div>
        </div>

        <div style="min-width:180px">
            <label class="form-label" for="filterRole">Rôle système</label>
            <select class="form-control" id="filterRole">
                <option value="">Tous les rôles</option>
                <option value="Super Administrateur">Super Administrateur</option>
                <option value="Administrateur">Administrateur</option>
                <option value="Officier d'État Civil">Officier d'État Civil</option>
                <option value="Agent de Saisie">Agent de Saisie</option>
            </select>
        </div>

        <div style="min-width:160px">
            <label class="form-label" for="filterStatutOfficier">Statut d'accès</label>
            <select class="form-control" id="filterStatutOfficier">
                <option value="">Tous les statuts</option>
                <option value="Actif">Actif</option>
                <option value="Suspendu">Suspendu</option>
                <option value="Inactif">Inactif</option>
            </select>
        </div>
    </div>
</div>

<div class="card" style="padding:0">
    <div class="table-wrapper">
        <table>
            <thead>
                <tr>
                    <th>Matricule</th>
                    <th>Nom complet</th>
                    <th>Email (Identifiant)</th>
                    <th>Service / Bureau</th>
                    <th>Rôle</th>
                    <th>Signature</th>
                    <th>Statut</th>
                    <th style="text-align:right">Actions</th>
                </tr>
            </thead>
            <tbody id="officier-table-body">
                <%-- Géré par dynamique tableau.js --%>
            </tbody>
        </table>
    </div>

    <%-- Footer de table & Pagination dynamique --%>
    <div style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4);border-top:1px solid var(--border-divider)">
        <span id="officier-pagination-info" style="font-size:var(--text-sm);color:var(--text-muted)"></span>
        <div id="officier-pagination-buttons" style="display:flex;gap:6px"></div>
    </div>
</div>

<%-- ========================================================= --%>
<%-- MODALE D'ENREGISTREMENT / MODIFICATION D'OFFICIER         --%>
<%-- ========================================================= --%>
<div class="modal-backdrop ${autoOpenModal ? 'show' : ''}" id="modal-officier" role="dialog" aria-modal="true">
    <div class="modal">
        <div class="modal-header">
            <h2 class="modal-title" style="font-size:var(--text-xl)">Habilitation & Profil Utilisateur</h2>
            <button class="icon-btn" onclick="closeModal('officier')" aria-label="Fermer">
                <i class="ti ti-x" aria-hidden="true"></i>
            </button>
        </div>

        <%-- Injection propre du formulaire généré par la Factory --%>
        ${formulaireHtml}

    </div>
</div>

<script>
    document.addEventListener("DOMContentLoaded", () => {
        // Sécurisation Null-Safe du JSON via Expression Language JSP
        const officiersData = ${empty officiersJson ? '[]' : officiersJson};
        
        // Initialisation et configuration de la table dynamique pour les officiers
        const officierTable = new DynamicTable({
            data: officiersData,
            tbodyId: 'officier-table-body',
            infoId: 'officier-pagination-info',
            pagerId: 'officier-pagination-buttons',
            totalCountId: 'officier-total-count',
            pageSize: 8,
            
            // Gabarit HTML d'une ligne (Template String)
            renderRow: (o) => `
            <tr>
            <td><code style="font-family:var(--font-mono);font-size:11px;color:var(--text-muted)">\${o.matricule}</code></td>
            <td><strong style="font-weight:var(--fw-medium)">\${o.nomComplet}</strong></td>
            <td style="color:var(--text-muted)">\${o.email}</td>
            <td>\${o.service}</td>
            <td><span class="badge badge-\${o.roleColorClass}">\${o.roleLabel}</span></td>
            <td>
            \${o.hasSignature ?
            '<span style="color:var(--color-success);font-size:var(--text-sm)"><i class="ti ti-signature"></i> Configurée</span>' :
            '<span style="color:var(--text-muted);font-size:var(--text-sm)"><i class="ti ti-signature-off"></i> Manquante</span>'}
            </td>
            <td>
            <span class="badge badge-\${o.statutColorClass}">
            \${o.afficherPointStatut ? '<i class="ti ti-point-filled" style="font-size:10px" aria-hidden="true"></i> ' : ''}
            \${o.statutLabel}
            </span>
            </td>
            <td style="text-align:right">
            <div style="display:flex;gap:6px;justify-content:flex-end">
            
            <%-- Bouton Preview uniforme --%>
            <a href="${pageContext.request.contextPath}/officier/liste?id=\${o.id}&mode=preview" class="btn btn-ghost btn-sm btn-icon" title="Consulter les informations du compte">
            <i class="ti ti-eye" aria-hidden="true"></i>
            </a>
            
            <%-- Bouton Édition uniforme utilisant le paramètre standardisé 'id' et 'mode' --%>
            <a href="${pageContext.request.contextPath}/officier/liste?id=\${o.id}&mode=edit" class="btn btn-ghost btn-sm btn-icon" title="Modifier les droits d'accès">
            <i class="ti ti-edit" aria-hidden="true"></i>
            </a>
            </div>
            </td>
            </tr>
            `,
            
            // Prédicat de filtrage multicritère
            filterFn: (item, filters) => {
                const query = (filters.query || '').toLowerCase();
                const matchQuery = !query ||
                item.nomComplet.toLowerCase().includes(query) ||
                item.matricule.toLowerCase().includes(query) ||
                item.email.toLowerCase().includes(query);
                
                const matchRole = !filters.role || item.roleLabel === filters.role;
                const matchStatut = !filters.statut || item.statutLabel === filters.statut;
                
                return matchQuery && matchRole && matchStatut;
            }
        });
        
        // Liaison des événements UI aux filtres de la table
        document.getElementById('searchOfficier').addEventListener('input', (e) => {
            officierTable.setFilter('query', e.target.value);
        });
        
        document.getElementById('filterRole').addEventListener('change', (e) => {
            officierTable.setFilter('role', e.target.value);
        });
        
        document.getElementById('filterStatutOfficier').addEventListener('change', (e) => {
            officierTable.setFilter('statut', e.target.value);
        });
        
        // Premier affichage
        officierTable.render(1);
    });
</script>