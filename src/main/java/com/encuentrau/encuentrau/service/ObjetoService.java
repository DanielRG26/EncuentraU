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

// Esta clase contiene todas las reglas del negocio
// Es la que decide qué se puede hacer y qué no
// El controller le pasa las peticiones y esta clase las resuelve
@Service
public class ObjetoService {

    private final ObjetoRepository objetoRepository;

    // Spring conecta automáticamente el repositorio con este servicio al arrancar
    public ObjetoService(ObjetoRepository objetoRepository) {
        this.objetoRepository = objetoRepository;
    }

    // -------------------------------------------------------------------------
    // REGISTRAR
    // -------------------------------------------------------------------------

    public Objeto registrarObjeto(Objeto objeto) {
        validarDatosObjeto(objeto); // primero revisa que no falte ningún dato obligatorio

        // Sin importar lo que mande el usuario, el estado siempre empieza como ENCONTRADO
        objeto.setEstado(EstadoObjeto.ENCONTRADO);

        // Si no mandaron la fecha, se usa la de hoy
        if (objeto.getFechaEncontrado() == null) {
            objeto.setFechaEncontrado(LocalDate.now());
        }

        return objetoRepository.save(objeto); // guarda el objeto en la base de datos y lo devuelve con su número asignado
    }

    // -------------------------------------------------------------------------
    // CONSULTAR
    // -------------------------------------------------------------------------

    public List<Objeto> listarObjetos() {
        return objetoRepository.findAll(); // trae todos los objetos que hay en la base de datos
    }

    public Objeto buscarObjeto(Long id) {
        // busca el objeto por su número; si no existe, lanza un error en vez de devolver vacío
        return objetoRepository.findById(id)
                .orElseThrow(() -> new ObjetoNoEncontradoException(id));
    }

    public List<Objeto> listarObjetosDisponibles() {
        // trae solo los objetos que están esperando ser reclamados
        return objetoRepository.findByEstado(EstadoObjeto.ENCONTRADO);
    }

    public List<Objeto> listarObjetosPorEstado(EstadoObjeto estado) {
        // trae los objetos que tienen un estado específico (ENCONTRADO, RECLAMADO o ENTREGADO)
        return objetoRepository.findByEstado(estado);
    }

    // -------------------------------------------------------------------------
    // ACTUALIZAR
    // -------------------------------------------------------------------------

    public Objeto actualizarObjeto(Long id, Objeto datos) {
        Objeto existente = buscarObjeto(id); // primero busca el objeto; si no existe, lanza error

        // Si el objeto ya fue entregado, no se puede tocar más
        if (existente.getEstado() == EstadoObjeto.ENTREGADO) {
            throw new ObjetoYaEntregadoException(id);
        }

        validarDatosObjeto(datos); // revisa que los datos nuevos también sean válidos

        // Reemplaza los datos viejos con los nuevos (sin tocar el estado ni el número)
        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        existente.setCategoria(datos.getCategoria());
        existente.setLugarEncontrado(datos.getLugarEncontrado());
        existente.setFechaEncontrado(datos.getFechaEncontrado());

        return objetoRepository.save(existente); // guarda los cambios en la base de datos
    }

    // -------------------------------------------------------------------------
    // CAMBIAR ESTADO
    // -------------------------------------------------------------------------

    public Objeto cambiarEstado(Long id, EstadoObjeto nuevoEstado) {
        Objeto objeto = buscarObjeto(id); // primero busca el objeto; si no existe, lanza error
        EstadoObjeto estadoActual = objeto.getEstado();

        // Si ya fue entregado, el caso está cerrado y no se puede hacer nada más
        if (estadoActual == EstadoObjeto.ENTREGADO) {
            throw new ObjetoYaEntregadoException(id);
        }

        // No tiene sentido reclamar algo que ya está reclamado
        if (estadoActual == EstadoObjeto.RECLAMADO && nuevoEstado == EstadoObjeto.RECLAMADO) {
            throw new ObjetoYaReclamadoException(id);
        }

        objeto.setEstado(nuevoEstado);
        return objetoRepository.save(objeto); // guarda el nuevo estado en la base de datos
    }

    // -------------------------------------------------------------------------
    // ELIMINAR
    // -------------------------------------------------------------------------

    public void eliminarObjeto(Long id) {
        Objeto objeto = buscarObjeto(id); // primero busca el objeto; si no existe, lanza error

        // No se puede borrar si ya hay alguien que lo reclamó
        if (objeto.getEstado() == EstadoObjeto.RECLAMADO) {
            throw new ObjetoYaReclamadoException(
                    "No se puede eliminar el objeto con id " + id + " porque ya tiene una reclamación activa.");
        }

        // No se puede borrar si ya fue entregado a su dueño
        if (objeto.getEstado() == EstadoObjeto.ENTREGADO) {
            throw new ObjetoYaEntregadoException(
                    "No se puede eliminar el objeto con id " + id + " porque ya fue entregado.");
        }

        objetoRepository.deleteById(id); // elimina el objeto de la base de datos
    }

    // -------------------------------------------------------------------------
    // VALIDACIÓN INTERNA
    // -------------------------------------------------------------------------

    // Revisa que el objeto tenga todos los datos obligatorios antes de guardarlo
    // Si falta alguno, lanza un error explicando cuál campo está vacío
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
