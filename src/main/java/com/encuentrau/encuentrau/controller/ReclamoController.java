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

import com.encuentrau.encuentrau.model.EstadoObjeto;
import com.encuentrau.encuentrau.model.Objeto;
import com.encuentrau.encuentrau.model.Reclamo;
import com.encuentrau.encuentrau.service.ObjetoService;
import com.encuentrau.encuentrau.service.ReclamoService;

// Puerta de entrada para todo lo relacionado con reclamos
// Todas las rutas de esta clase empiezan con /reclamos
@RestController
@RequestMapping("/reclamos")
public class ReclamoController {

    private final ReclamoService reclamoService;
    private final ObjetoService objetoService;

    // Spring conecta automáticamente los servicios con este controller al arrancar
    public ReclamoController(ReclamoService reclamoService, ObjetoService objetoService) {
        this.reclamoService = reclamoService;
        this.objetoService = objetoService;
    }

    // POST /reclamos?objetoId=1 → registra un reclamo sobre un objeto
    // El número del objeto va en la URL y los datos del reclamo en el body
    // Ejemplo body: { "usuario": { "id": 1 }, "descripcion": "Creo que es mío" }
    @PostMapping
    public ResponseEntity<?> registrarReclamo(
            @RequestParam Long objetoId,
            @RequestBody Reclamo reclamo) {
        try {
            // Busca el objeto — si no existe lanza ObjetoNoEncontradoException (404)
            Objeto objeto = objetoService.buscarObjeto(objetoId);

            // Registra el reclamo validando que el objeto esté en estado ENCONTRADO
            Reclamo nuevo = reclamoService.registrarReclamo(objeto, objeto.getEstado().name(), reclamo);

            // Cambia el estado del objeto a RECLAMADO automáticamente
            objetoService.cambiarEstado(objetoId, EstadoObjeto.RECLAMADO);

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo); // 201 Created

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // 409 Conflict
        }
    }

    // GET /reclamos/{id} → busca un reclamo específico por su número
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarReclamo(@PathVariable Long id) {
        Reclamo reclamo = reclamoService.buscarReclamo(id);
        return ResponseEntity.ok(reclamo); // 200 OK con el reclamo completo
    }

    // GET /reclamos → devuelve todos los reclamos registrados
    @GetMapping
    public ResponseEntity<?> listarReclamos() {
        return ResponseEntity.ok(reclamoService.listarReclamos()); // 200 OK con la lista
    }
}
