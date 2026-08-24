<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Connexion - Suivi Glycémie</title>
    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light d-flex align-items-center justify-content-center min-vh-100 py-3">

    <!-- w-100 et mx-3 empêchent le débordement horizontal sur smartphone -->
    <div class="card shadow-sm p-4 w-100 mx-3" style="max-width: 420px; border-radius: 15px;">
        <div class="text-center mb-4">
            <h3 class="fw-bold text-primary">🩺 Connexion</h3>
            <p class="text-muted small">Accédez à votre espace de suivi glycémique</p>
        </div>

        <!-- Affichage de l'erreur si les identifiants sont incorrects -->
        <% if(request.getAttribute("error") != null) { %>
            <div class="alert alert-danger py-2 small text-center" role="alert">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <form action="login" method="post">
            <div class="mb-3">
                <label class="form-label fw-semibold">Adresse Email</label>
                <input type="email" name="email" class="form-control" required placeholder="nom@exemple.com">
            </div>

            <div class="mb-2">
                <label class="form-label fw-semibold">Mot de passe</label>
                <input type="password" name="password" class="form-control" required placeholder="••••••••">
            </div>

            <!-- Lien Mot de passe oublié -->
            <div class="text-end mb-3">
                <a href="mot-de-passe-oublie.jsp" class="text-decoration-none small text-muted">Mot de passe oublié ?</a>
            </div>

            <button type="submit" class="btn btn-primary w-100 fw-bold py-2">Se connecter</button>

            <div class="text-center mt-3">
                <small class="text-muted">Pas encore de compte ? <a href="register.jsp">S'inscrire</a></small>
            </div>
        </form>
    </div>

</body>
</html>