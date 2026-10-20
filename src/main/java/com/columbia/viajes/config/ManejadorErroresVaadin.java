package com.columbia.viajes.config;

import com.columbia.viajes.security.SesionExpiradaException;
import com.columbia.viajes.views.base.Notificaciones;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.DefaultErrorHandler;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.stereotype.Component;

/**
 * Manejo global de errores no capturados en listeners (botones, combos, etc.).
 * Sesión expirada → logout y vuelta al login. Otros → notificación genérica.
 * Los errores durante la navegación los maneja SesionExpiradaView.
 */
@Component
public class ManejadorErroresVaadin implements VaadinServiceInitListener {

    private final AuthenticationContext authContext;

    public ManejadorErroresVaadin(AuthenticationContext authContext) {
        this.authContext = authContext;
    }

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addSessionInitListener(init -> init.getSession().setErrorHandler(error -> {
            if (esSesionExpirada(error.getThrowable())) {
                authContext.logout();
                return;
            }
            new DefaultErrorHandler().error(error);
            if (UI.getCurrent() != null) {
                Notificaciones.error("Ocurrió un error inesperado.");
            }
        }));
    }

    private static boolean esSesionExpirada(Throwable t) {
        for (Throwable actual = t; actual != null; actual = actual.getCause()) {
            if (actual instanceof SesionExpiradaException) {
                return true;
            }
        }
        return false;
    }
}
