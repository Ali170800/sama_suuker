package com.suivi.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/glycemie/supprimer")
public class GlycemieDeleteServlet extends HttpServlet {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("glycemiePU");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            Long id = Long.parseLong(idStr);
            EntityManager em = emf.createEntityManager();
            try {
                em.getTransaction().begin();
                com.suivi.model.Glycemie g = em.find(com.suivi.model.Glycemie.class, id);
                if (g != null) {
                    em.remove(g);
                }
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
            } finally {
                em.close();
            }
        }
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}