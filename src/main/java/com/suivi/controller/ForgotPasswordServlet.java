package com.suivi.controller;

import com.suivi.model.Compte;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/mot-de-passe-oublie")
public class ForgotPasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");

        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Veuillez entrer une adresse email.");
            request.getRequestDispatcher("mot-de-passe-oublie.jsp").forward(request, response);
            return;
        }

        // On récupère l'EntityManager préparé par le filtre (EntityManagerFilter)
        EntityManager em = (EntityManager) request.getAttribute("em");

        try {
            // Vérifier si le compte existe avec cet email
            Compte compte = em.createQuery("SELECT c FROM Compte c WHERE c.email = :email", Compte.class)
                    .setParameter("email", email)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (compte != null) {
                // Le compte existe : on stocke temporairement l'email en session pour l'étape suivante
                HttpSession session = request.getSession();
                session.setAttribute("emailReset", email);

                // Rediriger vers la page de saisie du nouveau mot de passe
                response.sendRedirect(request.getContextPath() + "/nouveau-mot-de-passe.jsp");
            } else {
                request.setAttribute("error", "Aucun compte associé à cet email.");
                request.getRequestDispatcher("mot-de-passe-oublie.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Une erreur est survenue.");
            request.getRequestDispatcher("mot-de-passe-oublie.jsp").forward(request, response);
        }
        // Note : Pas besoin de em.close() ici, le filtre s'en charge automatiquement !
    }
}