package com.columbia.viajes.views.base;

import com.columbia.viajes.security.SesionUsuario;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

public class ProtectedView extends VerticalLayout implements BeforeEnterObserver {
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!SesionUsuario.estaLogueado()) {
            event.rerouteTo("login");
            return;
        }
        // Hook opcional para que las clases hijas ejecuten su lógica posterior
        onBeforeEnter(event);
    }

    // Método que implementarán las vistas hijas de forma limpia
    protected void onBeforeEnter(BeforeEnterEvent event) {}
}
