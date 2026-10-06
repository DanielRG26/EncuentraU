package com.encuentrau.encuentrau.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.encuentrau.encuentrau.exception.ReclamoNoEncontradoException;
import com.encuentrau.encuentrau.model.Objeto;
import com.encuentrau.encuentrau.model.Reclamo;
import com.encuentrau.encuentrau.repository.ReclamoRepository;

// Esta clase contiene todas las reglas del negocio relacionadas con reclamos
@Service
public class ReclamoService {

    private static final String ESTADO_ENCONTRADO = "ENCONTRADO";
    private static final String ESTADO_RECLAMADO  = "RECLAMADO";
    private static final String ESTADO_ENTREGADO  = "ENTREGADO";

    private static final String RECLAMO_PENDIENTE = "PENDIENTE";
    private static final String RECLAMO_VALIDADO  = "VALIDADO";

    private final ReclamoRepository reclamoRepository;

    // Spring conecta automáticamente el repositorio con este servicio al arrancar
    public ReclamoService(ReclamoRepository reclamoRepository) {
        this.reclamoRepository = reclamoRepository;
    }

    /**
     * Registra un nuevo reclamo sobre un objeto.
     * Solo se puede reclamar si el objeto está en estado ENCONTRADO.
     *
     * @param objeto       el objeto completo sobre el que se reclama
     * @param estadoObjeto estado actual del objeto
     * @param reclamo      datos del reclamo (usuario y descripción)
     * @return el reclamo guardado con todos sus datos completos
     */
    public Reclamo registrarReclamo(Objeto objeto, String estadoObjeto, Reclamo reclamo) {
        // Si el objeto ya fue reclamado, no se puede volver a reclamar
        if (ESTADO_RECLAMADO.equalsIgnoreCase(estadoObjeto)) {
            throw new IllegalStateException("El objeto con id " + objeto.getId() + " ya fue reclamado.");
        }
        // Si el objeto ya fue entregado, el caso está cerrado
        if (ESTADO_ENTREGADO.equalsIgnoreCase(estadoObjeto)) {
            throw new IllegalStateException("El objeto con id " + objeto.getId() + " ya fue entregado y no puede reclamarse nuevamente.");
        }
        // Solo objetos en estado ENCONTRADO pueden reclamarse
        if (!ESTADO_ENCONTRADO.equalsIgnoreCase(estadoObjeto)) {
            throw new IllegalStateException("El objeto con id " + objeto.getId() + " no está disponible para ser reclamado.");
        }

        // Asigna el objeto real (con relación en BD), la fecha de hoy y estado PENDIENTE
        reclamo.setObjeto(objeto);
        reclamo.setFecha(LocalDate.now());
        reclamo.setEstado(RECLAMO_PENDIENTE);

        Reclamo guardado = reclamoRepository.save(reclamo);
        // Recarga desde la BD para que objeto y usuario salgan con todos sus datos completos
        return reclamoRepository.findById(guardado.getId()).orElse(guardado);
    }

    // Busca un reclamo por su número; lanza error si no existe
    public Reclamo buscarReclamo(Long id) {
        return reclamoRepository.findById(id)
                .orElseThrow(() -> new ReclamoNoEncontradoException(id));
    }

    // Devuelve todos los reclamos registrados
    public List<Reclamo> listarReclamos() {
        return reclamoRepository.findAll();
    }

    // Cambia el estado de un reclamo a VALIDADO (el encargado confirmó que el dueño es correcto)
    public Reclamo validarReclamo(Long id) {
        Reclamo reclamo = buscarReclamo(id);
        reclamo.setEstado(RECLAMO_VALIDADO);
        return reclamoRepository.save(reclamo);
    }
}
