<%-- Dashboard View: KPIs and Key Metrics --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div class="page-header">
  <div class="page-header-left">
    <h1>Tableau de bord</h1>
    <p>Lundi 2 juin 2025 — Vue d'ensemble de la commune</p>
  </div>
  <div class="page-header-actions">
    <button class="btn btn-ghost btn-sm" onclick="showToast('Rapport exporté','success')">
      <i class="ti ti-download" aria-hidden="true"></i> Exporter
    </button>
    <button class="btn btn-primary btn-sm" onclick="openModal('citoyen')">
      <i class="ti ti-plus" aria-hidden="true"></i> Nouveau citoyen
    </button>
  </div>
</div>

<!-- KPI Cards -->
<div class="grid cols-4 gap-4 mb-6" style="grid-template-columns:repeat(auto-fit,minmax(200px,1fr))">
  <article class="metric-card primary">
    <div class="metric-icon primary"><i class="ti ti-users" aria-hidden="true"></i></div>
    <div class="metric-value">1 284</div>
    <div class="metric-label">Citoyens enregistrés</div>
    <div class="metric-trend up"><i class="ti ti-trending-up" style="font-size:11px" aria-hidden="true"></i> +34 ce mois</div>
  </article>

  <article class="metric-card secondary">
    <div class="metric-icon secondary"><i class="ti ti-file-certificate" aria-hidden="true"></i></div>
    <div class="metric-value">47</div>
    <div class="metric-label">Actes ce mois</div>
    <div class="metric-trend up"><i class="ti ti-trending-up" style="font-size:11px" aria-hidden="true"></i> +12% vs N-1</div>
  </article>

  <article class="metric-card accent">
    <div class="metric-icon accent"><i class="ti ti-clipboard-list" aria-hidden="true"></i></div>
    <div class="metric-value">12</div>
    <div class="metric-label">Demandes en attente</div>
    <div class="metric-trend down"><i class="ti ti-trending-down" style="font-size:11px" aria-hidden="true"></i> 3 urgentes</div>
  </article>

  <article class="metric-card success">
    <div class="metric-icon success"><i class="ti ti-currency-franc" aria-hidden="true"></i></div>
    <div class="metric-value">842 K</div>
    <div class="metric-label">Recettes FCFA (mois)</div>
    <div class="metric-trend up"><i class="ti ti-trending-up" style="font-size:11px" aria-hidden="true"></i> +8% objectif</div>
  </article>
</div>

<!-- Charts row -->
<div class="grid gap-5 mb-5" style="grid-template-columns:minmax(0,2fr) minmax(0,1fr)">
  <!-- Actes par mois -->
  <div class="card">
    <div class="card-header">
      <div>
        <div class="card-title">Évolution des actes civils</div>
        <div class="card-subtitle">12 derniers mois</div>
      </div>
      <div style="display:flex;gap:8px;align-items:center">
        <span class="tag tag-teal"><i class="ti ti-point-filled" style="font-size:10px" aria-hidden="true"></i> Actes</span>
        <span class="badge badge-neutral" style="font-size:10px">2025</span>
      </div>
    </div>
    <div class="chart-wrap">
      <canvas id="chartActes" aria-label="Graphique évolution actes civils"></canvas>
    </div>
  </div>

  <!-- Répartition types -->
  <div class="card">
    <div class="card-header">
      <div>
        <div class="card-title">Types d'actes</div>
        <div class="card-subtitle">Répartition globale</div>
      </div>
    </div>
    <div class="chart-wrap" style="height:170px">
      <canvas id="chartTypes" aria-label="Répartition types d'actes"></canvas>
    </div>
    <div style="display:flex;flex-direction:column;gap:6px;margin-top:12px">
      <div style="display:flex;justify-content:space-between;align-items:center">
        <span style="display:flex;align-items:center;gap:6px;font-size:12px;color:var(--text-secondary)">
          <span style="width:10px;height:10px;border-radius:50%;background:var(--color-primary);display:inline-block"></span> Naissances
        </span>
        <span style="font-size:12px;font-weight:var(--fw-semi);color:var(--text-primary)">38%</span>
      </div>
      <div style="display:flex;justify-content:space-between;align-items:center">
        <span style="display:flex;align-items:center;gap:6px;font-size:12px;color:var(--text-secondary)">
          <span style="width:10px;height:10px;border-radius:50%;background:var(--color-secondary);display:inline-block"></span> Mariages
        </span>
        <span style="font-size:12px;font-weight:var(--fw-semi);color:var(--text-primary)">27%</span>
      </div>
      <div style="display:flex;justify-content:space-between;align-items:center">
        <span style="display:flex;align-items:center;gap:6px;font-size:12px;color:var(--text-secondary)">
          <span style="width:10px;height:10px;border-radius:50%;background:var(--c-accent-400);display:inline-block"></span> Décès
        </span>
        <span style="font-size:12px;font-weight:var(--fw-semi);color:var(--text-primary)">20%</span>
      </div>
      <div style="display:flex;justify-content:space-between;align-items:center">
        <span style="display:flex;align-items:center;gap:6px;font-size:12px;color:var(--text-secondary)">
          <span style="width:10px;height:10px;border-radius:50%;background:var(--c-info-500);display:inline-block"></span> Autres
        </span>
        <span style="font-size:12px;font-weight:var(--fw-semi);color:var(--text-primary)">15%</span>
      </div>
    </div>
  </div>
</div>

<!-- Bottom row -->
<div class="grid gap-5" style="grid-template-columns:minmax(0,1fr) minmax(0,1fr)">
  <!-- Demandes récentes -->
  <div class="card" style="padding:0">
    <div class="card-header" style="padding:var(--space-4) var(--space-4) 0">
      <div>
        <div class="card-title">Demandes récentes</div>
        <div class="card-subtitle">En attente de traitement</div>
      </div>
      <button class="btn btn-ghost btn-sm">Voir tout <i class="ti ti-arrow-right" aria-hidden="true"></i></button>
    </div>
    <div style="margin-top:var(--space-3)">
      <div class="demande-row">
        <span class="demande-num">DEM-2025-0089</span>
        <div class="demande-info"><strong>Cert. de résidence</strong><span>Amina Fouda — il y a 2h</span></div>
        <span class="badge badge-warning">En cours</span>
      </div>
      <div class="demande-row">
        <span class="demande-num">DEM-2025-0088</span>
        <div class="demande-info"><strong>Acte de naissance</strong><span>Jean-Paul Mbarga — il y a 4h</span></div>
        <span class="badge badge-info">Soumise</span>
      </div>
      <div class="demande-row">
        <span class="demande-num">DEM-2025-0087</span>
        <div class="demande-info"><strong>Autorisation construire</strong><span>Société BTP Cam — hier</span></div>
        <span class="badge badge-accent" style="background:var(--c-error-50);color:var(--c-error-700)">Urgente</span>
      </div>
    </div>
  </div>

  <!-- Activité + actions rapides -->
  <div style="display:flex;flex-direction:column;gap:var(--space-5)">
    <!-- Quick actions -->
    <div class="card">
      <div class="card-header">
        <div class="card-title">Actions rapides</div>
      </div>
      <div class="grid gap-3" style="grid-template-columns:repeat(4,1fr)">
        <a class="quick-action" href="#" onclick="openModal('citoyen');return false">
          <i class="ti ti-user-plus" style="color:var(--color-primary)" aria-hidden="true"></i>
          <span>Nouveau citoyen</span>
        </a>
        <a class="quick-action" href="#">
          <i class="ti ti-file-plus" style="color:var(--color-secondary)" aria-hidden="true"></i>
          <span>Créer acte</span>
        </a>
        <a class="quick-action" href="#" onclick="showToast('Caisse ouverte','info');return false">
          <i class="ti ti-cash" style="color:var(--color-accent)" aria-hidden="true"></i>
          <span>Encaisser</span>
        </a>
        <a class="quick-action" href="#" onclick="showToast('RDV planifié','success');return false">
          <i class="ti ti-calendar-plus" style="color:var(--c-info-600)" aria-hidden="true"></i>
          <span>Rendez-vous</span>
        </a>
      </div>
    </div>
  </div>
</div>

<script>
  // Initialize charts when dashboard loads
  document.addEventListener('DOMContentLoaded', () => {
    if (typeof initCharts === 'function') initCharts();
  });
</script>

