package com.midinero.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "transacciones")
public class Transaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "El tipo de transacción es obligatorio")
    @Column(nullable = false)
    private TipoTransaccion tipo;

    @NotBlank(message = "La categoría es obligatoria")
    @Column(nullable = false)
    private String categoria;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    @Column(nullable = false)
    private Double monto;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(length = 500)
    private String descripcion;

    public enum TipoTransaccion {
        INGRESO, GASTO
    }

    // Constructors
    public Transaccion() {}

    public Transaccion(Long id, Usuario usuario, TipoTransaccion tipo, String categoria, Double monto, LocalDateTime fecha, String descripcion) {
        this.id = id;
        this.usuario = usuario;
        this.tipo = tipo;
        this.categoria = categoria;
        this.monto = monto;
        this.fecha = fecha;
        this.descripcion = descripcion;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public TipoTransaccion getTipo() { return tipo; }
    public void setTipo(TipoTransaccion tipo) { this.tipo = tipo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
