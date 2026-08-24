<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Modifier la mesure - Suivi Glycémie</title>
    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light d-flex align-items-center justify-content-center min-vh-100 py-3">

    <!-- w-100 et mx-3 évitent tout débordement sur smartphone -->
    <div class="card shadow-sm p-3 p-md-4 w-100 mx-3" style="max-width: 500px; border-radius: 15px;">
        <div class="text-center mb-3">
            <h3 class="fw-bold text-warning fs-4">✏️ Modifier la mesure</h3>
            <p class="text-muted small">Mettez à jour l'ensemble de vos données de glycémie</p>
        </div>

        <% if(request.getAttribute("error") != null) { %>
            <div class="alert alert-danger py-2 small text-center" role="alert">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <form action="${pageContext.request.contextPath}/mesure/modifier" method="post">
            <!-- ID caché indispensable -->
            <input type="hidden" name="id" value="${glycemie.id}">

            <!-- 1. Valeur de la glycémie -->
            <div class="mb-3">
                <label class="form-label fw-semibold small">Valeur (g/L ou mg/dL)</label>
                <input type="text" name="valeur" class="form-control" value="${glycemie.valeur}" required placeholder="ex: 1.25">
            </div>

            <!-- 2. Type de mesure -->
            <div class="mb-3">
                <label class="form-label fw-semibold small">Type de mesure</label>
                <select name="typeMesure" id="typeMesure" class="form-select" required onchange="gererChampsInsuline()">
                    <option value="À jeun" ${glycemie.type == 'À jeun' ? 'selected' : ''}>À jeun</option>
                    <option value="Avant repas" ${glycemie.type == 'Avant repas' ? 'selected' : ''}>Avant repas</option>
                    <option value="Après repas" ${glycemie.type == 'Après repas' ? 'selected' : ''}>Après repas</option>
                    <option value="Après déjeuner" ${glycemie.type == 'Après déjeuner' ? 'selected' : ''}>Après déjeuner</option>
                    <option value="Avant diner" ${glycemie.type == 'Avant diner' ? 'selected' : ''}>Avant diner</option>
                    <option value="Après diner" ${glycemie.type == 'Après diner' ? 'selected' : ''}>Après diner</option>
                </select>
            </div>

            <!-- 3. Insuline 1 (Nom et Unités) -->
            <div class="card p-3 mb-3 bg-light border-0" id="blocInsuline1">
                <label class="form-label fw-semibold text-primary small" id="labelInsuline1">Première Insuline</label>
                <div class="row g-2">
                    <div class="col-7">
                        <input type="text" name="nomInsuline1" id="nomInsuline1" class="form-control form-control-sm" value="${not empty glycemie.insulines[0] ? glycemie.insulines[0].type : ''}" placeholder="Type (ex: Lente)">
                    </div>
                    <div class="col-5">
                        <input type="number" step="0.5" name="uniteInsuline1" id="uniteInsuline1" class="form-control form-control-sm" value="${not empty glycemie.insulines[0] ? glycemie.insulines[0].unite : ''}" placeholder="Unités">
                    </div>
                </div>
            </div>

            <!-- 4. Insuline 2 (Nom et Unités) -->
            <div class="card p-3 mb-3 bg-light border-0" id="blocInsuline2">
                <label class="form-label fw-semibold text-primary small">Deuxième Insuline (optionnel)</label>
                <div class="row g-2">
                    <div class="col-7">
                        <input type="text" name="nomInsuline2" id="nomInsuline2" class="form-control form-control-sm" value="${not empty glycemie.insulines[1] ? glycemie.insulines[1].type : ''}" placeholder="Type (ex: Rapide)">
                    </div>
                    <div class="col-5">
                        <input type="number" step="0.5" name="uniteInsuline2" id="uniteInsuline2" class="form-control form-control-sm" value="${not empty glycemie.insulines[1] ? glycemie.insulines[1].unite : ''}" placeholder="Unités">
                    </div>
                </div>
            </div>

            <div class="d-flex gap-2 justify-content-between mt-4">
                <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-secondary w-50">Annuler</a>
                <button type="submit" class="btn btn-warning fw-bold w-50 text-white">Enregistrer</button>
            </div>
        </form>
    </div>

    <!-- Script de gestion dynamique strict -->
    <script>
        function gererChampsInsuline() {
            const typeSelect = document.getElementById('typeMesure');
            const val = typeSelect.value.toLowerCase();

            const blocInsuline1 = document.getElementById('blocInsuline1');
            const blocInsuline2 = document.getElementById('blocInsuline2');
            const nomInsuline1 = document.getElementById('nomInsuline1');
            const uniteInsuline1 = document.getElementById('uniteInsuline1');
            const nomInsuline2 = document.getElementById('nomInsuline2');
            const uniteInsuline2 = document.getElementById('uniteInsuline2');
            const labelInsuline1 = document.getElementById('labelInsuline1');

            if (val.includes('après') || val.includes('apres')) {
                blocInsuline1.style.display = 'none';
                blocInsuline2.style.display = 'none';

                nomInsuline1.removeAttribute('required');
                uniteInsuline1.removeAttribute('required');

                nomInsuline1.value = '';
                uniteInsuline1.value = '';
                nomInsuline2.value = '';
                uniteInsuline2.value = '';
            } else {
                blocInsuline1.style.display = '';
                blocInsuline2.style.display = '';

                nomInsuline1.setAttribute('required', 'required');
                uniteInsuline1.setAttribute('required', 'required');
                labelInsuline1.innerHTML = 'Première Insuline <span class="text-danger">*</span>';
            }
        }

        document.addEventListener('DOMContentLoaded', gererChampsInsuline);
    </script>
</body>
</html>