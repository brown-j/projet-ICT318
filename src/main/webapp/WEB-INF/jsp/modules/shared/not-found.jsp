<%-- Page générique : Fonctionnalité non réalisée / En cours de construction --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div style="display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 65vh; text-align: center; padding: var(--space-6)">

    <%-- Bulle d'icône d'avertissement stylisée --%>
    <div style="width: 80px; height: 80px; border-radius: 50%; background: #fff4e5; display: flex; align-items: center; justify-content: center; margin-bottom: var(--space-4)">
        <i class="ti ti-cone" style="font-size: 38px; color: #ff9800" aria-hidden="true"></i>
    </div>

    <%-- Messages textuels --%>
    <h1 style="font-size: 22px; font-weight: var(--fw-medium); margin-bottom: var(--space-2); color: var(--text-main)">
        Fonctionnalité ou Ressource non disponible
    </h1>

    <p style="color: var(--text-muted); max-width: 420px; margin-bottom: var(--space-5); font-size: var(--text-sm); line-height: 1.6">
        Cette section n'a pas encore été réalisée. Nos équipes travaillent activement dessus pour la rendre disponible dans une prochaine version.
    </p>

    <%-- Actions de secours --%>
    <div style="display: flex; gap: var(--space-3)">
        <button onclick="window.history.back()" class="btn btn-secondary btn-sm">
            <i class="ti ti-arrow-left" aria-hidden="true"></i> Retour
        </button>
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary btn-sm">
            <i class="ti ti-smart-home" aria-hidden="true"></i> Accueil
        </a>
    </div>

</div>