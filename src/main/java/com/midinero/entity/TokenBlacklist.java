package com.midinero.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tokens_blacklist")
public class TokenBlacklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 1000)
    private String token;

    @CreationTimestamp
    @Column(name = "fecha_bloqueo", nullable = false)
    private LocalDateTime fechaBloqueo;

    // Constructors
    public TokenBlacklist() {}

    public TokenBlacklist(String token) {
        this.token = token;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public LocalDateTime getFechaBloqueo() { return fechaBloqueo; }
    public void setFechaBloqueo(LocalDateTime fechaBloqueo) { this.fechaBloqueo = fechaBloqueo; }
}
