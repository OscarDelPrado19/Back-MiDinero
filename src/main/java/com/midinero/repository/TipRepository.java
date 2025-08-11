package com.midinero.repository;

import com.midinero.entity.Tip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipRepository extends JpaRepository<Tip, Long> {
    boolean existsByTituloIgnoreCase(String titulo);
}
