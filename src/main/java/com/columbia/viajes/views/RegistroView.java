package com.columbia.viajes.views;

import com.columbia.viajes.client.AuthClient;
import com.columbia.viajes.views.base.Notificaciones;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;
import java.util.List;
import org.springframework.web.client.RestClientException;

/**
 * Registro público de turistas: crea la cuenta Cliente y su turista
 * (POST /auth/registro). Al terminar vuelve al login.
 */
@Route("registro")
@PageTitle("Crear cuenta | Columbia Viajes")
@AnonymousAllowed
public class RegistroView extends VerticalLayout implements BeforeEnterObserver {

    private final AuthenticationContext authContext;
    private final TuristaForm formulario = new TuristaForm(null);

    public RegistroView(AuthClient authClient, AuthenticationContext authContext) {
        this.authContext = authContext;

        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);

        H1 titulo = new H1("Columbia Viajes");
        titulo.getStyle().set("color", "var(--lumo-primary-color)");

        Button crear = new Button("Crear cuenta", e -> {
            List<String> errores = formulario.validar();
            if (!errores.isEmpty()) {
                Notificaciones.error(String.join(" ", errores));
                return;
            }
            try {
                authClient.registrar(formulario.armarRequest());
                Notificaciones.exito("Cuenta creada. Ya podés iniciar sesión.");
                UI.getCurrent().navigate(LoginView.class);
            } catch (RestClientException ex) {
                Notificaciones.error("No se pudo crear la cuenta", ex);
            }
        });
        crear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        RouterLink irAlLogin = new RouterLink("Iniciar sesión", LoginView.class);
        HorizontalLayout pie = new HorizontalLayout(new Span("¿Ya tenés cuenta?"), irAlLogin, crear);
        pie.setWidthFull();
        pie.setAlignItems(FlexComponent.Alignment.CENTER);
        pie.expand(irAlLogin); // empuja el botón hacia la derecha

        VerticalLayout tarjeta = new VerticalLayout(new H2("Crear cuenta"), formulario, pie);
        tarjeta.setMaxWidth("640px");
        tarjeta.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-s)");

        add(titulo, tarjeta);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authContext.isAuthenticated()) {
            event.forwardTo(HomeView.class);
        }
    }
}
