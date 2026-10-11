package com.columbia.viajes.security;

import com.columbia.viajes.dto.LoginResponse;
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

    public static boolean estaLogueado() {
        return getUsuario() != null;
    }

    public static void cerrarSesion() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute(USUARIO, null);
        }
    }
}