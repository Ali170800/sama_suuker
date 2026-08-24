<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mot de passe oublié - Suivi Glycémie</title>
    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light d-flex align-items-center justify-content-center min-vh-100 py-3">

    <!-- w-100 et mx-3 empêchent le débordement horizontal sur mobile -->
    <div class="card shadow-sm p-3 p-md-4 w-100 mx-3" style="max-width: 420px; border-radius: 15px;">
        <div class="text-center mb-4">
            <h3 class="fw-bold text-primary fs-4">🔑 Mot de passe oublié</h3>
            <p class="text-muted small">Entrez votre email pour vérifier votre compte</p>
        </div>

        <!-- Affichage de l'erreur si l'email n'existe pas ou est vide -->
        <% if(request.getAttribute("error") != null) { %>
            <div class="alert alert-danger py-2 small text-center" role="alert">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <form action="${pageContext.request.contextPath}/mot-de-passe-oublie" method="post">
            <div class="mb-3">
                <label class="form-label fw-semibold small">Adresse Email</label>
                <input type="email" name="email" class="form-control" required placeholder="nom@exemple.com">
            </div>

            <button type="submit" class="btn btn-primary w-100 fw-bold py-2">Vérifier l'email</button>

            <div class="text-center mt-3">
                <small class="text-muted"><a href="login.jsp" class="text-decoration-none">Retour à la connexion</a></small>
            </div>
        </form>
    </div>

</body>
</html>