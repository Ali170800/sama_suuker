package com.suivi.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "glycemie")
public class Glycemie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double valeur;

    @Column(nullable = false)
    private String type;

    @Column(name = "date_heure", nullable = false)
    private LocalDateTime dateHeure;

    @ManyToOne
    @JoinColumn(name = "compte_id", nullable = false)
    private Compte compte;

    // CORRECTION : Ajout de fetch = FetchType.EAGER pour charger les insulines immédiatement
    @OneToMany(mappedBy = "glycemie", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Insuline> insulines = new ArrayList<>();

    public Glycemie() {
        this.dateHeure = LocalDateTime.now();
    }

    public String getDateHeureFormatee() {
        if (this.dateHeure == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE 'le' dd-MM-yyyy 'à' HH'h'mm", Locale.FRENCH);
        return this.dateHeure.format(formatter);
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public double getValeur() { return valeur; }
    public void setValeur(double valeur) { this.valeur = valeur; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDateTime getDateHeure() { return dateHeure; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }

    public Compte getCompte() { return compte; }
    public void setCompte(Compte compte) { this.compte = compte; }

    public List<Insuline> getInsulines() { return insulines; }
    public void setInsulines(List<Insuline> insulines) { this.insulines = insulines; }

    // Méthode utilitaire pratique
    public void addInsuline(Insuline insuline) {
        insulines.add(insuline);
        insuline.setGlycemie(this);
    }
}