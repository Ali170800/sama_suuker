package com.suivi.controller;

import com.suivi.dao.GlycemieDao;
import com.suivi.model.Compte;
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

@WebServlet("/glycemie/ajouter")
public class GlycemieServlet extends HttpServlet {
    private final GlycemieDao glycemieDao = new GlycemieDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Compte compte = (session != null) ? (Compte) session.getAttribute("compteConnecte") : null;

        if (compte == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            double valeur = Double.parseDouble(request.getParameter("valeur"));
            String type = request.getParameter("type");

            Glycemie glycemie = new Glycemie();
            glycemie.setValeur(valeur);
            glycemie.setType(type);
            glycemie.setDateHeure(LocalDateTime.now());
            glycemie.setCompte(compte);

            String typeLower = type != null ? type.toLowerCase() : "";

            // Si ce n'est PAS un type "après" (donc À jeun, Avant repas, Avant diner...)
            if (!typeLower.contains("après") && !typeLower.contains("apres")) {

                // Insuline 1
                String nomInsuline1 = request.getParameter("nomInsuline1");
                String uniteInsuline1Str = request.getParameter("uniteInsuline1");
                if (nomInsuline1 != null && !nomInsuline1.trim().isEmpty() &&
                        uniteInsuline1Str != null && !uniteInsuline1Str.trim().isEmpty()) {

                    Insuline ins1 = new Insuline();
                    ins1.setType(nomInsuline1.trim());
                    ins1.setUnite(Double.parseDouble(uniteInsuline1Str));
                    ins1.setGlycemie(glycemie);
                    glycemie.getInsulines().add(ins1);
                }

                // Insuline 2 (Optionnelle)
                String nomInsuline2 = request.getParameter("nomInsuline2");
                String uniteInsuline2Str = request.getParameter("uniteInsuline2");
                if (nomInsuline2 != null && !nomInsuline2.trim().isEmpty() &&
                        uniteInsuline2Str != null && !uniteInsuline2Str.trim().isEmpty()) {

                    Insuline ins2 = new Insuline();
                    ins2.setType(nomInsuline2.trim());
                    ins2.setUnite(Double.parseDouble(uniteInsuline2Str));
                    ins2.setGlycemie(glycemie);
                    glycemie.getInsulines().add(ins2);
                }
            }

            glycemieDao.sauvegarder(glycemie);
            response.sendRedirect(request.getContextPath() + "/dashboard");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/dashboard?erreur=1");
        }
    }
}