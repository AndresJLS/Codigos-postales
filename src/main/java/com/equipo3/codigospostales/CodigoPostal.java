package com.equipo3.codigospostales;

/**
 * Representa un resultado obtenido desde la API.
 */
public class CodigoPostal {

    private final String codigo;
    private final String asentamiento;

    public CodigoPostal(String codigo, String asentamiento) {
        this.codigo = codigo;
        this.asentamiento = asentamiento;
    }

    public String codigo() {
        return codigo;
    }

    public String asentamiento() {
        return asentamiento;
    }

    @Override
    public String toString() {
        return asentamiento;
    }
}