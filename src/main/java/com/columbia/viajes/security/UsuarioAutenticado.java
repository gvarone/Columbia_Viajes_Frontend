package com.columbia.viajes.security;

import com.columbia.viajes.security.enums.RolEnum;
import java.io.Serializable;

/**
 * Principal de Spring Security: usuario logueado y su JWT del backend.
 * Se guarda en la sesión HTTP, por eso es Serializable.
 */
public record UsuarioAutenticado(
        String nombre,
        RolEnum rol,
        String token
) implements Serializable {

    @Override
    public String toString() {
        // Evita que el token termine en logs
        return "UsuarioAutenticado[nombre=" + nombre + ", rol=" + rol + "]";
    }
}
