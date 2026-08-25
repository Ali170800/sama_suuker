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

@WebServlet("/compte/modifier")
public class ModifierCompteServlet extends HttpServlet {

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

        // On récupère l'EntityManager préparé et partagé par le filtre (EntityManagerFilter)
        EntityManager em = (EntityManager) request.getAttribute("em");

        try {
            em.getTransaction().begin();

            Compte compte = em.find(Compte.class, compteConnecte.getId());

            if (compte != null) {
                if (nouvelEmail != null && !nouvelEmail.trim().isEmpty()) {
                    compte.setEmail(nouvelEmail.trim());
                }

                // Vérification stricte : on ne touche au mot de passe que s'il est explicitement rempli
                if (nouveauPassword != null && !nouveauPassword.trim().isEmpty() && nouveauPassword.trim().length() >= 4) {
                    compte.setPassword(PasswordUtils.hashPassword(nouveauPassword.trim()));
                }

                em.merge(compte);
                em.getTransaction().commit();

                // Mettre à jour l'objet en session pour que l'interface reflète les changements
                session.setAttribute("compteConnecte", compte);
            }

            response.sendRedirect(request.getContextPath() + "/dashboard?success=modifie");

        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace(); // Trace l'erreur dans les logs Render
            response.sendRedirect(request.getContextPath() + "/dashboard?error=modification");
        }
        // Note : Pas besoin de em.close() ici, le filtre s'en charge automatiquement dans son bloc finally !
    }
}