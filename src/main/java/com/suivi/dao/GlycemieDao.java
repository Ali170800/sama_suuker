package com.suivi.dao;

import com.suivi.model.Glycemie;
import com.suivi.model.Insuline;
import jakarta.persistence.EntityManager;
import java.util.List;

public class GlycemieDao {

    // On ne crée plus d'EntityManagerFactory ici, le filtre et JPAUtil s'en chargent.

    public void sauvegarder(EntityManager em, Glycemie glycemie) {
        // Le filtre gère la transaction globale si besoin, ou on gère la transaction locale ici.
        // Si vous utilisez une transaction par requête dans le filtre, vous pouvez l'enlever.
        // Par sécurité, si le filtre ne gère que l'ouverture/fermeture :
        boolean transactionActiveLocally = false;
        try {
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
                transactionActiveLocally = true;
            }

            em.merge(glycemie);

            if (transactionActiveLocally) {
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            if (transactionActiveLocally && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    public List<Glycemie> listerParCompte(EntityManager em, Long compteId) {
        return em.createQuery("SELECT g FROM Glycemie g WHERE g.compte.id = :compteId ORDER BY g.dateHeure DESC", Glycemie.class)
                .setParameter("compteId", compteId)
                .getResultList();
    }

    public Glycemie trouverParId(EntityManager em, Long id) {
        return em.find(Glycemie.class, id);
    }

    public void mettreAJour(EntityManager em, Glycemie glycemie) {
        boolean transactionActiveLocally = false;
        try {
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
                transactionActiveLocally = true;
            }

            em.merge(glycemie);

            if (transactionActiveLocally) {
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            if (transactionActiveLocally && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    /**
     * Récupère la dernière insuline enregistrée pour ce compte
     * (utilisée pour l'associer automatiquement aux mesures "Après repas").
     */
    public Insuline trouverDerniereInsulineAvant(EntityManager em, Long compteId) {
        String jpql = "SELECT i FROM Glycemie g JOIN g.insulines i WHERE g.compte.id = :compteId ORDER BY g.dateHeure DESC";
        List<Insuline> resultat = em.createQuery(jpql, Insuline.class)
                .setParameter("compteId", compteId)
                .setMaxResults(1)
                .getResultList();

        return resultat.isEmpty() ? null : resultat.get(0);
    }
}