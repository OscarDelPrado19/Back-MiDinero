package com.midinero.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PresupuestoDTO {
    private Long id;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @NotNull(message = "El límite es obligatorio")
    @Positive(message = "El límite debe ser positivo")
    private Double limite;

    @NotBlank(message = "El mes es obligatorio")
    private String mes;

    private Double gastoActual;

    // Constructors
    public PresupuestoDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Double getLimite() { return limite; }
    public void setLimite(Double limite) { this.limite = limite; }

    public String getMes() { return mes; }
    public void setMes(String mes) { this.mes = mes; }

    public Double getGastoActual() { return gastoActual; }
    public void setGastoActual(Double gastoActual) { this.gastoActual = gastoActual; }
}
