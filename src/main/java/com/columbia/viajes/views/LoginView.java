package com.columbia.viajes.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.WebAttributes;

/**
 * El formulario hace POST a /login y lo procesa Spring Security
 * (ver SecurityConfig y BackendAuthenticationProvider).
 */
@Route("login")
@PageTitle("Login | Columbia Viajes")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final AuthenticationContext authContext;
    private final LoginForm login = new LoginForm();
    private final LoginI18n i18n = crearI18n();

    public LoginView(AuthenticationContext authContext) {
        this.authContext = authContext;

        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H1 titulo = new H1("Columbia Viajes");
        titulo.addClassName("titulo-app");

        login.setAction("login");
        login.setForgotPasswordButtonVisible(false);
        login.setI18n(i18n);

        HorizontalLayout registro = new HorizontalLayout(
                new Span("¿No tenés cuenta?"), new RouterLink("Crear cuenta", RegistroView.class));
        registro.setAlignItems(FlexComponent.Alignment.CENTER);

        add(titulo, login, registro);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authContext.isAuthenticated()) {
            event.forwardTo(HomeView.class);
            return;
        }
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            if (!esErrorDeCredenciales()) {
                i18n.getErrorMessage().setTitle("Servidor no disponible");
                i18n.getErrorMessage().setMessage("No se pudo conectar con el servidor. Intentá más tarde.");
                login.setI18n(i18n);
            }
            login.setError(true);
        }
    }

    /** Spring Security guarda en la sesión la última excepción de login. */
    private boolean esErrorDeCredenciales() {
        WrappedSession sesion = VaadinSession.getCurrent().getSession();
        Object excepcion = sesion.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        sesion.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        return excepcion == null || excepcion instanceof BadCredentialsException;
    }

    private static LoginI18n crearI18n() {
        LoginI18n.Form form = new LoginI18n.Form();
        form.setTitle("Iniciar sesión");
        // Los clientes también pueden ingresar con su email
        form.setUsername("Usuario o email");
        form.setPassword("Contraseña");
        form.setSubmit("Ingresar");

        LoginI18n.ErrorMessage error = new LoginI18n.ErrorMessage();
        error.setTitle("Datos incorrectos");
        error.setMessage("Usuario, email o contraseña incorrectos.");
        error.setUsername("Ingresá tu usuario o email.");
        error.setPassword("Ingresá tu contraseña.");

        LoginI18n i18n = new LoginI18n();
        i18n.setForm(form);
        i18n.setErrorMessage(error);
        return i18n;
    }
}
