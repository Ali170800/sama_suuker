package com.suivi.controller;

import com.suivi.model.Compte;
import com.suivi.model.Glycemie;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Compte compte = (session != null) ? (Compte) session.getAttribute("compteConnecte") : null;

        if (compte == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String periode = request.getParameter("periode");
        String typeFiltre = request.getParameter("typeFiltre");

        // On récupère l'EntityManager préparé par le filtre (EntityManagerFilter)
        EntityManager em = (EntityManager) request.getAttribute("em");

        try {
            StringBuilder jpql = new StringBuilder("SELECT g FROM Glycemie g WHERE g.compte.id = :compteId");

            LocalDateTime maintenant = LocalDateTime.now();
            if ("aujourdhui".equals(periode) || "7jours".equals(periode) || "30jours".equals(periode)) {
                jpql.append(" AND g.dateHeure >= :dateDebut");
            }

            if (typeFiltre != null && !typeFiltre.isEmpty()) {
                jpql.append(" AND g.type = :typeFiltre");
            }

            jpql.append(" ORDER BY g.dateHeure DESC");

            var query = em.createQuery(jpql.toString(), Glycemie.class);
            query.setParameter("compteId", compte.getId());

            if ("aujourdhui".equals(periode)) {
                query.setParameter("dateDebut", maintenant.toLocalDate().atStartOfDay());
            } else if ("7jours".equals(periode)) {
                query.setParameter("dateDebut", maintenant.minusDays(7));
            } else if ("30jours".equals(periode)) {
                query.setParameter("dateDebut", maintenant.minusDays(30));
            }

            if (typeFiltre != null && !typeFiltre.isEmpty()) {
                query.setParameter("typeFiltre", typeFiltre);
            }

            List<Glycemie> glycemies = query.getResultList();
            request.setAttribute("glycemies", glycemies);

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Note : Pas besoin de em.close() ici, le filtre (EntityManagerFilter) s'en charge automatiquement !

        // On fait le forward vers la page JSP pour afficher les données
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
}