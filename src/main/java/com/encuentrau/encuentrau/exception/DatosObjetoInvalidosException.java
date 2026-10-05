package com.encuentrau.encuentrau.exception;

/**
 * Se lanza cuando se intenta registrar un objeto con información obligatoria
 * ausente o inválida (nombre, descripción, categoría, lugar o fecha vacíos).
 * Representa la regla: no se puede registrar un objeto sin datos mínimos.
 */
public class DatosObjetoInvalidosException extends RuntimeException {

    public DatosObjetoInvalidosException(String mensaje) {
        super(mensaje);
    }
}
