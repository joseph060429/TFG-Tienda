package com.proyecto.tienda.backend.ErroresGlobales;

public class ExcepcionesPersonalizadas extends RuntimeException {
    public ExcepcionesPersonalizadas(String mensaje) {
        super(mensaje);
    }
}
