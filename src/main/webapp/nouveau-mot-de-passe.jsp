<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nouveau mot de passe - Suivi Glycémie</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light d-flex align-items-center justify-content-center min-vh-100 py-3">

    <!-- w-100 et mx-3 évitent tout débordement sur smartphone -->
    <div class="card shadow-sm p-3 p-md-4 w-100 mx-3" style="max-width: 420px; border-radius: 15px;">
        <div class="text-center mb-4">
            <h3 class="fw-bold text-success fs-4">🔒 Nouveau mot de passe</h3>
            <p class="text-muted small">Choisissez un nouveau mot de passe sécurisé</p>
        </div>

        <% if(request.getAttribute("error") != null) { %>
            <div class="alert alert-danger py-2 small text-center" role="alert">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <form action="${pageContext.request.contextPath}/reinitialiser-password" method="post">
            <div class="mb-3">
                <label class="form-label fw-semibold small">Nouveau mot de passe</label>
                <input type="password" name="password" class="form-control" required placeholder="••••••••">
            </div>

            <div class="mb-4">
                <label class="form-label fw-semibold small">Confirmer le mot de passe</label>
                <input type="password" name="confirmPassword" class="form-control" required placeholder="••••••••">
            </div>

            <button type="submit" class="btn btn-success w-100 fw-bold py-2">Mettre à jour le mot de passe</button>
        </form>
    </div>

</body>
</html>