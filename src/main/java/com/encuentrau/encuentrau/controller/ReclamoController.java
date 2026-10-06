package com.encuentrau.encuentrau.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.encuentrau.encuentrau.exception.ReclamoNoEncontradoException;
import com.encuentrau.encuentrau.model.EstadoObjeto;
import com.encuentrau.encuentrau.model.Objeto;
import com.encuentrau.encuentrau.model.Reclamo;
import com.encuentrau.encuentrau.service.ObjetoService;
import com.encuentrau.encuentrau.service.ReclamoService;

/**
 * Controller de reclamaciones.
 *
 * POST /reclamos       → registrar una reclamación
 * GET  /reclamos/{id}  → consultar una reclamación por id
 */
@RestController
@RequestMapping("/reclamos")
public class ReclamoController {

    private final ReclamoService reclamoService;
    private final ObjetoService objetoService;

    public ReclamoController(ReclamoService reclamoService, ObjetoService objetoService) {
        this.reclamoService = reclamoService;
        this.objetoService = objetoService;
    }

    /**
     * Registra una nueva reclamación sobre un objeto existente.
     *
     * Flujo:
     * 1. Consulta el objeto por objetoId (lanza ObjetoNoEncontradoException si no existe)
     * 2. Verifica su estado en ReclamoService (lanza IllegalStateException si no es ENCONTRADO)
     * 3. Guarda el reclamo
     * 4. Cambia el estado del objeto a RECLAMADO
     *
     * Ejemplo: POST /reclamos?objetoId=1
     * Body: { "usuario": { "id": 1 }, "descripcion": "Creo que es mío" }
     */
    @PostMapping
    public ResponseEntity<?> registrarReclamo(
            @RequestParam Long objetoId,
            @RequestBody Reclamo reclamo) {
        try {
            // 1. Obtener el objeto — lanza ObjetoNoEncontradoException si no existe
            Objeto objeto = objetoService.buscarObjeto(objetoId);

            // 2. Registrar el reclamo validando el estado del objeto
            Reclamo nuevo = reclamoService.registrarReclamo(objetoId, objeto.getEstado().name(), reclamo);

            // 3. Cambiar el estado del objeto a RECLAMADO
            objetoService.cambiarEstado(objetoId, EstadoObjeto.RECLAMADO);

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        // ObjetoNoEncontradoException la maneja el GlobalExceptionHandler de Daniel
    }

    /**
     * Consulta una reclamación por su id.
     *
     * Ejemplo: GET /reclamos/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarReclamo(@PathVariable Long id) {
        try {
            Reclamo reclamo = reclamoService.buscarReclamo(id);
            return ResponseEntity.ok(reclamo);
        } catch (ReclamoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
