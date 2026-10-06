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

// Esta clase es la puerta de entrada de la aplicación
// Recibe las peticiones que llegan desde afuera (como desde Postman o una app web)
// y las pasa al servicio para que las procese
// Todas las rutas de esta clase empiezan con /objetos
@RestController
@RequestMapping("/objetos")
public class ObjetoController {

    // El servicio es quien sabe qué hacer con cada petición
    // El controller solo recibe y entrega, no toma decisiones
    private final ObjetoService objetoService;

    // Spring conecta automáticamente el servicio con este controller al arrancar
    public ObjetoController(ObjetoService objetoService) {
        this.objetoService = objetoService;
    }

    // -------------------------------------------------------------------------
    // Registrar un objeto nuevo → POST /objetos
    // -------------------------------------------------------------------------

    // Se activa cuando alguien envía una petición POST a /objetos
    // El objeto llega en formato JSON dentro del cuerpo de la petición
    // Responde con el objeto guardado y el código 201 (significa "creado con éxito")
    @PostMapping
    public ResponseEntity<Objeto> registrarObjeto(@RequestBody Objeto objeto) {
        Objeto guardado = objetoService.registrarObjeto(objeto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // -------------------------------------------------------------------------
    // Ver todos los objetos → GET /objetos
    // -------------------------------------------------------------------------

    // Se activa cuando alguien hace GET a /objetos
    // ?disponibles=true  → solo los que aún no fueron reclamados (ENCONTRADO)
    // ?estado=RECLAMADO  → solo los reclamados
    // ?estado=ENTREGADO  → solo los entregados
    // sin parámetros     → devuelve todos
    @GetMapping
    public ResponseEntity<List<Objeto>> listarObjetos(
            @RequestParam(required = false, defaultValue = "false") boolean disponibles,
            @RequestParam(required = false) EstadoObjeto estado) {

        List<Objeto> lista;

        if (estado != null) {
            // filtra por el estado que llegó en la URL (?estado=RECLAMADO, etc.)
            lista = objetoService.listarObjetosPorEstado(estado);
        } else if (disponibles) {
            // atajo rápido para ver solo los disponibles (?disponibles=true)
            lista = objetoService.listarObjetosDisponibles();
        } else {
            // sin parámetros: devuelve todos
            lista = objetoService.listarObjetos();
        }

        return ResponseEntity.ok(lista);
    }

    // -------------------------------------------------------------------------
    // Buscar un objeto por su número → GET /objetos/{id}
    // -------------------------------------------------------------------------

    // Se activa cuando alguien hace GET a /objetos/3 (o cualquier número)
    // El número de la URL se usa para buscar ese objeto específico
    // Responde con el objeto encontrado y el código 200
    @GetMapping("/{id}")
    public ResponseEntity<Objeto> buscarObjeto(@PathVariable Long id) {
        Objeto objeto = objetoService.buscarObjeto(id);
        return ResponseEntity.ok(objeto);
    }

    // -------------------------------------------------------------------------
    // Actualizar los datos de un objeto → PUT /objetos/{id}
    // -------------------------------------------------------------------------

    // Se activa cuando alguien hace PUT a /objetos/3 (o cualquier número)
    // El número indica cuál objeto actualizar, y el JSON del cuerpo trae los nuevos datos
    // Responde con el objeto ya actualizado y el código 200
    @PutMapping("/{id}")
    public ResponseEntity<Objeto> actualizarObjeto(
            @PathVariable Long id,       // número del objeto que se quiere cambiar
            @RequestBody Objeto datos) { // datos nuevos que llegan en el cuerpo de la petición
        Objeto actualizado = objetoService.actualizarObjeto(id, datos);
        return ResponseEntity.ok(actualizado);
    }

    // -------------------------------------------------------------------------
    // Cambiar el estado de un objeto → PUT /objetos/{id}/estado
    // -------------------------------------------------------------------------

    // Endpoint separado solo para cambiar el estado, para no mezclar con actualizar datos
    // Ejemplo: PUT /objetos/1/estado?nuevoEstado=RECLAMADO
    // El nuevo estado se pasa como parámetro en la URL, no en el cuerpo
    @PutMapping("/{id}/estado")
    public ResponseEntity<Objeto> cambiarEstado(
            @PathVariable Long id,                    // número del objeto
            @RequestParam EstadoObjeto nuevoEstado) { // nuevo estado que llega en la URL (?nuevoEstado=...)
        Objeto actualizado = objetoService.cambiarEstado(id, nuevoEstado);
        return ResponseEntity.ok(actualizado);
    }

    // -------------------------------------------------------------------------
    // Eliminar un objeto → DELETE /objetos/{id}
    // -------------------------------------------------------------------------

    // Se activa cuando alguien hace DELETE a /objetos/3 (o cualquier número)
    // Solo se puede eliminar si el objeto todavía no fue reclamado ni entregado
    // Responde con el código 204 (significa "listo, no hay nada que mostrar")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarObjeto(@PathVariable Long id) {
        objetoService.eliminarObjeto(id);
        return ResponseEntity.noContent().build();
    }
}
