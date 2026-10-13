package com.columbia.viajes.security;

import com.columbia.viajes.security.enums.RolEnum;
import com.columbia.viajes.dto.response.LoginResponse;
import com.vaadin.flow.server.VaadinSession;

public final class SesionUsuario {

    private static final String USUARIO = "usuario";

    private SesionUsuario() {}

    public static void iniciar(LoginResponse usuario) {
        VaadinSession.getCurrent().setAttribute(USUARIO, usuario);
    }

    public static LoginResponse getUsuario() {
        VaadinSession session = VaadinSession.getCurrent();
        return session == null ? null : (LoginResponse) session.getAttribute(USUARIO);
    }
    
    public static String getToken() {
        LoginResponse usuario = getUsuario();
        return (usuario != null) ? usuario.token() : null;
    }

    public static boolean estaLogueado() {
        return getUsuario() != null;
    }

    public static void cerrarSesion() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute(USUARIO, null);
        }
    }
    
    public static RolEnum getRol() {
        LoginResponse usuario = getUsuario();
        if (usuario == null || usuario.rolNombre() == null) {
            return null;
        }
        return RolEnum.desdeDb(usuario.rolNombre());
    }
    
    public static boolean esAdmin() {
        LoginResponse usuario = getUsuario();
        return usuario != null && "Administrador".equals(usuario.rolNombre());
    }

    public static boolean esVendedor() {
        LoginResponse usuario = getUsuario();
        return usuario != null && "Vendedor".equals(usuario.rolNombre());    
    }

    public static boolean esDuenio() {
        LoginResponse usuario = getUsuario();
        return usuario != null && "Dueño".equals(usuario.rolNombre());
    }

    public static boolean esCliente() {
        LoginResponse usuario = getUsuario();
        return usuario != null && "Cliente".equals(usuario.rolNombre());
    }
}