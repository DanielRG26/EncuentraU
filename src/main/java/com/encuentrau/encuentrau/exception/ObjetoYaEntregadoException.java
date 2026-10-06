package com.encuentrau.encuentrau.exception;

/**
 * Se lanza cuando se intenta reclamar o modificar como disponible un objeto
 * que ya fue entregado a su propietario.
 * Representa la regla: un objeto en estado ENTREGADO es un caso cerrado.
 */
public class ObjetoYaEntregadoException extends RuntimeException {

    public ObjetoYaEntregadoException(Long id) {
        super("El objeto con id " + id + " ya fue entregado a su propietario.");
    }

    public ObjetoYaEntregadoException(String mensaje) {
        super(mensaje);
    }
}
