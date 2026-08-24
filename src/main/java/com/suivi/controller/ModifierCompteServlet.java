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

@WebServlet("/compte/modifier")
public class ModifierCompteServlet extends HttpServlet {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("glycemiePU");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("compteConnecte") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Compte compteConnecte = (Compte) session.getAttribute("compteConnecte");
        String nouvelEmail = request.getParameter("email");
        String nouveauPassword = request.getParameter("password");

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Récupérer le compte depuis la base de données pour le mettre à jour
            Compte compte = em.find(Compte.class, compteConnecte.getId());

            if (compte != null) {
                compte.setEmail(nouvelEmail);

                // Ne met à jour le mot de passe que s'il a été saisi (avec hashage)
                if (nouveauPassword != null && !nouveauPassword.trim().isEmpty()) {
                    compte.setPassword(PasswordUtils.hashPassword(nouveauPassword));
                }

                em.merge(compte);
                em.getTransaction().commit();

                // Mettre à jour l'objet en session pour que les infos soient à jour
                session.setAttribute("compteConnecte", compte);
            }

            response.sendRedirect(request.getContextPath() + "/dashboard");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/dashboard?error=modification");
        } finally {
            em.close();
        }
    }
}