package com.midinero.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

@Entity
@Table(name = "metas_ahorro")
public class MetaAhorro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotBlank(message = "El nombre de la meta es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotNull(message = "El monto objetivo es obligatorio")
    @PositiveOrZero(message = "El monto objetivo debe ser positivo o cero")
    @Column(name = "monto_objetivo", nullable = false)
    private Double montoObjetivo;

    @PositiveOrZero(message = "El monto actual debe ser positivo o cero")
    @Column(name = "monto_actual", nullable = false)
    private Double montoActual = 0.0;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha fin es obligatoria")
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "completada")
    private Boolean completada = false;

    // Constructors
    public MetaAhorro() {}

    public MetaAhorro(Usuario usuario, String nombre, Double montoObjetivo, LocalDate fechaInicio, LocalDate fechaFin) {
        this.usuario = usuario;
        this.nombre = nombre;
        this.montoObjetivo = montoObjetivo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getMontoObjetivo() { return montoObjetivo; }
    public void setMontoObjetivo(Double montoObjetivo) { this.montoObjetivo = montoObjetivo; }

    public Double getMontoActual() { return montoActual; }
    public void setMontoActual(Double montoActual) { this.montoActual = montoActual; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public Boolean getCompletada() { return completada; }
    public void setCompletada(Boolean completada) { this.completada = completada; }
}
