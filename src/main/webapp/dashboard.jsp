<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tableau de Bord - Suivi Glycémie</title>
    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body class="bg-light py-3">

    <!-- Utilisation de container-fluid et mx-2/mx-3 pour s'adapter à tous les mobiles sans dépasser -->
    <div class="container-fluid px-2 px-md-4" style="max-width: 1100px;">

        <!-- En-tête -->
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center mb-3 bg-white p-3 p-md-4 rounded-4 shadow-sm gap-3">
            <div>
                <h2 class="fw-bold text-primary m-0">🩺 Sama Suukar</h2>
                <p class="text-muted m-0 small">Patient : <strong>${sessionScope.compteConnecte.prenom} ${sessionScope.compteConnecte.nom}</strong> (${sessionScope.compteConnecte.email})</p>
            </div>
            <div class="d-flex flex-wrap gap-2 w-100 w-md-auto justify-content-md-end">
                <form id="pdfForm" action="${pageContext.request.contextPath}/glycemie/pdf" method="post" class="flex-fill flex-md-grow-0">
                    <input type="hidden" name="chartImage" id="chartImageInput">
                    <button type="button" onclick="generatePdfWithChart()" class="btn btn-danger fw-semibold w-100">📄 Rapport PDF</button>
                </form>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-secondary btn-sm flex-fill flex-md-grow-0 d-flex align-items-center justify-content-center">Déconnexion</a>
            </div>
        </div>

        <!-- Bloc de modification des identifiants -->
        <div class="card shadow-sm border-0 rounded-4 p-3 p-md-4 mb-3 bg-white">
            <h4 class="fw-bold mb-3 text-dark fs-5">🔐 Modifier mes identifiants</h4>
            <form action="${pageContext.request.contextPath}/compte/modifier" method="post" class="row g-3">
                <div class="col-12 col-md-5">
                    <label class="form-label fw-semibold small">Nouvel Email</label>
                    <input type="email" name="email" class="form-control form-control-sm" value="${sessionScope.compteConnecte.email}" required>
                </div>
                <div class="col-12 col-md-5">
                    <label class="form-label fw-semibold small">Nouveau Mot de passe</label>
                    <input type="password" name="password" class="form-control form-control-sm" placeholder="Laisser vide si inchangé">
                </div>
                <div class="col-12 col-md-2 d-flex align-items-end">
                    <button type="submit" class="btn btn-warning btn-sm fw-bold w-100">Mettre à jour</button>
                </div>
            </form>
        </div>

        <!-- Formulaire d'ajout de mesure -->
        <div class="card shadow-sm border-0 rounded-4 p-3 p-md-4 mb-3">
            <h4 class="fw-bold mb-3 text-dark fs-5">➕ Ajouter une nouvelle mesure</h4>

            <form action="${pageContext.request.contextPath}/glycemie/ajouter" method="post" class="row g-3">
                <div class="col-12 col-md-3">
                    <label class="form-label fw-semibold small">Valeur (mg/dL ou g/L)</label>
                    <input type="number" step="0.1" name="valeur" class="form-control" required placeholder="ex: 1.25">
                </div>

                <div class="col-12 col-md-3">
                    <label class="form-label fw-semibold small">Type de mesure</label>
                    <select name="type" id="typeMesure" class="form-select" required onchange="gererChampsInsuline()">
                        <option value="À jeun">À jeun</option>
                        <option value="Avant repas">Avant repas</option>
                        <option value="Après repas">Après repas</option>
                        <option value="Après déjeuner">Après déjeuner</option>
                        <option value="Avant diner">Avant diner</option>
                        <option value="Après diner">Après diner</option>
                    </select>
                </div>

                <div class="col-12 col-md-3" id="colInsuline1">
                    <label class="form-label fw-semibold small" id="labelInsuline1">Insuline 1 (Nom & Unités)</label>
                    <div class="input-group">
                        <input type="text" name="nomInsuline1" id="nomInsuline1" class="form-control" placeholder="Nom exact">
                        <input type="number" step="0.5" name="uniteInsuline1" id="uniteInsuline1" class="form-control" placeholder="U">
                    </div>
                </div>

                <div class="col-12 col-md-3" id="colInsuline2">
                    <label class="form-label fw-semibold small">Insuline 2 (Optionnelle)</label>
                    <div class="input-group">
                        <input type="text" name="nomInsuline2" id="nomInsuline2" class="form-control" placeholder="Nom exact">
                        <input type="number" step="0.5" name="uniteInsuline2" id="uniteInsuline2" class="form-control" placeholder="U">
                    </div>
                </div>

                <div class="col-12 mt-3">
                    <button type="submit" class="btn btn-success fw-bold px-4 w-100 w-md-auto">Enregistrer la mesure</button>
                </div>
            </form>
        </div>

        <!-- Formulaire de filtrage -->
        <div class="card shadow-sm border-0 rounded-4 p-3 mb-3 bg-white">
            <form action="${pageContext.request.contextPath}/dashboard" method="get" class="row g-3 align-items-end">
                <div class="col-12 col-md-4">
                    <label class="form-label fw-semibold small">📅 Période</label>
                    <select name="periode" class="form-select form-select-sm">
                        <option value="">Toutes les périodes</option>
                        <option value="aujourdhui" ${param.periode == 'aujourdhui' ? 'selected' : ''}>Aujourd'hui</option>
                        <option value="7jours" ${param.periode == '7jours' ? 'selected' : ''}>Les 7 derniers jours</option>
                        <option value="30jours" ${param.periode == '30jours' ? 'selected' : ''}>Les 30 derniers jours</option>
                    </select>
                </div>

                <div class="col-12 col-md-4">
                    <label class="form-label fw-semibold small">🏷️ Type de mesure</label>
                    <select name="typeFiltre" class="form-select form-select-sm">
                        <option value="">Tous les types</option>
                        <option value="À jeun" ${param.typeFiltre == 'À jeun' ? 'selected' : ''}>À jeun</option>
                        <option value="Avant repas" ${param.typeFiltre == 'Avant repas' ? 'selected' : ''}>Avant repas</option>
                        <option value="Après repas" ${param.typeFiltre == 'Après repas' ? 'selected' : ''}>Après repas</option>
                        <option value="Après déjeuner" ${param.typeFiltre == 'Après déjeuner' ? 'selected' : ''}>Après déjeuner</option>
                        <option value="Avant diner" ${param.typeFiltre == 'Avant diner' ? 'selected' : ''}>Avant diner</option>
                        <option value="Après diner" ${param.typeFiltre == 'Après diner' ? 'selected' : ''}>Après diner</option>
                    </select>
                </div>

                <div class="col-12 col-md-4 d-flex gap-2">
                    <button type="submit" class="btn btn-primary btn-sm fw-bold w-100">Filtrer</button>
                    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-secondary btn-sm w-100 text-center">Réinitialiser</a>
                </div>
            </form>
        </div>

        <!-- 1. Tableau des données -->
        <div class="card shadow-sm border-0 rounded-4 p-3 p-md-4 mb-3">
            <h4 class="fw-bold mb-3 text-dark fs-5">📋 Historique des mesures</h4>

            <div class="table-responsive">
                <table class="table table-hover align-middle text-nowrap">
                    <thead class="table-dark">
                        <tr>
                            <th>Date & Heure</th>
                            <th>Valeur</th>
                            <th>Type</th>
                            <th>Insuline (U)</th>
                            <th>Nom(s) Insuline</th>
                            <th class="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="g" items="${glycemies}">
                            <tr>
                                <td class="text-capitalize">${g.dateHeureFormatee}</td>
                                <td class="fw-bold text-primary">${g.valeur}</td>
                                <td><span class="badge bg-secondary">${g.type}</span></td>
                                <td>
                                    <c:forEach var="ins" items="${g.insulines}">
                                        <div>${ins.unite} U</div>
                                    </c:forEach>
                                    <c:if test="${empty g.insulines}">-</c:if>
                                </td>
                                <td>
                                    <c:forEach var="ins" items="${g.insulines}">
                                        <div><strong>${ins.type}</strong></div>
                                    </c:forEach>
                                    <c:if test="${empty g.insulines}">-</c:if>
                                </td>
                                <td class="text-end">
                                    <a href="${pageContext.request.contextPath}/mesure/modifier?id=${g.id}" class="btn btn-sm btn-outline-warning fw-semibold me-1">✏️ Modifier</a>
                                    <a href="${pageContext.request.contextPath}/glycemie/supprimer?id=${g.id}" class="btn btn-sm btn-outline-danger fw-semibold" onclick="return confirm('Voulez-vous vraiment supprimer cette mesure ?');">🗑️ Supprimer</a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty glycemies}">
                            <tr>
                                <td colspan="6" class="text-center py-4 text-muted fst-italic">Aucune mesure enregistrée pour le moment.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- 2. Courbe d'évolution -->
        <div class="card shadow-sm border-0 rounded-4 p-3 p-md-4 bg-white mb-4">
            <h4 class="fw-bold mb-3 text-dark fs-5">📈 Courbe d'évolution de la glycémie</h4>
            <div style="position: relative; height: 280px; width: 100%;">
                <canvas id="dashboardChart"></canvas>
            </div>
        </div>

    </div>

    <!-- Script de gestion dynamique strict -->
    <script>
        function gererChampsInsuline() {
            const typeSelect = document.getElementById('typeMesure');
            const val = typeSelect.value.toLowerCase();

            const colInsuline1 = document.getElementById('colInsuline1');
            const colInsuline2 = document.getElementById('colInsuline2');
            const uniteInsuline1 = document.getElementById('uniteInsuline1');
            const nomInsuline1 = document.getElementById('nomInsuline1');
            const uniteInsuline2 = document.getElementById('uniteInsuline2');
            const nomInsuline2 = document.getElementById('nomInsuline2');
            const labelInsuline1 = document.getElementById('labelInsuline1');

            if (val.includes('après') || val.includes('apres')) {
                colInsuline1.style.display = 'none';
                colInsuline2.style.display = 'none';

                nomInsuline1.removeAttribute('required');
                uniteInsuline1.removeAttribute('required');
                nomInsuline1.value = '';
                uniteInsuline1.value = '';
                nomInsuline2.value = '';
                uniteInsuline2.value = '';
            } else {
                colInsuline1.style.display = '';
                colInsuline2.style.display = '';

                nomInsuline1.setAttribute('required', 'required');
                uniteInsuline1.setAttribute('required', 'required');
                labelInsuline1.innerHTML = 'Insuline 1 (Nom & Unités) <span class="text-danger">*</span>';
            }
        }

        document.addEventListener('DOMContentLoaded', gererChampsInsuline);
        document.getElementById('typeMesure').addEventListener('change', gererChampsInsuline);

        const labelsData = [
            <c:forEach var="g" items="${glycemies}" varStatus="status">
                "${g.dateHeureFormatee}"${!status.last ? ',' : ''}
            </c:forEach>
        ];
        const valuesData = [
            <c:forEach var="g" items="${glycemies}" varStatus="status">
                ${g.valeur}${!status.last ? ',' : ''}
            </c:forEach>
        ];

        const ctxDashboard = document.getElementById('dashboardChart').getContext('2d');
        const liveChart = new Chart(ctxDashboard, {
            type: 'line',
            data: {
                labels: labelsData,
                datasets: [{
                    label: 'Glycémie (g/L)',
                    data: valuesData,
                    borderColor: '#0d6efd',
                    backgroundColor: 'rgba(13, 110, 253, 0.1)',
                    borderWidth: 2,
                    fill: true,
                    tension: 0.1,
                    pointRadius: 4,
                    pointBackgroundColor: '#0d6efd'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        min: 0.4,
                        max: 3.0,
                        title: { display: true, text: 'Glycémie (g/L)' }
                    },
                    x: {
                        title: { display: true, text: 'Date et Heure' }
                    }
                }
            }
        });

        function generatePdfWithChart() {
            const base64Image = document.getElementById('dashboardChart').toDataURL('image/png');
            document.getElementById('chartImageInput').value = base64Image;
            document.getElementById('pdfForm').submit();
        }
    </script>
</body>
</html>