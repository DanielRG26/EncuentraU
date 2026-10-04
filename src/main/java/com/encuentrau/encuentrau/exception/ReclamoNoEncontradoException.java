package com.encuentrau.encuentrau.exception;

public class ReclamoNoEncontradoException extends RuntimeException {

    public ReclamoNoEncontradoException(Long id) {
        super("No se encontró ningún reclamo con id: " + id);
    }
}
