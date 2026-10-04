package com.encuentrau.encuentrau.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.encuentrau.encuentrau.model.Reclamo;

@Repository
public interface ReclamoRepository extends JpaRepository<Reclamo, Long> {

    List<Reclamo> findByObjetoId(Long objetoId);

    boolean existsByObjetoIdAndEstadoIn(Long objetoId, List<String> estados);
}
