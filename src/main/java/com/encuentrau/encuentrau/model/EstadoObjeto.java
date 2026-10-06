package com.encuentrau.encuentrau.model;

/**
 * Representa los tres estados posibles de un objeto dentro del sistema.
 *
 * ENCONTRADO → el objeto fue registrado porque alguien lo encontró.
 * RECLAMADO  → existe una solicitud de alguien que afirma ser el propietario.
 * ENTREGADO  → el objeto fue entregado después de validar la reclamación.
 */
public enum EstadoObjeto {
    ENCONTRADO,
    RECLAMADO,
    ENTREGADO
}
