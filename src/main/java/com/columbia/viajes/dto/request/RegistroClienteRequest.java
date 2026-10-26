package com.columbia.viajes.dto.request;

/**
 * Equivale a RegistroClienteRequest del backend: crea la cuenta (rol Cliente,
 * asignado por el servidor) y su turista en una sola operación.
 */
public record RegistroClienteRequest(
        String nombreUsuario,
        String contrasenia,
        TuristaRequest turista
) {}
