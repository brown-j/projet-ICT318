<%-- JSP Include: Sidebar Navigation --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<aside class="sidebar" id="sidebar">
    <div class="sidebar-logo">
        <div class="sidebar-logo-icon">
            <i class="ti ti-building-community" aria-hidden="true"></i>
        </div>
        <div>
            <div class="sidebar-logo-text">Mairie CAM</div>
            <div class="sidebar-logo-sub">Gestion Communale</div>
        </div>
    </div>

    <nav class="sidebar-nav" role="navigation" aria-label="Navigation principale">
        <div class="nav-section">
            <div class="nav-section-label">Principal</div>
            <a class="nav-item" href="${pageContext.request.contextPath}/dashboard" data-title="Tableau de bord">
                <i class="ti ti-layout-dashboard" aria-hidden="true"></i>
                Tableau de bord
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/citoyen/liste" data-title="Citoyens">
                <i class="ti ti-users" aria-hidden="true"></i>
                Citoyens
                <c:if test="${not empty globalCountCitoyens}">
                    <span class="nav-badge">${globalCountCitoyens}</span>
                </c:if>
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/acte/liste" data-title="Actes civils">
                <i class="ti ti-file-certificate" aria-hidden="true"></i>
                Actes civils
                <c:if test="${not empty globalCountActes}">
                    <span class="nav-badge">${globalCountActes}</span>
                </c:if>
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/demande/liste" data-title="Demandes">
                <i class="ti ti-clipboard-list" aria-hidden="true"></i>
                Demandes
                <c:if test="${not empty globalCountDemandes}">
                    <span class="nav-badge" style="background:var(--c-accent-50);color:var(--c-accent-600)">${globalCountDemandes}</span>
                </c:if>
            </a>
        </div>

        <div class="nav-section">
            <div class="nav-section-label">Finances</div>
            <a class="nav-item" href="${pageContext.request.contextPath}/paiement/liste" data-title="Paiements">
                <i class="ti ti-credit-card" aria-hidden="true"></i>
                Paiements
            </a>
            <!--<a class="nav-item" href="${pageContext.request.contextPath}/rapport/export" data-title="Rapports">
            <i class="ti ti-chart-bar" aria-hidden="true"></i>
            Rapports
            </a>-->
        </div>

        <div class="nav-section">
            <div class="nav-section-label">Administration</div>
            <!--<a class="nav-item" href="${pageContext.request.contextPath}/agenda/rendez-vous" data-title="Agenda">
            <i class="ti ti-calendar" aria-hidden="true"></i>
            Agenda
            </a>-->
            <a class="nav-item" href="${pageContext.request.contextPath}/officier/liste" data-title="Officiers">
                <i class="ti ti-user-shield" aria-hidden="true"></i>
                Officiers
            </a>
            <a class="nav-item" href="${pageContext.request.contextPath}/parametre/configuration" data-title="Paramètres">
                <i class="ti ti-settings" aria-hidden="true"></i>
                Paramètres
            </a>
        </div>
    </nav>

    <div class="sidebar-footer">
        <div class="nav-item" style="cursor:default">
            <div class="avatar" style="width:28px;height:28px;font-size:11px" title="${sessionScope.user.prenom} ${sessionScope.user.nom}">
                <%-- Extraction automatique des initiales (Ex: Paul Tchakounté -> PT) --%>
                ${fn:toUpperCase(fn:substring(sessionScope.user.prenom, 0, 1))}${fn:toUpperCase(fn:substring(sessionScope.user.nom, 0, 1))}
            </div>
            <div style="flex:1;min-width:0">
                <div style="font-size:var(--text-sm);font-weight:var(--fw-medium);color:var(--text-primary);white-space:nowrap;overflow:hidden;text-overflow:ellipsis">
                    ${sessionScope.user.prenom} ${sessionScope.user.nom}
                </div>
                <div style="font-size:var(--text-xs);color:var(--text-muted)">
                    <%-- Affiche le titre de l'officier, s'il est null on se rabat sur le nom de son rôle --%>
                    ${not empty sessionScope.user.titre ? sessionScope.user.titre : sessionScope.user.role.nom}
                </div>
            </div>
            <a href="${pageContext.request.contextPath}/logout" class="icon-btn" style="width:28px;height:28px" title="Déconnexion" aria-label="Déconnexion">
                <i class="ti ti-logout" style="font-size:15px" aria-hidden="true"></i>
            </a>
        </div>
    </div>
</aside>