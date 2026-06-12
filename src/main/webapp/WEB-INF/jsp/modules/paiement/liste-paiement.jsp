<%-- Payment History View: Managed by DynamicTable --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Importation de l'enum pour toute utilisation stricte ou typée si nécessaire --%>
<%@ page import="com.app.jpa.model.JPAEnum.ModePaiement" %>

<div class="page-header">
  <div class="page-header-left">
    <h1>Historique des Paiements</h1>
    <p><strong id="paiement-total-count">0</strong> transaction(s) filtrée(s) / <span style="color:var(--text-muted)">${totalCount} au total</span></p>
  </div>
  <div class="page-header-actions">
    <button class="btn btn-ghost btn-sm"><i class="ti ti-download" aria-hidden="true"></i> Exporter la table</button>
  </div>
</div>

<%-- Alerte Caisse du jour Dynamique --%>
<div class="alert alert-success mb-4">
  <i class="ti ti-cash" aria-hidden="true"></i>
  <div>
    <div class="alert-title">Caisse du jour : <strong>${caisseDuJour} FCFA</strong></div>
    Somme totale des encaissements enregistrés aujourd'hui.
  </div>
</div>

<%-- Filtres connectés dynamiquement à l'instance DynamicTable --%>
<div class="card mb-4" style="padding:var(--space-4)">
  <div style="display:flex;gap:var(--space-3);flex-wrap:wrap;align-items:flex-end">

    <div style="flex:1;min-width:200px">
      <label class="form-label" for="searchPaiement">Rechercher</label>
      <div class="input-group">
        <span class="input-group-icon"><i class="ti ti-search" aria-hidden="true"></i></span>
        <input class="form-control" id="searchPaiement" type="search" placeholder="N° de reçu, nom du caissier…">
      </div>
    </div>

    <div style="min-width:160px">
      <label class="form-label" for="filterModePaiement">Mode de paiement</label>
      <select class="form-control" id="filterModePaiement">
        <option value="">Tous les modes</option>
        <%-- Génération dynamique des options basée sur la liste d'enums passée par le contrôleur --%>
        <c:forEach items="${listeModes}" var="mode">
          <option value="${mode.name()}">${mode.getLibelle()}</option>
        </c:forEach>
      </select>
    </div>
  </div>
</div>

<div class="card" style="padding:0">
  <div class="table-wrapper">
    <table>
      <thead>
        <tr>
          <th>N° Reçu</th>
          <th>Demande</th>
          <th>Montant</th>
          <th>Mode</th>
          <th>Caissier</th>
          <th>Date / Heure</th>
        </tr>
      </thead>
      <tbody id="paiement-table-body">
        <%-- Géré dynamiquement par le tableau.js --%>
      </tbody>
    </table>
  </div>

  <%-- Footer de table & Pagination dynamique --%>
  <div style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4);border-top:1px solid var(--border-divider)">
    <span id="paiement-pagination-info" style="font-size:var(--text-sm);color:var(--text-muted)"></span>
    <div id="paiement-pagination-buttons" style="display:flex;gap:6px"></div>
  </div>
</div>

<script>
  document.addEventListener("DOMContentLoaded", () => {
    // Sécurisation Null-Safe du JSON via JSP Expression Language
    const paiementsData = ${empty paiementsJson ? '[]' : paiementsJson};
    
    // Initialisation du tableau dynamique pour les paiements
    const paiementTable = new DynamicTable({
      data: paiementsData,
      tbodyId: 'paiement-table-body',
      infoId: 'paiement-pagination-info',
      pagerId: 'paiement-pagination-buttons',
      totalCountId: 'paiement-total-count',
      pageSize: 8,
      
      // Gabarit HTML d'une ligne (Template String)
      renderRow: (p) => `
      <tr>
      <td><code style="font-family:var(--font-mono);font-size:11px;color:var(--text-muted);font-weight:var(--fw-medium)">\${p.referenceRecu}</code></td>
      <td>\${p.typeDemandeLabel}</td>
      <td><strong>\${p.montantFormate} FCFA</strong></td>
      <td>
      <span class="badge badge-primary">
      <i class="ti \${
        p.modePaiement === 'ESPECES' ? 'ti-cash' :
        p.modePaiement === 'MOBILE_MONEY' ? 'ti-device-mobile' :
        p.modePaiement === 'CARTE_BANCAIRE' ? 'ti-credit-card' : 'ti-wallet'
      }" style="font-size:11px" aria-hidden="true"></i>
      \${p.modePaiementLabel}
      </span>
      </td>
      <td>\${p.caissierNom}</td>
      <td style="color:var(--text-muted)">\${p.datePaiementFormatee}</td>
      </tr>
      `,
      
      // Prédicat de filtrage multicritère (Recherche textuelle + Mode)
      filterFn: (item, filters) => {
        const query = (filters.query || '').toLowerCase();
        const matchQuery = !query ||
        item.referenceRecu.toLowerCase().includes(query) ||
        item.caissierNom.toLowerCase().includes(query) ||
        item.typeDemandeLabel.toLowerCase().includes(query);
        
        const matchMode = !filters.mode || item.modePaiement === filters.mode;
        
        return matchQuery && matchMode;
      }
    });
    
    // Liaison des contrôles de l'UI
    document.getElementById('searchPaiement').addEventListener('input', (e) => {
      paiementTable.setFilter('query', e.target.value);
    });
    
    document.getElementById('filterModePaiement').addEventListener('change', (e) => {
      paiementTable.setFilter('mode', e.target.value);
    });
    
    // Rendu initial au chargement
    paiementTable.render(1);
  });
</script>