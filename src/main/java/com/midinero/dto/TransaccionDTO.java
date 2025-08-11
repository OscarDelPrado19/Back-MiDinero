package com.midinero.dto;

import com.midinero.entity.Transaccion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class TransaccionDTO {
    private Long id;

    @NotNull(message = "El tipo de transacción es obligatorio")
    private Transaccion.TipoTransaccion tipo;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    private Double monto; // 👈 cambiado a Double

    private String descripcion;
    private LocalDateTime fecha;

    public TransaccionDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Transaccion.TipoTransaccion getTipo() { return tipo; }
    public void setTipo(Transaccion.TipoTransaccion tipo) { this.tipo = tipo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
