package com.midinero.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public class MetaAhorroDTO {
    private Long id;

    @NotBlank(message = "El nombre de la meta es obligatorio")
    private String nombre;

    @NotNull(message = "El monto objetivo es obligatorio")
    @PositiveOrZero(message = "El monto objetivo debe ser positivo o cero")
    private Double montoObjetivo;

    @PositiveOrZero(message = "El monto actual debe ser positivo o cero")
    private Double montoActual;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha fin es obligatoria")
    private LocalDate fechaFin;

    private Boolean completada;
    private Double porcentajeCompletado;

    // Constructors
    public MetaAhorroDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Double getPorcentajeCompletado() { return porcentajeCompletado; }
    public void setPorcentajeCompletado(Double porcentajeCompletado) { this.porcentajeCompletado = porcentajeCompletado; }
}
