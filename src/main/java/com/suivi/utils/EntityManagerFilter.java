package com.suivi.utils;

import com.suivi.utils.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter("/*") // S'applique à toutes les pages et servlets de l'application
public class EntityManagerFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialisation si nécessaire
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        EntityManager em = JPAUtil.getEntityManager();

        // On rend l'EntityManager accessible dans la requête si une servlet en a besoin directement
        request.setAttribute("em", em);

        try {
            // Laisse passer la requête vers la servlet ou la page JSP
            chain.doFilter(request, response);
        } finally {
            // S'exécute TOUJOURS à la fin (succès ou erreur), fermant ainsi la connexion proprement
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public void destroy() {
        JPAUtil.close();
    }
}