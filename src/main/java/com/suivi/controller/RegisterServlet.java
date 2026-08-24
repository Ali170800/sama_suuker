package com.suivi.controller;

import com.suivi.model.Compte;
import com.suivi.utils.PasswordUtils; // <-- Import de l'utilitaire de hashage
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

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("glycemiePU");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (nom == null || prenom == null || email == null || password == null || confirmPassword == null ||
                nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty()) {
            request.setAttribute("error", "Veuillez remplir tous les champs.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Les mots de passe ne correspondent pas.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        EntityManager em = emf.createEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(c) FROM Compte c WHERE c.email = :email", Long.class)
                    .setParameter("email", email)
                    .getSingleResult();

            if (count > 0) {
                request.setAttribute("error", "Cet email est déjà utilisé.");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }

            em.getTransaction().begin();
            Compte nouveauCompte = new Compte();
            nouveauCompte.setNom(nom);
            nouveauCompte.setPrenom(prenom);
            nouveauCompte.setEmail(email);

            // 🔒 HASHAGE DU MOT DE PASSE AVANT ENREGISTREMENT
            nouveauCompte.setPassword(PasswordUtils.hashPassword(password));

            em.persist(nouveauCompte);
            em.getTransaction().commit();

            // Connexion automatique immédiate via la session
            HttpSession session = request.getSession();
            session.setAttribute("compteConnecte", nouveauCompte);

            // Redirection vers le dashboard
            response.sendRedirect(request.getContextPath() + "/dashboard");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            request.setAttribute("error", "Une erreur est survenue lors de l'inscription.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
        } finally {
            em.close();
        }
    }
}