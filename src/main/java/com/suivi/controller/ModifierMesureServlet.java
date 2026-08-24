package com.suivi.controller;

import com.suivi.dao.GlycemieDao;
import com.suivi.model.Glycemie;
import com.suivi.model.Insuline;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/mesure/modifier")
public class ModifierMesureServlet extends HttpServlet {
    private GlycemieDao glycemieDao = new GlycemieDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("compteConnecte") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                Long id = Long.parseLong(idStr);
                // Utilisation d'une méthode DAO ou recherche sécurisée avec ses insulines
                Glycemie glycemie = glycemieDao.trouverParId(id); // Optionnel si vous préférez ajouter cette méthode, ou via EntityManager
                if (glycemie != null) {
                    request.setAttribute("glycemie", glycemie);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        request.getRequestDispatcher("/modifier-mesure.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("compteConnecte") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String idStr = request.getParameter("id");
        String valeurStr = request.getParameter("valeur");
        String type = request.getParameter("typeMesure");
        String dateHeureStr = request.getParameter("dateHeure");

        // Récupération des deux insulines du formulaire de modification
        String nomIns1 = request.getParameter("nomInsuline1");
        String uniteIns1 = request.getParameter("uniteInsuline1");
        String nomIns2 = request.getParameter("nomInsuline2");
        String uniteIns2 = request.getParameter("uniteInsuline2");

        if (idStr == null || valeurStr == null || type == null) {
            response.sendRedirect(request.getContextPath() + "/dashboard?error=invalid");
            return;
        }

        try {
            Long id = Long.parseLong(idStr);

            // Récupération de l'objet existant via le DAO (avec FetchType.EAGER, les insulines sont déjà chargées)
            Glycemie glycemie = glycemieDao.trouverParId(id);

            if (glycemie != null) {
                // Mise à jour des champs de la glycémie
                glycemie.setValeur(Double.parseDouble(valeurStr));
                glycemie.setType(type);

                if (dateHeureStr != null && !dateHeureStr.isEmpty()) {
                    glycemie.setDateHeure(LocalDateTime.parse(dateHeureStr));
                }

                // Nettoyage et mise à jour de la liste des insulines (grâce à orphanRemoval = true)
                glycemie.getInsulines().clear();

                // Ajout de la première insuline si renseignée
                if (uniteIns1 != null && !uniteIns1.trim().isEmpty()) {
                    Insuline ins1 = new Insuline();
                    ins1.setUnite(Double.parseDouble(uniteIns1));
                    ins1.setType(nomIns1 != null && !nomIns1.trim().isEmpty() ? nomIns1.trim() : "Insuline 1");
                    glycemie.addInsuline(ins1);
                }

                // Ajout de la deuxième insuline si renseignée
                if (uniteIns2 != null && !uniteIns2.trim().isEmpty()) {
                    Insuline ins2 = new Insuline();
                    ins2.setUnite(Double.parseDouble(uniteIns2));
                    ins2.setType(nomIns2 != null && !nomIns2.trim().isEmpty() ? nomIns2.trim() : "Insuline 2");
                    glycemie.addInsuline(ins2);
                }

                // Sauvegarde via le DAO
                glycemieDao.mettreAJour(glycemie);
            }

            response.sendRedirect(request.getContextPath() + "/dashboard");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/dashboard?error=update");
        }
    }
}