package com.midinero.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    @Email(message = "Email debe ser válido")
    @NotBlank(message = "El email es obligatorio")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(nullable = false)
    private String password;

    @Column
    private String carrera;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    // === NUEVO: saldo del usuario ===
    @Column(name = "saldo", nullable = false)
    private Double saldo = 0.0;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaccion> transacciones;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Presupuesto> presupuestos;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MetaAhorro> metasAhorro;

    @OneToMany(
            mappedBy = "usuario",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<TokenReset> tokensReset = new ArrayList<>();

    // Constructors
    public Usuario() {}

    public Usuario(String nombreCompleto, String email, String password) {
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.password = password;
    }

    // === Helpers de dominio para saldo ===
    public void acreditar(Double monto) {
        if (monto == null || monto <= 0) {
            throw new IllegalArgumentException("Monto a acreditar inválido");
        }
        this.saldo += monto;
    }

    public void debitar(Double monto) {
        if (monto == null || monto <= 0) {
            throw new IllegalArgumentException("Monto a debitar inválido");
        }
        if (this.saldo < monto) {
            throw new IllegalStateException("Saldo insuficiente");
        }
        this.saldo -= monto;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCarrera() { return carrera; }
    public void setCarrera(String carrera) { this.carrera = carrera; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Double getSaldo() { return saldo; }
    public void setSaldo(Double saldo) { this.saldo = (saldo != null ? saldo : 0.0); }

    public List<Transaccion> getTransacciones() { return transacciones; }
    public void setTransacciones(List<Transaccion> transacciones) { this.transacciones = transacciones; }

    public List<Presupuesto> getPresupuestos() { return presupuestos; }
    public void setPresupuestos(List<Presupuesto> presupuestos) { this.presupuestos = presupuestos; }

    public List<MetaAhorro> getMetasAhorro() { return metasAhorro; }
    public void setMetasAhorro(List<MetaAhorro> metasAhorro) { this.metasAhorro = metasAhorro; }

    public List<TokenReset> getTokensReset() { return tokensReset; }
    public void setTokensReset(List<TokenReset> tokensReset) { this.tokensReset = tokensReset; }
}
