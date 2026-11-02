package com.columbia.viajes.dto.request;

/** Edición de usuario (PUT /usuarios/{id}): el backend no permite cambiar contraseña ni turista. */
public record UsuarioActualizacionRequest(
        String nombre,
        Integer idRol,
        Integer idSucursal
) {}
