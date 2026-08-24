package com.suivi.controller;

import com.suivi.model.Compte;
import com.suivi.utils.PasswordUtils; // <-- Import de l'utilitaire de hashage
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.NoResultException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("glycemiePU");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            request.setAttribute("error", "Veuillez remplir tous les champs.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        EntityManager em = emf.createEntityManager();
        try {
            // 1. Recherche du compte uniquement par email
            Compte compte = null;
            try {
                compte = em.createQuery("SELECT c FROM Compte c WHERE c.email = :email", Compte.class)
                        .setParameter("email", email)
                        .getSingleResult();
            } catch (NoResultException e) {
                compte = null;
            }

            // 2. Vérification sécurisée du mot de passe avec BCrypt
            if (compte != null && PasswordUtils.checkPassword(password, compte.getPassword())) {
                // Connexion réussie : Création de la session
                HttpSession session = request.getSession();
                session.setAttribute("compteConnecte", compte);

                // Redirection vers le tableau de bord
                response.sendRedirect(request.getContextPath() + "/dashboard");
            } else {
                // Identifiants incorrects
                request.setAttribute("error", "Email ou mot de passe incorrect.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Une erreur est survenue lors de la connexion.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } finally {
            em.close();
        }
    }
}