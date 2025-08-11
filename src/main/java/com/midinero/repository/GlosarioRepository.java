package com.midinero.repository;

import com.midinero.entity.Glosario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlosarioRepository extends JpaRepository<Glosario, Long> {
    boolean existsByTerminoIgnoreCase(String termino);
}
