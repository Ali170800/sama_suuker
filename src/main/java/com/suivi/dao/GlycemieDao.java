package com.suivi.dao;

import com.suivi.model.Glycemie;
import com.suivi.model.Insuline;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class GlycemieDao {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("glycemiePU");

    public void sauvegarder(Glycemie glycemie) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(glycemie); // Remplacé par merge pour gérer correctement l'entité Insuline
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Glycemie> listerParCompte(Long compteId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT g FROM Glycemie g WHERE g.compte.id = :compteId ORDER BY g.dateHeure DESC", Glycemie.class)
                    .setParameter("compteId", compteId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Glycemie trouverParId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Glycemie.class, id);
        } finally {
            em.close();
        }
    }

    public void mettreAJour(Glycemie glycemie) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(glycemie);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Récupère la dernière insuline enregistrée pour ce compte
     * (utilisée pour l'associer automatiquement aux mesures "Après repas").
     */
    public Insuline trouverDerniereInsulineAvant(Long compteId) {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT i FROM Glycemie g JOIN g.insulines i WHERE g.compte.id = :compteId ORDER BY g.dateHeure DESC";
            List<Insuline> resultat = em.createQuery(jpql, Insuline.class)
                    .setParameter("compteId", compteId)
                    .setMaxResults(1)
                    .getResultList();

            return resultat.isEmpty() ? null : resultat.get(0);
        } finally {
            em.close();
        }
    }
}