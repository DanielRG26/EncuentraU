package com.encuentrau.encuentrau.exception;

/**
 * Se lanza cuando se intenta consultar o modificar un objeto que no existe en el sistema.
 * Representa una regla del dominio: no se puede operar sobre algo que no está registrado.
 */
public class ObjetoNoEncontradoException extends RuntimeException {

    public ObjetoNoEncontradoException(Long id) {
        super("No se encontró ningún objeto con el id: " + id);
    }

    public ObjetoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
