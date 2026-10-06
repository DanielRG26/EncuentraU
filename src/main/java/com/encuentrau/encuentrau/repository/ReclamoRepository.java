package com.encuentrau.encuentrau.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.encuentrau.encuentrau.model.Reclamo;

@Repository
public interface ReclamoRepository extends JpaRepository<Reclamo, Long> {

    // Busca todos los reclamos que corresponden a un objeto específico
    // Spring genera la consulta: SELECT * FROM reclamos WHERE objeto_id = ?
    List<Reclamo> findByObjetoId(Long objetoId);

    // Verifica si ya existe un reclamo para un objeto en ciertos estados
    // Útil para saber si un objeto ya fue reclamado antes de permitir otro reclamo
    boolean existsByObjetoIdAndEstadoIn(Long objetoId, List<String> estados);
}
