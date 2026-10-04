package com.encuentrau.encuentrau.controller;

import com.encuentrau.encuentrau.model.Reclamo;
import com.encuentrau.encuentrau.service.ReclamoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de reclamaciones.
 *
 * POST /reclamos           → registrar una reclamación
 * GET  /reclamos/{id}      → consultar una reclamación por id
 */
@RestController
@RequestMapping("/reclamos")
public class ReclamoController {

    private final ReclamoService reclamoService;

    public ReclamoController(ReclamoService reclamoService) {
        this.reclamoService = reclamoService;
    }

    /**
     * Registra una nueva reclamación.
     *
     * El body del request debe incluir el reclamo.
     * El parámetro estadoObjeto se recibe como query param mientras
     * se integra con ObjetoService de Daniel.
     *
     * Ejemplo: POST /reclamos?objetoId=1&estadoObjeto=ENCONTRADO
     */
    @PostMapping
    public ResponseEntity<?> registrarReclamo(
            @RequestParam Long objetoId,
            @RequestParam String estadoObjeto,
            @RequestBody Reclamo reclamo) {
        try {
            Reclamo nuevo = reclamoService.registrarReclamo(objetoId, estadoObjeto, reclamo);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
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
        } catch (com.encuentrau.encuentrau.exception.ReclamoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
