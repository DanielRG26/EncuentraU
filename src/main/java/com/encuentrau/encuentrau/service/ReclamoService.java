package com.encuentrau.encuentrau.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.encuentrau.encuentrau.exception.ReclamoNoEncontradoException;
import com.encuentrau.encuentrau.model.Reclamo;
import com.encuentrau.encuentrau.repository.ReclamoRepository;

@Service
public class ReclamoService {

    private static final String ESTADO_ENCONTRADO = "ENCONTRADO";
    private static final String ESTADO_RECLAMADO  = "RECLAMADO";
    private static final String ESTADO_ENTREGADO  = "ENTREGADO";

    private static final String RECLAMO_PENDIENTE = "PENDIENTE";
    private static final String RECLAMO_VALIDADO  = "VALIDADO";

    private final ReclamoRepository reclamoRepository;

    public ReclamoService(ReclamoRepository reclamoRepository) {
        this.reclamoRepository = reclamoRepository;
    }

    /**
     * Registra un nuevo reclamo sobre un objeto.
     *
     * Reglas:
     * - Solo se puede reclamar si el objeto está en estado ENCONTRADO.
     * - No se permite reclamar un objeto ya RECLAMADO o ENTREGADO.
     *
     * @param objetoId  id del objeto sobre el que se reclama
     * @param estadoObjeto  estado actual del objeto (viene de ObjetoService de Daniel)
     * @param reclamo   datos del reclamo a registrar
     * @return el reclamo guardado
     */
    public Reclamo registrarReclamo(Long objetoId, String estadoObjeto, Reclamo reclamo) {
        // Regla: objeto ya RECLAMADO o ENTREGADO → no se permite
        // Las excepciones ObjetoYaReclamadoException y ObjetoYaEntregadoException
        // las lanzará Daniel desde su capa; aquí respetamos el estado recibido.
        if (ESTADO_RECLAMADO.equalsIgnoreCase(estadoObjeto)) {
            throw new IllegalStateException("El objeto con id " + objetoId + " ya fue reclamado.");
        }
        if (ESTADO_ENTREGADO.equalsIgnoreCase(estadoObjeto)) {
            throw new IllegalStateException("El objeto con id " + objetoId + " ya fue entregado y no puede reclamarse nuevamente.");
        }
        if (!ESTADO_ENCONTRADO.equalsIgnoreCase(estadoObjeto)) {
            throw new IllegalStateException("El objeto con id " + objetoId + " no está disponible para ser reclamado.");
        }

        reclamo.setObjetoId(objetoId);
        reclamo.setFecha(LocalDate.now());
        reclamo.setEstado(RECLAMO_PENDIENTE);

        return reclamoRepository.save(reclamo);
    }

    /**
     * Busca un reclamo por su id.
     * Lanza ReclamoNoEncontradoException si no existe.
     */
    public Reclamo buscarReclamo(Long id) {
        return reclamoRepository.findById(id)
                .orElseThrow(() -> new ReclamoNoEncontradoException(id));
    }

    /**
     * Lista todos los reclamos registrados.
     */
    public List<Reclamo> listarReclamos() {
        return reclamoRepository.findAll();
    }

    /**
     * Valida un reclamo: cambia su estado a VALIDADO.
     * Esto habilita que el objeto pueda pasar a ENTREGADO.
     */
    public Reclamo validarReclamo(Long id) {
        Reclamo reclamo = buscarReclamo(id);
        reclamo.setEstado(RECLAMO_VALIDADO);
        return reclamoRepository.save(reclamo);
    }
}
