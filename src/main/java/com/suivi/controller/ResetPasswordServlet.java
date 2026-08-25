package com.suivi.controller;

import com.suivi.model.Compte;
import com.suivi.utils.PasswordUtils;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/reinitialiser-password")
public class ResetPasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("emailReset") == null) {
            response.sendRedirect(request.getContextPath() + "/mot-de-passe-oublie.jsp?error=expired");
            return;
        }

        String email = (String) session.getAttribute("emailReset");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (password == null || password.trim().isEmpty() || !password.equals(confirmPassword)) {
            response.sendRedirect(request.getContextPath() + "/nouveau-mot-de-passe.jsp?error=match");
            return;
        }

        // On récupère l'EntityManager préparé et partagé par le filtre (EntityManagerFilter)
        EntityManager em = (EntityManager) request.getAttribute("em");

        try {
            em.getTransaction().begin();

            Compte compte = em.createQuery("SELECT c FROM Compte c WHERE c.email = :email", Compte.class)
                    .setParameter("email", email)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (compte != null) {
                // Hasher le mot de passe avant de l'enregistrer
                String motDePasseSecurise = PasswordUtils.hashPassword(password);
                compte.setPassword(motDePasseSecurise);

                em.merge(compte);
                em.getTransaction().commit();
            } else {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                response.sendRedirect(request.getContextPath() + "/mot-de-passe-oublie.jsp?error=notfound");
                return;
            }

            // Nettoyer la session temporaire
            session.removeAttribute("emailReset");

            // Rediriger vers la page de connexion avec succès
            response.sendRedirect(request.getContextPath() + "/login.jsp?success=reset");

        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/nouveau-mot-de-passe.jsp?error=server");
        }
        // Note : Pas besoin de em.close() ici, le filtre s'en charge automatiquement dans son bloc finally !
    }
}