package com.encuentrau.encuentrau.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

/**
 * Entidad que representa un objeto encontrado dentro de la institución.
 * Aplica encapsulamiento: todos los atributos son privados y se acceden
 * mediante getters y setters.
 */
@Entity
@Table(name = "objetos")
public class Objeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    private String lugarEncontrado;

    @Column(nullable = false)
    private LocalDate fechaEncontrado;

    /**
     * Estado actual del objeto en el proceso de recuperación.
     * Se almacena como String en la base de datos para mayor legibilidad.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoObjeto estado;

    // Constructor vacío requerido por JPA
    public Objeto() {}

    // Constructor con todos los campos (sin id, lo genera la BD)
    public Objeto(String nombre, String descripcion, String categoria,
                  String lugarEncontrado, LocalDate fechaEncontrado, EstadoObjeto estado) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.lugarEncontrado = lugarEncontrado;
        this.fechaEncontrado = fechaEncontrado;
        this.estado = estado;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getLugarEncontrado() {
        return lugarEncontrado;
    }

    public void setLugarEncontrado(String lugarEncontrado) {
        this.lugarEncontrado = lugarEncontrado;
    }

    public LocalDate getFechaEncontrado() {
        return fechaEncontrado;
    }

    public void setFechaEncontrado(LocalDate fechaEncontrado) {
        this.fechaEncontrado = fechaEncontrado;
    }

    public EstadoObjeto getEstado() {
        return estado;
    }

    public void setEstado(EstadoObjeto estado) {
        this.estado = estado;
    }
}
