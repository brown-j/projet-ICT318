<!-- Path: src/main/webapp/WEB-INF/profil.jsp -->
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %> <%-- Directive pour activer la JSTL --%>

<!DOCTYPE html>
<html>
<head>
    <title>Mon Profil</title>
</head>
<body>

    <%-- 1. Interpolation pour afficher une variable simple --%>
    <h1>Bienvenue sur votre espace, ${nom} !</h1>

    <h2>Votre Panier d'achats :</h2>

    <%-- 2. Test : Vérifie si la liste des articles n'est pas vide --%>
    <c:if test="${not empty listeArticles}">
        <ul>
            <%-- 3. Boucle : Parcourt la liste des JavaBeans --%>
            <c:forEach items="${listeArticles}" var="article">
                <li>
                    <%-- Interpolation des propriétés du JavaBean --%>
                    <strong>${article.nom}</strong> - ${article.prix} €
                    
                    <%-- 4. Test imbriqué : Affiche un badge si l'article est en promo --%>
                    <c:if test="${article.enPromo}">
                        <span style="color: red; font-weight: bold;"> [PROMO !]</span>
                    </c:if>
                </li>
            </c:forEach>
        </ul>
    </c:if>

    <%-- Test alternatif : Si le panier était vide --%>
    <c:if test="${empty listeArticles}">
        <p>Votre panier est vide pour le moment.</p>
    </c:if>

</body>
</html>