package com.columbia.viajes.security;

/**
 * El backend rechazó el token (vencido o invalidado). Se maneja de forma
 * centralizada cerrando la sesión del front y volviendo al login.
 */
public class SesionExpiradaException extends RuntimeException {

    public SesionExpiradaException() {
        super("La sesión expiró. Volvé a iniciar sesión.");
    }
}
