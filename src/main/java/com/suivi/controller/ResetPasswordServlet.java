package com.suivi.controller;

import com.suivi.model.Compte;
import com.suivi.utils.PasswordUtils; // 1. Ne pas oublier d'importer l'utilitaire
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/reinitialiser-password")
public class ResetPasswordServlet extends HttpServlet {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("glycemiePU");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("emailReset") == null) {
            response.sendRedirect(request.getContextPath() + "/mot-de-passe-oublie.jsp");
            return;
        }

        String email = (String) session.getAttribute("emailReset");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (password == null || !password.equals(confirmPassword)) {
            response.sendRedirect(request.getContextPath() + "/nouveau-mot-de-passe.jsp?error=match");
            return;
        }

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Compte compte = em.createQuery("SELECT c FROM Compte c WHERE c.email = :email", Compte.class)
                    .setParameter("email", email)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (compte != null) {
                // 2. HASHER LE MOT DE PASSE AVANT DE L'ENREGISTRER
                String motDePasseSecurise = PasswordUtils.hashPassword(password);
                compte.setPassword(motDePasseSecurise);

                em.merge(compte);
                em.getTransaction().commit();
            }

            // Nettoyer la session temporaire
            session.removeAttribute("emailReset");

            // Rediriger vers la page de connexion avec succès
            response.sendRedirect(request.getContextPath() + "/login.jsp?success=reset");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/nouveau-mot-de-passe.jsp?error=server");
        } finally {
            em.close();
        }
    }
}