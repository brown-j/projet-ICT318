<%-- JSP Include: Top Navigation Bar with Search, Theme & User Menu --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<header class="topbar" role="banner">
  <button class="icon-btn" id="menuBtn" onclick="document.getElementById('sidebar').classList.toggle('open')" aria-label="Ouvrir le menu" style="display:none">
    <i class="ti ti-menu-2" aria-hidden="true"></i>
  </button>

  <div class="topbar-search" role="search">
    <i class="ti ti-search" aria-hidden="true"></i>
    <input type="search" placeholder="Rechercher citoyen, acte, demande…" aria-label="Rechercher">
    <kbd style="font-size:10px;color:var(--text-subtle);background:var(--bg-surface);padding:2px 6px;border-radius:4px;border:1px solid var(--border-default);white-space:nowrap">⌘K</kbd>
  </div>

  <div class="topbar-actions">
    <div class="weather-chip" aria-label="Météo">
      <i class="ti ti-sun" style="font-size:15px;color:var(--c-accent-400)" aria-hidden="true"></i>
      28°C Yaoundé
    </div>

    <button class="icon-btn" title="Notifications" aria-label="Notifications (3 non lues)">
      <i class="ti ti-bell" aria-hidden="true"></i>
      <span class="notif-dot" aria-hidden="true"></span>
    </button>
    <button class="icon-btn" title="Messages" aria-label="Messages">
      <i class="ti ti-mail" aria-hidden="true"></i>
    </button>

    <!-- Theme toggle -->
    <button class="icon-btn" id="themeBtn" onclick="toggleTheme()" title="Changer de thème" aria-label="Basculer thème clair/sombre">
      <i class="ti ti-moon" aria-hidden="true" id="themeIcon"></i>
    </button>

    <div style="height:24px" class="divider-vertical"></div>

    <div style="display:flex;align-items:center;gap:8px;cursor:pointer" title="Mon profil">
      <div class="avatar">KN</div>
      <div style="display:flex;flex-direction:column">
        <span style="font-size:var(--text-sm);font-weight:var(--fw-medium);color:var(--text-primary)">Koffi N.</span>
        <span style="font-size:var(--text-xs);color:var(--text-muted)">Officier</span>
      </div>
      <i class="ti ti-chevron-down" style="font-size:14px;color:var(--text-muted)" aria-hidden="true"></i>
    </div>
  </div>
</header>

