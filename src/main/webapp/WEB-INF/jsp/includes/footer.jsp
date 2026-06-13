<%-- JSP Include: Footer with Script Includes --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- ⚠️ N'oublie pas d'importer la JSTL si ce n'est pas fait --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="toast-container" id="toastContainer" role="status" aria-live="polite"></div>

<script src="${pageContext.request.contextPath}/resources/js/searchable-select.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/charts-init.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/dynamic-table.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/app-core.js"></script>

<%-- 🌟 PONT ENTRE LA SESSION JAVA ET LA FONCTION JS 🌟 --%>
<c:if test="${not empty sessionScope.toastMsg}">
    <script>
        // On attend que le DOM soit prêt et que app-core.js soit chargé
        document.addEventListener("DOMContentLoaded", function() {
            // Appel de ta fonction JS native avec les données du serveur
            // On utilise une valeur par défaut 'info' si le type n'est pas fourni
            showToast("${sessionScope.toastMsg}", "${empty sessionScope.toastType ? 'info' : sessionScope.toastType}");
        });
    </script>

    <%-- 🛑 On nettoie la session pour que le message ne réapparaisse pas si on rafraîchit (F5) --%>
    <c:remove var="toastMsg" scope="session" />
    <c:remove var="toastType" scope="session" />
</c:if>

<%-- ================================================================= --%>
<%-- MODALE GLOBALE UNIQUE (Réceptacle dynamique)                      --%>
<%-- ================================================================= --%>
<c:if test="${not empty modalContent}">
    <div class="modal-backdrop ${autoOpenModal ? 'show' : ''}" id="modal-global" role="dialog" aria-modal="true">
        <div class="modal">
            <div class="modal-header">
                <h2 class="modal-title" style="font-size:var(--text-xl)">
                    ${empty modalTitle ? 'Formulaire' : modalTitle}
                </h2>
                <button class="icon-btn" onclick="closeModal('global')" aria-label="Fermer">
                    <i class="ti ti-x" aria-hidden="true"></i>
                </button>
            </div>

            ${modalContent}

        </div>
    </div>
</c:if>