package com.midinero.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "tips")
public class Tip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Column(nullable = false, unique = true, length = 100)
    private String titulo;

    @NotBlank(message = "El contenido es obligatorio")
    @Column(nullable = false, length = 1000)
    private String contenido;

    // Constructors
    public Tip() {}

    public Tip(String titulo, String contenido) {
        this.titulo = titulo;
        this.contenido = contenido;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
}
