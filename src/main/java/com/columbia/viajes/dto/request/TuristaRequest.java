package com.columbia.viajes.dto.request;

/** idUsuario es opcional: un turista puede no tener cuenta en el sistema. */
public record TuristaRequest(
        String nombre,
        String apellidos,
        String direccion,
        String email,
        String telefono,
        Integer idUsuario
) {}
