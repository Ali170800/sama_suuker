package com.suivi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "insuline")
public class Insuline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type; // Nom exact de l'insuline (ex: Lantus, Novorapid...)

    @Column(nullable = false)
    private double unite; // Nombre d'unités

    @ManyToOne
    @JoinColumn(name = "glycemie_id", nullable = false)
    private Glycemie glycemie;

    public Insuline() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getUnite() { return unite; }
    public void setUnite(double unite) { this.unite = unite; }

    public Glycemie getGlycemie() { return glycemie; }
    public void setGlycemie(Glycemie glycemie) { this.glycemie = glycemie; }
}