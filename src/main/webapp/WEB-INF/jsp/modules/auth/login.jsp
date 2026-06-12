<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr" data-theme="light">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Connexion - Portail Mairie</title>

        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@3.0.0/dist/tabler-icons.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mairie-civic.css">

        <style>
            /* CSS spécifique à la page de login utilisant tes tokens */
            .login-wrapper {
                min-height: 100vh;
                display: flex;
                align-items: center;
                justify-content: center;
                background-color: var(--bg-page);
                padding: var(--space-6);
            }
            .login-card {
                width: 100%;
                max-width: 420px;
            }
            .login-brand {
                text-align: center;
                margin-bottom: var(--space-6);
            }
            .login-logo-icon {
                width: 56px;
                height: 56px;
                background: var(--color-primary);
                border-radius: var(--radius-lg);
                display: flex;
                align-items: center;
                justify-content: center;
                margin: 0 auto var(--space-3);
                box-shadow: var(--shadow-md);
            }
            .login-logo-icon i {
                color: #fff;
                font-size: 28px;
            }
        </style>
    </head>
    <body>

        <div class="login-wrapper">
            <div class="card card-lg login-card">

                <!-- En-tête / Logo -->
                <div class="login-brand">
                    <div class="login-logo-icon">
                        <!-- Remplacé par une icône Tabler représentative d'une mairie/institution -->
                        <i class="ti ti-building-bank"></i>
                    </div>
                    <h1 class="card-title" style="font-size: var(--text-2xl);">Portail Officier</h1>
                    <p class="card-subtitle text-muted">Connectez-vous pour accéder à votre espace</p>
                </div>

                <!-- 🚨 Affichage des erreurs générées par la Servlet -->
                <c:if test="${not empty erreur}">
                    <div class="alert alert-error" style="margin-bottom: var(--space-4);">
                        <i class="ti ti-alert-triangle"></i>
                        <div>
                            <div class="alert-title">Erreur d'authentification</div>
                            <span>${erreur}</span>
                        </div>
                    </div>
                </c:if>

                <!-- Formulaire de connexion -->
                <form action="${pageContext.request.contextPath}/login" method="post">

                    <!-- Champ Email -->
                    <div class="form-group">
                        <label for="email" class="form-label">Adresse Email <span class="required">*</span></label>
                        <div class="input-group">
                            <div class="input-group-icon">
                                <i class="ti ti-mail"></i>
                            </div>
                            <input type="email" id="email" name="email" class="form-control"
                            placeholder="agent@mairie.cm"
                            value="${emailSaisi}" required autofocus>
                        </div>
                    </div>

                    <!-- Champ Mot de passe -->
                    <div class="form-group" style="margin-bottom: var(--space-6);">
                        <label for="password" class="form-label">Mot de passe <span class="required">*</span></label>
                        <div class="input-group">
                            <div class="input-group-icon">
                                <i class="ti ti-lock"></i>
                            </div>
                            <input type="password" id="password" name="password" class="form-control"
                            placeholder="Votre mot de passe" required>
                        </div>
                    </div>

                    <!-- Bouton de soumission -->
                    <button type="submit" class="btn btn-primary btn-lg" style="width: 100%;">
                        <i class="ti ti-login"></i>
                        Se connecter
                    </button>

                </form>

            </div>
        </div>

    </body>
</html>