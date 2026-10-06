package com.encuentrau.encuentrau.exception;

/**
 * Se lanza cuando se intenta registrar una nueva reclamación sobre un objeto
 * que ya tiene una reclamación válida en curso.
 * Representa la regla: un objeto en estado RECLAMADO no puede reclamarse de nuevo.
 */
public class ObjetoYaReclamadoException extends RuntimeException {

    public ObjetoYaReclamadoException(Long id) {
        super("El objeto con id " + id + " ya tiene una reclamación activa.");
    }

    public ObjetoYaReclamadoException(String mensaje) {
        super(mensaje);
    }
}
