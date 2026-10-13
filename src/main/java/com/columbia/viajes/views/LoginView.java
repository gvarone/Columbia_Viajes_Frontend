package com.columbia.viajes.views;

import com.columbia.viajes.dto.request.LoginRequest;
import com.columbia.viajes.dto.response.LoginResponse;
import com.columbia.viajes.security.SesionUsuario;
import com.columbia.viajes.views.base.ProtectedView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent; 
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Route("login")
@PageTitle("Login | Columbia Viajes")
public class LoginView extends ProtectedView {

    private final RestTemplate restTemplate;

    public LoginView(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H1 titulo = new H1("Columbia Viajes");
        titulo.getStyle().set("color", "var(--lumo-primary-color)");

        TextField usuarioField = new TextField("Usuario");
        usuarioField.setRequiredIndicatorVisible(true);
        usuarioField.setWidth("300px");

        PasswordField passwordField = new PasswordField("Contraseña");
        passwordField.setRequiredIndicatorVisible(true);
        passwordField.setWidth("300px");

        Button loginButton = new Button("Iniciar Sesión", event -> {
            String nombre = usuarioField.getValue();
            String contrasenia = passwordField.getValue();

            if (nombre.isEmpty() || contrasenia.isEmpty()) {
                mostrarError("Por favor, complete todos los campos");
                return;
            }
            try {
                String url = "http://localhost:8080/api/auth/login";
                LoginRequest requestBody = new LoginRequest(nombre, contrasenia);
                ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                        url, 
                        requestBody, 
                        LoginResponse.class
                );

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    LoginResponse loginResponse = response.getBody();
                    SesionUsuario.iniciar(loginResponse);
                    Notification.show("¡Bienvenido, " + loginResponse.nombre() + "!", 3000, Notification.Position.TOP_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                    getUI().ifPresent(ui -> ui.navigate(""));
                }

            } catch (HttpClientErrorException.Unauthorized e) {
                mostrarError("Usuario o contraseña incorrectos");
            } catch (Exception e) {
                mostrarError("Error al conectar con el servidor: " + e.getMessage());
            }
        });

        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.addClickShortcut(Key.ENTER);
        loginButton.setWidth("300px");

        add(titulo, usuarioField, passwordField, loginButton);
    }

    private void mostrarError(String mensaje) {
        Notification.show(mensaje, 4000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}