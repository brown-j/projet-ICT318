<%-- Dashboard View: KPIs and Key Metrics --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- Directive pour activer la JSTL --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/dashboard.css">

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

<div class="grid cols-4 gap-4 mb-6" style="grid-template-columns:repeat(auto-fit,minmax(200px,1fr))">
  <c:forEach items="${kpis}" var="kpi">
    <article class="metric-card ${kpi.type}">
      <div class="metric-icon ${kpi.type}"><i class="${kpi.icon}" aria-hidden="true"></i></div>
      <div class="metric-value"> ${kpi.value} </div>
      <div class="metric-label"> ${kpi.label} </div>
      <div class="metric-trend ${kpi.trendType}"><i class="${kpi.trendIcon}" style="font-size:11px" aria-hidden="true"></i> ${kpi.trendValue} </div>
    </article>
  </c:forEach>
</div>

<div class="grid gap-5 mb-5" style="grid-template-columns:minmax(0,2fr) minmax(0,1fr)">
  <div class="card">
    <div class="card-header">
      <div>
        <div class="card-title">Évolution des actes civils</div>
        <div class="card-subtitle">12 derniers mois</div>
      </div>
      <div style="display:flex;gap:8px;align-items:center">
        <span class="tag tag-teal"><i class="ti ti-point-filled" style="font-size:10px" aria-hidden="true"></i>
          Actes</span>
          <span class="badge badge-neutral" style="font-size:10px">2025</span>
        </div>
      </div>
      <div class="chart-wrap">
        <canvas id="chartActes" aria-label="Graphique évolution actes civils"></canvas>
      </div>
    </div>

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
        <c:forEach items="${repartitionTypes}" var="stat">
          <div style="display:flex;justify-content:space-between;align-items:center">
            <span style="display:flex;align-items:center;gap:6px;font-size:12px;color:var(--text-secondary)">
              <span style="width:10px;height:10px;border-radius:50%;background:var(--color-${stat.colorClass});display:inline-block"></span>
              ${stat.label}
            </span>
            <span style="font-size:12px;font-weight:var(--fw-semi);color:var(--text-primary)">${stat.percentage}%</span>
          </div>
        </c:forEach>
      </div>
    </div>
  </div>

  <div class="grid gap-5" style="grid-template-columns:minmax(0,1fr) minmax(0,1fr)">

    <div class="card" style="padding:0">
      <div class="card-header" style="padding:var(--space-4) var(--space-4) 0">
        <div>
          <div class="card-title">Demandes récentes</div>
          <div class="card-subtitle">En attente de traitement</div>
        </div>
        <a href="${pageContext.request.contextPath}/demande/liste" class="btn btn-ghost btn-sm">
          Voir tout <i class="ti ti-arrow-right" aria-hidden="true"></i>
        </a>
      </div>
      <div style="margin-top:var(--space-3)">

        <%-- Boucle sur le ViewModel --%>
        <c:forEach items="${recentDemandesList}" var="demande">
          <div class="demande-row">
            <span class="demande-num">${demande.numeroSuivi}</span>
            <div class="demande-info">
              <strong>${demande.titreDemande}</strong>
              <span>${demande.infosDemandeur} — ${demande.tempsEcoule}</span>
            </div>
            <%-- Vue "Stupide" : concaténation directe de la classe couleur et du label --%>
            <span class="badge badge-${demande.colorClass}">${demande.badgeLabel}</span>
          </div>
        </c:forEach>

        <%-- Message de repli si la liste est vide --%>
        <c:if test="${empty recentDemandesList}">
          <div style="padding: var(--space-4); text-align: center; color: var(--text-muted); font-size: 14px;">
            <p style="margin-top: 4px;">Aucune demande administrative récente.</p>
          </div>
        </c:if>

      </div>
    </div>

    <div style="display:flex;flex-direction:column;gap:var(--space-5)">

      <div class="card">
        <div class="card-header">
          <div class="card-title">Actions rapides</div>
        </div>
        <div class="grid gap-3" style="grid-template-columns:repeat(4,1fr)">
          <a class="quick-action" href="#" onclick="openModal('citoyen');return false">
            <i class="ti ti-user-plus" style="color:var(--color-primary)" aria-hidden="true"></i>
            <span>Nouveau citoyen</span>
          </a>
          <a class="quick-action" href="#" onclick="openModal('acte');return false">
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

      <!-- Activité récente (DYNAMISÉE) -->
      <div class="card">
        <div class="card-header">
          <div class="card-title">Activité récente</div>
          <button class="btn btn-ghost btn-sm">Tout <i class="ti ti-arrow-right" aria-hidden="true"></i></button>
        </div>
        <div>

          <c:forEach items="${recentActivitesList}" var="activite">
            <div class="activity-item">

              <!-- Injection dynamique de la couleur (ex: --c-primary-50, --c-success-600) -->
              <div class="activity-icon" style="background:var(--c-${activite.colorClass}-50); color:var(--c-${activite.colorClass}-600)">
                <i class="${activite.iconClass}" aria-hidden="true"></i>
              </div>

              <div class="activity-text">
                <strong>${activite.titre}</strong>
                <p>${activite.description}</p>
              </div>

              <div class="activity-time">${activite.heureFormatee}</div>
            </div>
          </c:forEach>

          <c:if test="${empty recentActivitesList}">
            <div style="padding: var(--space-3); text-align: center; color: var(--text-muted); font-size: 13px;">
              Aucune activité récente.
            </div>
          </c:if>

        </div>
      </div>

      <script>
        document.addEventListener('DOMContentLoaded', () => {
          // 1. JSP traduit le JSON pour que le navigateur le comprenne
          // (Si evolutionDataJson est null/vide, on passe un objet vide {} pour éviter un crash)
          const serverData = JSON.parse('${not empty evolutionDataJson ? evolutionDataJson : "{}"}');
          
          // 2. On lance la fonction de notre fichier externe
          initChartsServer(serverData);
        });
      </script>