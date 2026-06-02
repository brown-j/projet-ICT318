<%-- Citizens List View: Search, Filter & Pagination --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div class="page-header">
  <div class="page-header-left">
    <h1>Gestion des citoyens</h1>
    <p>1 284 citoyens enregistrés dans la commune</p>
  </div>
  <div class="page-header-actions">
    <button class="btn btn-ghost btn-sm"><i class="ti ti-download" aria-hidden="true"></i> Exporter</button>
    <button class="btn btn-primary btn-sm" onclick="openModal('citoyen')"><i class="ti ti-plus" aria-hidden="true"></i> Nouveau</button>
  </div>
</div>

<div class="card mb-4" style="padding:var(--space-4)">
  <div style="display:flex;gap:var(--space-3);flex-wrap:wrap;align-items:flex-end">
    <div style="flex:1;min-width:200px">
      <label class="form-label" for="searchCitoyen">Rechercher</label>
      <div class="input-group">
        <span class="input-group-icon"><i class="ti ti-search" aria-hidden="true"></i></span>
        <input class="form-control" id="searchCitoyen" type="search" placeholder="Nom, prénom, NIN…">
      </div>
    </div>
    <div style="min-width:160px">
      <label class="form-label" for="filterStatut">Statut</label>
      <select class="form-control" id="filterStatut">
        <option value="">Tous les statuts</option>
        <option>Actif</option>
        <option>Archivé</option>
        <option>Décédé</option>
      </select>
    </div>
    <div style="min-width:160px">
      <label class="form-label" for="filterQuartier">Quartier</label>
      <select class="form-control" id="filterQuartier">
        <option value="">Tous</option>
        <option>Bastos</option>
        <option>Biyem-Assi</option>
        <option>Melen</option>
        <option>Nlongkak</option>
      </select>
    </div>
    <button class="btn btn-secondary btn-sm" style="height:40px"><i class="ti ti-filter" aria-hidden="true"></i> Filtrer</button>
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
      <tbody>
        <tr>
          <td><code style="font-family:var(--font-mono);font-size:11px;color:var(--text-muted)">CM-2025-10847</code></td>
          <td><strong style="font-weight:var(--fw-medium)">Abena Zoa Marie</strong></td>
          <td style="color:var(--text-muted)">14/03/1990</td>
          <td>Biyem-Assi</td>
          <td><span class="badge badge-primary">Marié(e)</span></td>
          <td><span class="badge badge-success"><i class="ti ti-point-filled" style="font-size:10px" aria-hidden="true"></i> Actif</span></td>
          <td style="text-align:right">
            <div style="display:flex;gap:6px;justify-content:flex-end">
              <a href="${pageContext.request.contextPath}/citoyen/detail?id=1" class="btn btn-ghost btn-sm btn-icon" title="Voir" aria-label="Voir Abena Zoa Marie"><i class="ti ti-eye" aria-hidden="true"></i></a>
              <button class="btn btn-ghost btn-sm btn-icon" title="Modifier" aria-label="Modifier"><i class="ti ti-edit" aria-hidden="true"></i></button>
            </div>
          </td>
        </tr>
        <tr>
          <td><code style="font-family:var(--font-mono);font-size:11px;color:var(--text-muted)">CM-2025-10831</code></td>
          <td><strong style="font-weight:var(--fw-medium)">Mbarga Jean-Paul</strong></td>
          <td style="color:var(--text-muted)">07/11/1985</td>
          <td>Bastos</td>
          <td><span class="badge badge-neutral">Célibataire</span></td>
          <td><span class="badge badge-success"><i class="ti ti-point-filled" style="font-size:10px" aria-hidden="true"></i> Actif</span></td>
          <td style="text-align:right">
            <div style="display:flex;gap:6px;justify-content:flex-end">
              <a href="${pageContext.request.contextPath}/citoyen/detail?id=2" class="btn btn-ghost btn-sm btn-icon" aria-label="Voir Mbarga Jean-Paul"><i class="ti ti-eye" aria-hidden="true"></i></a>
              <button class="btn btn-ghost btn-sm btn-icon" aria-label="Modifier"><i class="ti ti-edit" aria-hidden="true"></i></button>
            </div>
          </td>
        </tr>
        <tr>
          <td><code style="font-family:var(--font-mono);font-size:11px;color:var(--text-muted)">CM-2024-09412</code></td>
          <td><strong style="font-weight:var(--fw-medium)">Essama Paul</strong></td>
          <td style="color:var(--text-muted)">22/05/1972</td>
          <td>Melen</td>
          <td><span class="badge badge-primary">Marié(e)</span></td>
          <td><span class="badge badge-warning">Archivé</span></td>
          <td style="text-align:right">
            <div style="display:flex;gap:6px;justify-content:flex-end">
              <a href="${pageContext.request.contextPath}/citoyen/detail?id=3" class="btn btn-ghost btn-sm btn-icon" aria-label="Voir Essama Paul"><i class="ti ti-eye" aria-hidden="true"></i></a>
              <button class="btn btn-ghost btn-sm btn-icon" aria-label="Modifier"><i class="ti ti-edit" aria-hidden="true"></i></button>
            </div>
          </td>
        </tr>
        <tr>
          <td><code style="font-family:var(--font-mono);font-size:11px;color:var(--text-muted)">CM-2024-08801</code></td>
          <td><strong style="font-weight:var(--fw-medium)">Fouda Amina</strong></td>
          <td style="color:var(--text-muted)">30/08/1999</td>
          <td>Nlongkak</td>
          <td><span class="badge badge-neutral">Célibataire</span></td>
          <td><span class="badge badge-success"><i class="ti ti-point-filled" style="font-size:10px" aria-hidden="true"></i> Actif</span></td>
          <td style="text-align:right">
            <div style="display:flex;gap:6px;justify-content:flex-end">
              <a href="${pageContext.request.contextPath}/citoyen/detail?id=4" class="btn btn-ghost btn-sm btn-icon" aria-label="Voir Fouda Amina"><i class="ti ti-eye" aria-hidden="true"></i></a>
              <button class="btn btn-ghost btn-sm btn-icon" aria-label="Modifier"><i class="ti ti-edit" aria-hidden="true"></i></button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
  <div style="display:flex;align-items:center;justify-content:space-between;padding:var(--space-3) var(--space-4);border-top:1px solid var(--border-divider)">
    <span style="font-size:var(--text-sm);color:var(--text-muted)">Affichage 1-4 sur 1 284 entrées</span>
    <div style="display:flex;gap:6px">
      <button class="btn btn-ghost btn-sm" disabled><i class="ti ti-chevron-left" aria-hidden="true"></i></button>
      <button class="btn btn-primary btn-sm" style="width:32px;padding:0">1</button>
      <button class="btn btn-ghost btn-sm" style="width:32px;padding:0">2</button>
      <button class="btn btn-ghost btn-sm" style="width:32px;padding:0">3</button>
      <button class="btn btn-ghost btn-sm"><i class="ti ti-chevron-right" aria-hidden="true"></i></button>
    </div>
  </div>
</div>

