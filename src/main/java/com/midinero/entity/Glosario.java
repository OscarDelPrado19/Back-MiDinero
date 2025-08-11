package com.midinero.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "glosario")
public class Glosario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El término es obligatorio")
    @Column(nullable = false, unique = true)
    private String termino;

    @NotBlank(message = "La definición es obligatoria")
    @Column(nullable = false, length = 1000)
    private String definicion;

    // Constructors
    public Glosario() {}

    public Glosario(String termino, String definicion) {
        this.termino = termino;
        this.definicion = definicion;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTermino() { return termino; }
    public void setTermino(String termino) { this.termino = termino; }

    public String getDefinicion() { return definicion; }
    public void setDefinicion(String definicion) { this.definicion = definicion; }
}
