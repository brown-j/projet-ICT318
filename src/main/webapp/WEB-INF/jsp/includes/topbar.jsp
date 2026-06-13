<%-- JSP Include: Top Navigation Bar with Search, Theme & User Menu --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<header class="topbar" role="banner">
  <button class="icon-btn" id="menuBtn" onclick="document.getElementById('sidebar').classList.toggle('open')" aria-label="Ouvrir le menu" style="display:none">
    <i class="ti ti-menu-2" aria-hidden="true"></i>
  </button>

  <div class="topbar-search" role="search">
    <i class="ti ti-search" aria-hidden="true"></i>
    <%-- Optionnel : Tu pourras englober l'input dans un <form action="${pageContext.request.contextPath}/recherche"> plus tard --%>
    <input type="search" placeholder="Rechercher citoyen, acte, demande…" aria-label="Rechercher">
    <kbd style="font-size:10px;color:var(--text-subtle);background:var(--bg-surface);padding:2px 6px;border-radius:4px;border:1px solid var(--border-default);white-space:nowrap">⌘K</kbd>
  </div>

  <div class="topbar-actions">
    <%-- Météo : Tu peux dynamiser la température si tu as une API, sinon valeur par défaut --%>
    <div class="weather-chip" aria-label="Météo">
      <i class="ti ti-sun" style="font-size:15px;color:var(--c-accent-400)" aria-hidden="true"></i>
      ${not empty tempActuelle ? tempActuelle : '28'}°C Yaoundé
    </div>

    <button class="icon-btn" title="Notifications" aria-label="Notifications">
      <i class="ti ti-bell" aria-hidden="true"></i>
      <%-- Affiche le point rouge uniquement s'il y a des notifications non lues passées en attribut --%>
      <c:if test="${not empty unreadNotifs and unreadNotifs > 0}">
        <span class="notif-dot" aria-hidden="true"></span>
      </c:if>
    </button>
    <button class="icon-btn" title="Messages" aria-label="Messages">
      <i class="ti ti-mail" aria-hidden="true"></i>
    </button>

    <!-- Theme toggle -->
    <button class="icon-btn" id="themeBtn" onclick="toggleTheme()" title="Changer de thème" aria-label="Basculer thème clair/sombre">
      <i class="ti ti-moon" aria-hidden="true" id="themeIcon"></i>
    </button>

    <div style="height:24px" class="divider-vertical"></div>

    <%-- PROFIL UTILISATEUR DYNAMIQUE --%>
    <div style="display:flex;align-items:center;gap:8px;cursor:pointer" title="Mon profil">
      <div class="avatar">
        <%-- Initiales (ex: Paul Tchakounté -> PT) --%>
        ${fn:toUpperCase(fn:substring(sessionScope.user.prenom, 0, 1))}${fn:toUpperCase(fn:substring(sessionScope.user.nom, 0, 1))}
      </div>
      <div style="display:flex;flex-direction:column">
        <span style="font-size:var(--text-sm);font-weight:var(--fw-medium);color:var(--text-primary)">
          <%-- Prénom + Initiale du nom (ex: Paul T.) --%>
          ${sessionScope.user.prenom} ${fn:toUpperCase(fn:substring(sessionScope.user.nom, 0, 1))}.
        </span>
        <span style="font-size:var(--text-xs);color:var(--text-muted)">
          <%-- Rôle (ex: Super Administrateur) --%>
          ${sessionScope.user.role.nom}
        </span>
      </div>
      <i class="ti ti-chevron-down" style="font-size:14px;color:var(--text-muted)" aria-hidden="true"></i>
    </div>
  </div>
</header>