<%-- Actes List View: Managed by DynamicTable --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="page-header">
  <div class="page-header-left">
    <h1>Registre des actes civils</h1>
    <p><strong id="acte-total-count">0</strong> acte(s) filtré(s) / <span style="color:var(--text-muted)">${totalCount} au total</span></p>
  </div>
  <div class="page-header-actions">
    <button class="btn btn-ghost btn-sm"><i class="ti ti-download" aria-hidden="true"></i> Exporter la liste</button>
    <button class="btn btn-primary btn-sm" onclick="openModal('acte')"><i class="ti ti-plus" aria-hidden="true"></i> Dresser un acte</button>
  </div>
</div>

<%-- Filtres connectés dynamiquement à l'instance DynamicTable --%>
<div class="card mb-4" style="padding:var(--space-4)">
  <div style="display:flex;gap:var(--space-3);flex-wrap:wrap;align-items:flex-end">

    <div style="flex:1;min-width:200px">
      <label class="form-label" for="searchActe">Rechercher</label>
      <div class="input-group">
        <span class="input-group-icon"><i class="ti ti-search" aria-hidden="true"></i></span>
        <input class="form-control" id="searchActe" type="search" placeholder="N° d'acte, nom du citoyen…">
      </div>
    </div>

    <div style="min-width:160px">
      <label class="form-label" for="filterStatutActe">Statut</label>
      <select class="form-control" id="filterStatutActe">
        <option value="">Tous les statuts</option>
        <option value="En cours">En cours</option>
        <option value="Délivré">Délivré</option>
        <option value="Annulé">Annulé</option>
        <option value="Archivé">Archivé</option>
      </select>
    </div>

    <div style="min-width:160px">
      <label class="form-label" for="filterTypeActe">Type d'acte</label>
      <select class="form-control" id="filterTypeActe">
        <option value="">Tous les types</option>
        <option value="Naissance">Naissance</option>
        <option value="Mariage">Mariage</option>
        <option value="Décès">Décès</option>
      </select>
    </div>
  </div>
</div>

<div class="card" style="padding:0">
  <div class="table-wrapper">
    <table>
      <thead>
        <tr>
          <th>N° Acte</th>
          <th>Type d'acte</th>
          <th>Citoyen concerné</th>
          <th>Date événement</th>
          <th>Date établ.</th>
          <th>Statut</th>
          <th style="text-align:right">Actions</th>
        </tr>
      </thead>
      <tbody id="acte-table-body">
        <%-- Géré dynamiquement par le tableau.js --%>
      </tbody>
    </table>
  </div>

  <%-- Footer de table & Pagination dynamique --%>
  <div style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4);border-top:1px solid var(--border-divider)">
    <span id="acte-pagination-info" style="font-size:var(--text-sm);color:var(--text-muted)"></span>
    <div id="acte-pagination-buttons" style="display:flex;gap:6px"></div>
  </div>
</div>

<script>
  document.addEventListener("DOMContentLoaded", () => {
    // 1. Récupération directe du JSON Gson envoyé par le Servlet ActeListeServlet
    const actesData = ${actesJson};
    
    // 2. Initialisation et configuration de la table dynamique pour les actes civils
    const acteTable = new DynamicTable({
      data: actesData,
      tbodyId: 'acte-table-body',
      infoId: 'acte-pagination-info',
      pagerId: 'acte-pagination-buttons',
      totalCountId: 'acte-total-count',
      pageSize: 8,
      
      // Gabarit HTML d'une ligne (Template String)
      renderRow: (a) => `
      <tr>
      <td><code style="font-family:var(--font-mono);font-size:11px;color:var(--text-muted);font-weight:var(--fw-medium)">\${a.numeroActe}</code></td>
      <td>
      <span style="display:inline-flex;align-items:center;gap:6px">
      <i class="ti \${a.typeActeLabel === 'Naissance' ? 'ti-baby-carriage' : a.typeActeLabel === 'Mariage' ? 'ti-rings' : 'ti-scooter'}" style="color:var(--text-muted);font-size:14px" aria-hidden="true"></i>
      \${a.typeActeLabel}
      </span>
      </td>
      <td><strong style="font-weight:var(--fw-medium)">\${a.citoyenPrincipalNom}</strong></td>
      <td style="color:var(--text-muted)">\${a.dateEvenementFormatee}</td>
      <td style="color:var(--text-muted)">\${a.dateEtablissementFormatee}</td>
      <td>
      <span class="badge badge-\${a.statutColorClass}">
      \${a.afficherPointStatut ? '<i class="ti ti-point-filled" style="font-size:10px" aria-hidden="true"></i> ' : ''}
      \${a.statutLabel}
      </span>
      </td>
      <td style="text-align:right">
      <div style="display:flex;gap:6px;justify-content:flex-end">
      <a href="${pageContext.request.contextPath}/acte/detail?id=\${a.id}" class="btn btn-ghost btn-sm btn-icon" title="Consulter le registre">
      <i class="ti ti-eye" aria-hidden="true"></i>
      </a>
      \${a.hasPdf ? `
      <a href="${pageContext.request.contextPath}/acte/telecharger?id=\${a.id}" class="btn btn-ghost btn-sm btn-icon" style="color:var(--c-danger-600)" title="Télécharger l'acte officiel (PDF)" target="_blank">
      <i class="ti ti-file-type-pdf" aria-hidden="true"></i>
      </a>
      ` : `
      <button class="btn btn-ghost btn-sm btn-icon" style="color:var(--text-muted)" title="Aucun document numérisé" disabled>
      <i class="ti ti-file-off" aria-hidden="true"></i>
      </button>
      `}
      </div>
      </td>
      </tr>
      `,
      
      // Prédicat de filtrage multicritère (Recherche croisée numéro/nom + filtres cumulatifs)
      filterFn: (item, filters) => {
        const query = (filters.query || '').toLowerCase();
        const matchQuery = !query ||
        item.numeroActe.toLowerCase().includes(query) ||
        item.citoyenPrincipalNom.toLowerCase().includes(query);
        
        const matchStatut = !filters.statut || item.statutLabel === filters.statut;
        const matchType = !filters.type || item.typeActeLabel === filters.type;
        
        return matchQuery && matchStatut && matchType;
      }
    });
    
    // 3. Liaison des contrôles de l'UI aux critères de filtrage
    document.getElementById('searchActe').addEventListener('input', (e) => {
      acteTable.setFilter('query', e.target.value);
    });
    
    document.getElementById('filterStatutActe').addEventListener('change', (e) => {
      acteTable.setFilter('statut', e.target.value);
    });
    
    document.getElementById('filterTypeActe').addEventListener('change', (e) => {
      acteTable.setFilter('type', e.target.value);
    });
    
    // 4. Premier rendu du tableau au chargement de l'écran
    acteTable.render(1);
  });
</script>