package com.encuentrau.encuentrau.service;

import com.encuentrau.encuentrau.exception.DatosObjetoInvalidosException;
import com.encuentrau.encuentrau.exception.ObjetoNoEncontradoException;
import com.encuentrau.encuentrau.exception.ObjetoYaEntregadoException;
import com.encuentrau.encuentrau.exception.ObjetoYaReclamadoException;
import com.encuentrau.encuentrau.model.EstadoObjeto;
import com.encuentrau.encuentrau.model.Objeto;
import com.encuentrau.encuentrau.repository.ObjetoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio que concentra la lógica de negocio relacionada con objetos.
 * Toda validación de reglas del dominio ocurre aquí, nunca en el controller
 * ni en el repository.
 */
@Service
public class ObjetoService {

    private final ObjetoRepository objetoRepository;

    // Inyección por constructor (buena práctica en Spring)
    public ObjetoService(ObjetoRepository objetoRepository) {
        this.objetoRepository = objetoRepository;
    }

    // -------------------------------------------------------------------------
    // REGISTRAR
    // -------------------------------------------------------------------------

    /**
     * Registra un objeto encontrado en el sistema.
     * El objeto siempre se crea con estado ENCONTRADO.
     *
     * Reglas:
     * - nombre, descripcion, categoria, lugarEncontrado y fechaEncontrado
     *   son obligatorios. Si falta alguno lanza DatosObjetoInvalidosException.
     *
     * @param objeto datos del objeto a registrar (sin id ni estado)
     * @return el objeto guardado con id asignado y estado ENCONTRADO
     */
    public Objeto registrarObjeto(Objeto objeto) {
        validarDatosObjeto(objeto);

        // El estado inicial siempre es ENCONTRADO, sin importar lo que venga en el body
        objeto.setEstado(EstadoObjeto.ENCONTRADO);

        // Si no viene fecha, se asigna la de hoy
        if (objeto.getFechaEncontrado() == null) {
            objeto.setFechaEncontrado(LocalDate.now());
        }

        return objetoRepository.save(objeto);
    }

    // -------------------------------------------------------------------------
    // CONSULTAR
    // -------------------------------------------------------------------------

    /**
     * Devuelve todos los objetos registrados en el sistema.
     *
     * @return lista de todos los objetos
     */
    public List<Objeto> listarObjetos() {
        return objetoRepository.findAll();
    }

    /**
     * Busca un objeto por su id.
     *
     * @param id identificador del objeto
     * @return el objeto encontrado
     * @throws ObjetoNoEncontradoException si no existe un objeto con ese id
     */
    public Objeto buscarObjeto(Long id) {
        return objetoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNoEncontradoException(id));
    }

    /**
     * Devuelve solo los objetos que están en estado ENCONTRADO.
     * Útil para mostrar los objetos disponibles para reclamar.
     *
     * @return lista de objetos disponibles
     */
    public List<Objeto> listarObjetosDisponibles() {
        return objetoRepository.findByEstado(EstadoObjeto.ENCONTRADO);
    }

    // -------------------------------------------------------------------------
    // ACTUALIZAR
    // -------------------------------------------------------------------------

    /**
     * Actualiza la información descriptiva de un objeto (nombre, descripcion,
     * categoria, lugarEncontrado, fechaEncontrado).
     * No permite cambiar el estado desde aquí; para eso está cambiarEstado().
     *
     * @param id   id del objeto a actualizar
     * @param datos objeto con los nuevos valores
     * @return el objeto actualizado
     * @throws ObjetoNoEncontradoException   si no existe
     * @throws ObjetoYaEntregadoException    si el objeto ya fue entregado
     * @throws DatosObjetoInvalidosException si los datos nuevos son inválidos
     */
    public Objeto actualizarObjeto(Long id, Objeto datos) {
        Objeto existente = buscarObjeto(id);

        // Un objeto ENTREGADO es un caso cerrado; no se modifica
        if (existente.getEstado() == EstadoObjeto.ENTREGADO) {
            throw new ObjetoYaEntregadoException(id);
        }

        validarDatosObjeto(datos);

        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        existente.setCategoria(datos.getCategoria());
        existente.setLugarEncontrado(datos.getLugarEncontrado());
        existente.setFechaEncontrado(datos.getFechaEncontrado());

        return objetoRepository.save(existente);
    }

    // -------------------------------------------------------------------------
    // CAMBIAR ESTADO
    // -------------------------------------------------------------------------

    /**
     * Cambia el estado de un objeto respetando las reglas del flujo:
     *   ENCONTRADO → RECLAMADO → ENTREGADO
     *
     * Reglas:
     * - No se puede reclamar un objeto ya RECLAMADO.
     * - No se puede reclamar ni modificar un objeto ENTREGADO.
     * - El cambio debe seguir la secuencia definida.
     *
     * @param id          id del objeto
     * @param nuevoEstado el estado al que se quiere cambiar
     * @return el objeto con el estado actualizado
     * @throws ObjetoNoEncontradoException si no existe
     * @throws ObjetoYaReclamadoException  si se intenta reclamar uno ya reclamado
     * @throws ObjetoYaEntregadoException  si se intenta operar sobre uno entregado
     */
    public Objeto cambiarEstado(Long id, EstadoObjeto nuevoEstado) {
        Objeto objeto = buscarObjeto(id);
        EstadoObjeto estadoActual = objeto.getEstado();

        // Un objeto ENTREGADO es un caso cerrado; ningún cambio es posible
        if (estadoActual == EstadoObjeto.ENTREGADO) {
            throw new ObjetoYaEntregadoException(id);
        }

        // No se puede reclamar un objeto que ya está reclamado
        if (estadoActual == EstadoObjeto.RECLAMADO && nuevoEstado == EstadoObjeto.RECLAMADO) {
            throw new ObjetoYaReclamadoException(id);
        }

        objeto.setEstado(nuevoEstado);
        return objetoRepository.save(objeto);
    }

    // -------------------------------------------------------------------------
    // ELIMINAR
    // -------------------------------------------------------------------------

    /**
     * Elimina el registro de un objeto del sistema.
     * Solo se permite eliminar objetos en estado ENCONTRADO.
     * No se elimina un objeto que ya fue reclamado o entregado para preservar
     * la trazabilidad del proceso.
     *
     * @param id id del objeto a eliminar
     * @throws ObjetoNoEncontradoException si no existe
     * @throws ObjetoYaReclamadoException  si el objeto ya fue reclamado
     * @throws ObjetoYaEntregadoException  si el objeto ya fue entregado
     */
    public void eliminarObjeto(Long id) {
        Objeto objeto = buscarObjeto(id);

        if (objeto.getEstado() == EstadoObjeto.RECLAMADO) {
            throw new ObjetoYaReclamadoException(
                    "No se puede eliminar el objeto con id " + id + " porque ya tiene una reclamación activa.");
        }

        if (objeto.getEstado() == EstadoObjeto.ENTREGADO) {
            throw new ObjetoYaEntregadoException(
                    "No se puede eliminar el objeto con id " + id + " porque ya fue entregado.");
        }

        objetoRepository.deleteById(id);
    }

    // -------------------------------------------------------------------------
    // VALIDACIÓN INTERNA
    // -------------------------------------------------------------------------

    /**
     * Verifica que los campos obligatorios del objeto no sean nulos ni vacíos.
     * Si alguno falta, lanza DatosObjetoInvalidosException con un mensaje claro.
     */
    private void validarDatosObjeto(Objeto objeto) {
        if (objeto.getNombre() == null || objeto.getNombre().isBlank()) {
            throw new DatosObjetoInvalidosException("El nombre del objeto es obligatorio.");
        }
        if (objeto.getDescripcion() == null || objeto.getDescripcion().isBlank()) {
            throw new DatosObjetoInvalidosException("La descripción del objeto es obligatoria.");
        }
        if (objeto.getCategoria() == null || objeto.getCategoria().isBlank()) {
            throw new DatosObjetoInvalidosException("La categoría del objeto es obligatoria.");
        }
        if (objeto.getLugarEncontrado() == null || objeto.getLugarEncontrado().isBlank()) {
            throw new DatosObjetoInvalidosException("El lugar donde fue encontrado el objeto es obligatorio.");
        }
    }
}
