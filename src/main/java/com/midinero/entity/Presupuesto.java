package com.midinero.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "presupuestos")
public class Presupuesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotBlank(message = "La categoría es obligatoria")
    @Column(nullable = false)
    private String categoria;

    @NotNull(message = "El límite es obligatorio")
    @Positive(message = "El límite debe ser positivo")
    @Column(nullable = false)
    private Double limite;

    @NotBlank(message = "El mes es obligatorio")
    @Column(nullable = false)
    private String mes; // Formato: "2024-01"

    // Constructors
    public Presupuesto() {}

    public Presupuesto(Usuario usuario, String categoria, Double limite, String mes) {
        this.usuario = usuario;
        this.categoria = categoria;
        this.limite = limite;
        this.mes = mes;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Double getLimite() { return limite; }
    public void setLimite(Double limite) { this.limite = limite; }

    public String getMes() { return mes; }
    public void setMes(String mes) { this.mes = mes; }
}
