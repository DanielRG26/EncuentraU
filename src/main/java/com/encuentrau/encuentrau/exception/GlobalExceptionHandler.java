package com.encuentrau.encuentrau.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejador global de excepciones.
 * Intercepta las excepciones lanzadas por el servicio y las convierte en
 * respuestas HTTP con un cuerpo JSON claro para el cliente.
 *
 * De esta forma el controller permanece limpio y las respuestas de error
 * son consistentes en toda la API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Objeto no encontrado → 404 Not Found
     */
    @ExceptionHandler(ObjetoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleObjetoNoEncontrado(
            ObjetoNoEncontradoException ex) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Objeto ya reclamado → 409 Conflict
     */
    @ExceptionHandler(ObjetoYaReclamadoException.class)
    public ResponseEntity<Map<String, Object>> handleObjetoYaReclamado(
            ObjetoYaReclamadoException ex) {
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Objeto ya entregado → 409 Conflict
     */
    @ExceptionHandler(ObjetoYaEntregadoException.class)
    public ResponseEntity<Map<String, Object>> handleObjetoYaEntregado(
            ObjetoYaEntregadoException ex) {
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Datos inválidos → 400 Bad Request
     */
    @ExceptionHandler(DatosObjetoInvalidosException.class)
    public ResponseEntity<Map<String, Object>> handleDatosInvalidos(
            DatosObjetoInvalidosException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Cualquier otra excepción no controlada → 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleExcepcionGeneral(Exception ex) {
        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado en el servidor.");
    }

    // -------------------------------------------------------------------------
    // Método auxiliar para armar el cuerpo de la respuesta de error
    // -------------------------------------------------------------------------

    private ResponseEntity<Map<String, Object>> construirRespuesta(HttpStatus status, String mensaje) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now().toString());
        cuerpo.put("status", status.value());
        cuerpo.put("error", status.getReasonPhrase());
        cuerpo.put("mensaje", mensaje);

        return ResponseEntity.status(status).body(cuerpo);
    }
}
