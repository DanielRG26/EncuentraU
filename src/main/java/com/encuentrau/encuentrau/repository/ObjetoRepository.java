package com.encuentrau.encuentrau.repository;

import com.encuentrau.encuentrau.model.EstadoObjeto;
import com.encuentrau.encuentrau.model.Objeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de acceso a datos para la entidad Objeto.
 * Extiende JpaRepository para obtener las operaciones CRUD básicas sin escribirlas.
 * Las reglas de negocio NO van aquí — pertenecen a ObjetoService.
 */
@Repository
public interface ObjetoRepository extends JpaRepository<Objeto, Long> {

    /**
     * Busca todos los objetos que se encuentren en un estado específico.
     * Útil para listar solo los objetos disponibles (ENCONTRADO) o en proceso.
     *
     * @param estado el estado por el que se quiere filtrar
     * @return lista de objetos con ese estado
     */
    List<Objeto> findByEstado(EstadoObjeto estado);
}
