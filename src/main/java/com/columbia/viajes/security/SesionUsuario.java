package com.columbia.viajes.security;

import com.columbia.viajes.security.enums.RolEnum;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Acceso de solo lectura al usuario autenticado en Spring Security.
 * El inicio y cierre de sesión los maneja Spring Security (ver SecurityConfig).
 */
public final class SesionUsuario {

    private SesionUsuario() {}

    public static Optional<UsuarioAutenticado> getUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UsuarioAutenticado usuario) {
            return Optional.of(usuario);
        }
        return Optional.empty();
    }

    public static String getToken() {
        return getUsuario().map(UsuarioAutenticado::token).orElse(null);
    }

    public static RolEnum getRol() {
        return getUsuario().map(UsuarioAutenticado::rol).orElse(null);
    }

    public static boolean tieneRol(RolEnum... roles) {
        RolEnum rol = getRol();
        return rol != null && Arrays.asList(roles).contains(rol);
    }
}
