<%-- Master Layout Template: Base Structure for All Pages --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr" data-theme="light">
<head>
    <jsp:include page="../includes/head.jsp" />
</head>
<body>
<div class="app-shell">
    <jsp:include page="../includes/sidebar.jsp" />
    
    <div class="main-area">
        <jsp:include page="../includes/topbar.jsp" />
        
        <main class="page-content" role="main" id="pageContent">
            <!-- Breadcrumb -->
            <nav class="breadcrumb mb-4" aria-label="Fil d'Ariane">
                <a href="${pageContext.request.contextPath}/dashboard">Accueil</a>
                <i class="ti ti-chevron-right breadcrumb-sep" style="font-size:13px" aria-hidden="true"></i>
                <span class="breadcrumb-current" id="breadcrumbCurrent">Page</span>
            </nav>
            
            <!-- Dynamic View Injection Point -->
            <jsp:include page="${view}" />
        </main>
    </div>
</div>

<!-- Footer with Global Modals and Scripts -->
<jsp:include page="../includes/footer.jsp" />
</body>
</html>

