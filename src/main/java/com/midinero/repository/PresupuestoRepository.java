package com.midinero.repository;

import com.midinero.entity.Presupuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PresupuestoRepository extends JpaRepository<Presupuesto, Long> {
    List<Presupuesto> findByUsuarioId(Long usuarioId);
    Optional<Presupuesto> findByUsuarioIdAndCategoriaAndMes(Long usuarioId, String categoria, String mes);
    List<Presupuesto> findByUsuarioIdAndMes(Long usuarioId, String mes);
}
