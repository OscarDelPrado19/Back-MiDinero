package com.midinero.repository;

import com.midinero.entity.MetaAhorro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetaAhorroRepository extends JpaRepository<MetaAhorro, Long> {
    List<MetaAhorro> findByUsuarioIdOrderByFechaInicioDesc(Long usuarioId);
    List<MetaAhorro> findByUsuarioIdAndCompletada(Long usuarioId, Boolean completada);
}
