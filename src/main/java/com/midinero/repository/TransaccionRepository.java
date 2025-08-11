package com.midinero.repository;

import com.midinero.entity.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    List<Transaccion> findByUsuarioIdOrderByFechaDesc(Long usuarioId);
    
    @Query("SELECT SUM(t.monto) FROM Transaccion t WHERE t.usuario.id = :usuarioId AND t.tipo = :tipo AND t.categoria = :categoria AND t.fecha >= :fechaInicio AND t.fecha <= :fechaFin")
    Double sumByUsuarioIdAndTipoAndCategoriaAndFechaBetween(
        @Param("usuarioId") Long usuarioId,
        @Param("tipo") Transaccion.TipoTransaccion tipo,
        @Param("categoria") String categoria,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
    
    List<Transaccion> findByUsuarioIdAndTipoAndFechaBetween(
        Long usuarioId, 
        Transaccion.TipoTransaccion tipo, 
        LocalDateTime fechaInicio, 
        LocalDateTime fechaFin
    );
}
