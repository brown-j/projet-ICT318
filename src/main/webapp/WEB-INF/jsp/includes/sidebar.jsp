<%-- JSP Include: Sidebar Navigation --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<aside class="sidebar" id="sidebar">
  <div class="sidebar-logo">
    <div class="sidebar-logo-icon">
      <i class="ti ti-building-community" aria-hidden="true"></i>
    </div>
    <div>
      <div class="sidebar-logo-text">Mairie Ydé III</div>
      <div class="sidebar-logo-sub">Gestion Communale</div>
    </div>
  </div>

  <nav class="sidebar-nav" role="navigation" aria-label="Navigation principale">
    <div class="nav-section">
      <div class="nav-section-label">Principal</div>
      <a class="nav-item active" href="${pageContext.request.contextPath}/dashboard" aria-current="page">
        <i class="ti ti-layout-dashboard" aria-hidden="true"></i>
        Tableau de bord
      </a>
      <a class="nav-item" href="${pageContext.request.contextPath}/citoyen/liste">
        <i class="ti ti-users" aria-hidden="true"></i>
        Citoyens
        <span class="nav-badge">1 284</span>
      </a>
      <a class="nav-item" href="${pageContext.request.contextPath}/acte/liste">
        <i class="ti ti-file-certificate" aria-hidden="true"></i>
        Actes civils
        <span class="nav-badge">47</span>
      </a>
      <a class="nav-item" href="${pageContext.request.contextPath}/demande/liste">
        <i class="ti ti-clipboard-list" aria-hidden="true"></i>
        Demandes
        <span class="nav-badge" style="background:var(--c-accent-50);color:var(--c-accent-600)">12</span>
      </a>
    </div>

    <div class="nav-section">
      <div class="nav-section-label">Finances</div>
      <a class="nav-item" href="${pageContext.request.contextPath}/paiement/liste">
        <i class="ti ti-credit-card" aria-hidden="true"></i>
        Paiements
      </a>
      <a class="nav-item" href="${pageContext.request.contextPath}/rapport/export">
        <i class="ti ti-chart-bar" aria-hidden="true"></i>
        Rapports
      </a>
    </div>

    <div class="nav-section">
      <div class="nav-section-label">Administration</div>
      <a class="nav-item" href="${pageContext.request.contextPath}/agenda/rendez-vous">
        <i class="ti ti-calendar" aria-hidden="true"></i>
        Agenda
      </a>
      <a class="nav-item" href="${pageContext.request.contextPath}/officier/liste">
        <i class="ti ti-user-shield" aria-hidden="true"></i>
        Officiers
      </a>
      <a class="nav-item" href="${pageContext.request.contextPath}/parametre/configuration">
        <i class="ti ti-settings" aria-hidden="true"></i>
        Paramètres
      </a>
    </div>
  </nav>

  <div class="sidebar-footer">
    <div class="nav-item" style="cursor:default">
      <div class="avatar" style="width:28px;height:28px;font-size:11px">KN</div>
      <div style="flex:1;min-width:0">
        <div style="font-size:var(--text-sm);font-weight:var(--fw-medium);color:var(--text-primary);white-space:nowrap;overflow:hidden;text-overflow:ellipsis">Koffi Nkemdirim</div>
        <div style="font-size:var(--text-xs);color:var(--text-muted)">Officier principal</div>
      </div>
      <a href="${pageContext.request.contextPath}/logout" class="icon-btn" style="width:28px;height:28px" title="Déconnexion" aria-label="Déconnexion">
        <i class="ti ti-logout" style="font-size:15px" aria-hidden="true"></i>
      </a>
    </div>
  </div>
</aside>

