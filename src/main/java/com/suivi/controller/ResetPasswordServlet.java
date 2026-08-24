package com.suivi.controller;

import com.suivi.model.Compte;
import com.suivi.utils.PasswordUtils;
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

    // Il est préférable de récupérer l'EntityManagerFactory via le ServletContext ou de l'initialiser proprement,
    // mais si vous gardez l'approche Persistence, assurez-vous de bien gérer les exceptions.
    private static EntityManagerFactory emf;

    @Override
    public void init() throws ServletException {
        super.init();
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("glycemiePU");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("emailReset") == null) {
            // Redirection avec une erreur de session expirée si besoin
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

        EntityManager em = null;
        try {
            em = emf.createEntityManager();
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
            e.printStackTrace(); // Trace l'erreur dans les logs de Render pour voir le vrai problème SQL/réseau
            response.sendRedirect(request.getContextPath() + "/nouveau-mot-de-passe.jsp?error=server");
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public void destroy() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
        super.destroy();
    }
}
