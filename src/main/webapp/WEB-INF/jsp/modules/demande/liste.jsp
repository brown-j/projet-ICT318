<%-- Requests List View: Managed by DynamicTable --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="page-header">
  <div class="page-header-left">
    <h1>Suivi des demandes administratives</h1>
    <p><strong id="demande-total-count">0</strong> demande(s) filtrée(s) / <span style="color:var(--text-muted)">${totalCount} au total</span></p>
  </div>
  <div class="page-header-actions">
    <button class="btn btn-ghost btn-sm"><i class="ti ti-download" aria-hidden="true"></i> Exporter le registre</button>
    <button class="btn btn-primary btn-sm" onclick="openModal('demande')"><i class="ti ti-plus" aria-hidden="true"></i> Nouvelle demande</button>
  </div>
</div>

<%-- Barre des filtres multicritères --%>
<div class="card mb-4" style="padding:var(--space-4)">
  <div style="display:flex;gap:var(--space-3);flex-wrap:wrap;align-items:flex-end">

    <div style="flex:1;min-width:200px">
      <label class="form-label" for="searchDemande">Rechercher</label>
      <div class="input-group">
        <span class="input-group-icon"><i class="ti ti-search" aria-hidden="true"></i></span>
        <input class="form-control" id="searchDemande" type="search" placeholder="Code de suivi, nom du requérant…">
      </div>
    </div>

    <div style="min-width:160px">
      <label class="form-label" for="filterStatutDemande">Statut</label>
      <select class="form-control" id="filterStatutDemande">
        <option value="">Tous les statuts</option>
        <option value="Nouvelle">Nouvelle</option>
        <option value="En cours">En cours</option>
        <option value="Validée">Validée</option>
        <option value="Rejetée">Rejetée</option>
        <option value="Clôturée">Clôturée</option>
      </select>
    </div>

    <div style="min-width:160px">
      <label class="form-label" for="filterPrioriteDemande">Priorité</label>
      <select class="form-control" id="filterPrioriteDemande">
        <option value="">Toutes les priorités</option>
        <option value="Basse">Basse</option>
        <option value="Normale">Normale</option>
        <option value="Haute">Haute</option>
        <option value="Urgente">Urgente</option>
      </select>
    </div>
  </div>
</div>

<%-- Tableau de données géré par tableau.js --%>
<div class="card" style="padding:0">
  <div class="table-wrapper">
    <table>
      <thead>
        <tr>
          <th>Code Suivi</th>
          <th>Type de Demande</th>
          <th>Citoyen Requérant</th>
          <th>Date Soumission</th>
          <th>Priorité</th>
          <th>Statut</th>
          <th style="text-align:right">Actions</th>
        </tr>
      </thead>
      <tbody id="demande-table-body">
        <%-- Lignes injectées dynamiquement --%>
      </tbody>
    </table>
  </div>

  <%-- Pagination standardisée --%>
  <div style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4);border-top:1px solid var(--border-divider)">
    <span id="demande-pagination-info" style="font-size:var(--text-sm);color:var(--text-muted)"></span>
    <div id="demande-pagination-buttons" style="display:flex;gap:6px"></div>
  </div>
</div>

<script>
  document.addEventListener("DOMContentLoaded", () => {
    // 1. Extraction sécurisée du JSON envoyé par le Servlet DemandeListeServlet
    const demandesData = ${demandesJson};
    
    // 2. Initialisation et configuration de la table dynamique des demandes
    const demandeTable = new DynamicTable({
      data: demandesData,
      tbodyId: 'demande-table-body',
      infoId: 'demande-pagination-info',
      pagerId: 'demande-pagination-buttons',
      totalCountId: 'demande-total-count',
      pageSize: 8,
      
      // Gabarit HTML d'une ligne (Template String)
      renderRow: (d) => `
      <tr>
      <td><code style="font-family:var(--font-mono);font-size:11px;color:var(--text-muted);font-weight:var(--fw-medium)">\${d.numeroSuivi}</code></td>
      <td><strong style="font-weight:var(--fw-medium)">\${d.typeDemandeLabel}</strong></td>
      <td>\${d.requérantNomComplet}</td>
      <td style="color:var(--text-muted)">\${d.dateSoumissionFormatee}</td>
      <td>
      <span class="badge badge-\${d.prioriteColorClass}" style="font-size:10px; font-weight:var(--fw-semibold)">
      \${d.prioriteLabel === 'Urgente' ? '<i class="ti ti-alert-circle" style="font-size:11px" aria-hidden="true"></i> ' : ''}\${d.prioriteLabel}
      </span>
      </td>
      <td>
      <span class="badge badge-\${d.statutColorClass}">
      \${d.afficherPointStatut ? '<i class="ti ti-point-filled" style="font-size:10px" aria-hidden="true"></i> ' : ''}
      \${d.statutLabel}
      </span>
      </td>
      <td style="text-align:right">
      <div style="display:flex;gap:6px;justify-content:flex-end">
      <a href="${pageContext.request.contextPath}/demande/detail?id=\${d.id}" class="btn btn-ghost btn-sm btn-icon" title="Traiter la demande / Voir les détails">
      <i class="ti ti-folder-open" aria-hidden="true"></i>
      </a>
      \${d.hasDocumentFinal ? `
      <a href="${pageContext.request.contextPath}/demande/telecharger?id=\${d.id}" class="btn btn-ghost btn-sm btn-icon" style="color:var(--c-success-600)" title="Télécharger le document signé (PDF)" target="_blank">
      <i class="ti ti-download" aria-hidden="true"></i>
      </a>
      ` : `
      <button class="btn btn-ghost btn-sm btn-icon" style="color:var(--text-muted)" title="Aucun document délivré pour le moment" disabled>
      <i class="ti ti-file-off" aria-hidden="true"></i>
      </button>
      `}
      </div>
      </td>
      </tr>
      `,
      
      // Prédicat de filtrage multicritère (Recherche croisée code/nom + priorités + statuts)
      filterFn: (item, filters) => {
        const query = (filters.query || '').toLowerCase();
        const matchQuery = !query ||
        item.numeroSuivi.toLowerCase().includes(query) ||
        item.requérantNomComplet.toLowerCase().includes(query);
        
        const matchStatut = !filters.statut || item.statutLabel === filters.statut;
        const matchPriorite = !filters.priorite || item.prioriteLabel === filters.priorite;
        
        return matchQuery && matchStatut && matchPriorite;
      }
    });
    
    // 3. Liaison des contrôles d'UI aux filtres de l'instance
    document.getElementById('searchDemande').addEventListener('input', (e) => {
      demandeTable.setFilter('query', e.target.value);
    });
    
    document.getElementById('filterStatutDemande').addEventListener('change', (e) => {
      demandeTable.setFilter('statut', e.target.value);
    });
    
    document.getElementById('filterPrioriteDemande').addEventListener('change', (e) => {
      demandeTable.setFilter('priorite', e.target.value);
    });
    
    // 4. Lancement du premier affichage (Page 1)
    demandeTable.render(1);
  });
</script>