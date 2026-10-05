package com.encuentrau.encuentrau.controller;

import com.encuentrau.encuentrau.model.EstadoObjeto;
import com.encuentrau.encuentrau.model.Objeto;
import com.encuentrau.encuentrau.service.ObjetoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller que expone los endpoints REST para la gestión de objetos.
 * Solo recibe la solicitud HTTP, delega al servicio y devuelve la respuesta.
 * La lógica de negocio NO va aquí.
 */
@RestController
@RequestMapping("/objetos")
public class ObjetoController {

    private final ObjetoService objetoService;

    public ObjetoController(ObjetoService objetoService) {
        this.objetoService = objetoService;
    }

    /**
     * POST /objetos
     * Registra un objeto encontrado. El estado siempre será ENCONTRADO.
     *
     * Body de ejemplo:
     * {
     *   "nombre": "Calculadora científica",
     *   "descripcion": "Casio fx-991, color negro",
     *   "categoria": "Electrónico",
     *   "lugarEncontrado": "Salón 204",
     *   "fechaEncontrado": "2026-10-04"
     * }
     */
    @PostMapping
    public ResponseEntity<Objeto> registrarObjeto(@RequestBody Objeto objeto) {
        Objeto guardado = objetoService.registrarObjeto(objeto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    /**
     * GET /objetos
     * Consulta todos los objetos registrados.
     * Opcionalmente filtra por estado con el parámetro ?disponibles=true
     * para obtener solo los objetos en estado ENCONTRADO.
     */
    @GetMapping
    public ResponseEntity<List<Objeto>> listarObjetos(
            @RequestParam(required = false, defaultValue = "false") boolean disponibles) {

        List<Objeto> lista = disponibles
                ? objetoService.listarObjetosDisponibles()
                : objetoService.listarObjetos();

        return ResponseEntity.ok(lista);
    }

    /**
     * GET /objetos/{id}
     * Consulta un objeto específico por su id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Objeto> buscarObjeto(@PathVariable Long id) {
        Objeto objeto = objetoService.buscarObjeto(id);
        return ResponseEntity.ok(objeto);
    }

    /**
     * PUT /objetos/{id}
     * Actualiza la información descriptiva de un objeto.
     * Para cambiar el estado usar PUT /objetos/{id}/estado
     */
    @PutMapping("/{id}")
    public ResponseEntity<Objeto> actualizarObjeto(
            @PathVariable Long id,
            @RequestBody Objeto datos) {
        Objeto actualizado = objetoService.actualizarObjeto(id, datos);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * PUT /objetos/{id}/estado
     * Cambia el estado de un objeto.
     *
     * Parámetro de query: ?nuevoEstado=RECLAMADO  (o ENTREGADO)
     *
     * Ejemplo: PUT /objetos/1/estado?nuevoEstado=RECLAMADO
     */
    @PutMapping("/{id}/estado")
    public ResponseEntity<Objeto> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoObjeto nuevoEstado) {
        Objeto actualizado = objetoService.cambiarEstado(id, nuevoEstado);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * DELETE /objetos/{id}
     * Elimina un objeto del sistema.
     * Solo se permite si el objeto está en estado ENCONTRADO.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarObjeto(@PathVariable Long id) {
        objetoService.eliminarObjeto(id);
        return ResponseEntity.noContent().build();
    }
}
