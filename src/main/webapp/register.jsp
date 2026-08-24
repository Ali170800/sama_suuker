<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inscription - Suivi Glycémie</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light d-flex align-items-center justify-content-center min-vh-100 py-3">

    <!-- w-100 et mx-3 permettent d'éviter les débordements sur les petits téléphones -->
    <div class="card shadow-sm p-3 p-md-4 w-100 mx-3" style="max-width: 450px; border-radius: 15px;">
        <div class="text-center mb-3">
            <h3 class="fw-bold text-primary">🩺 Inscription</h3>
            <p class="text-muted small">Créez votre espace de suivi médical</p>
        </div>

        <% if(request.getAttribute("error") != null) { %>
            <div class="alert alert-danger py-2 small" role="alert">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <form action="register" method="post">
            <div class="mb-3">
                <label class="form-label fw-semibold">Nom</label>
                <input type="text" name="nom" class="form-control" required placeholder="Votre nom">
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">Prénom</label>
                <input type="text" name="prenom" class="form-control" required placeholder="Votre prénom">
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">Adresse Email</label>
                <input type="email" name="email" class="form-control" required placeholder="nom@exemple.com">
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">Mot de passe</label>
                <input type="password" name="password" class="form-control" required placeholder="••••••••">
            </div>

            <div class="mb-4">
                <label class="form-label fw-semibold">Confirmer le mot de passe</label>
                <input type="password" name="confirmPassword" class="form-control" required placeholder="••••••••">
            </div>

            <button type="submit" class="btn btn-primary w-100 fw-bold py-2">S'inscrire</button>

            <div class="text-center mt-3">
                <small class="text-muted">Déjà un compte ? <a href="login.jsp">Se connecter</a></small>
            </div>
        </form>
    </div>

</body>
</html>