package com.midinero.repository;

import com.midinero.entity.TokenReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TokenResetRepository extends JpaRepository<TokenReset, Long> {
    Optional<TokenReset> findByTokenAndUsadoFalseAndFechaExpiracionAfter(String token, LocalDateTime now);
    void deleteByFechaExpiracionBefore(LocalDateTime now);
}
