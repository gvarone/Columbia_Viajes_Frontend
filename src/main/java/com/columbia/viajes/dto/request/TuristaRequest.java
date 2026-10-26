package com.columbia.viajes.dto.request;

/** Datos personales del turista. Su cuenta Cliente viaja aparte (ver RegistroClienteRequest). */
public record TuristaRequest(
        String nombre,
        String apellidos,
        String direccion,
        String email,
        String telefono
) {}
