package com.columbia.viajes.views;

import com.columbia.viajes.security.SesionExpiradaException;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.servlet.http.HttpServletResponse;

/** Si el token vence mientras se navega a una vista: cierra sesión y vuelve al login. */
@AnonymousAllowed
public class SesionExpiradaView extends Div implements HasErrorParameter<SesionExpiradaException> {

    private final AuthenticationContext authContext;

    public SesionExpiradaView(AuthenticationContext authContext) {
        this.authContext = authContext;
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<SesionExpiradaException> parameter) {
        authContext.logout();
        return HttpServletResponse.SC_UNAUTHORIZED;
    }
}
