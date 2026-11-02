package com.columbia.viajes.views;

import com.columbia.viajes.client.AuthClient;
import com.columbia.viajes.dto.request.CambioContraseniaRequest;
import com.columbia.viajes.views.base.Notificaciones;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.web.client.RestClientException;

/**
 * Cambio de contraseña del usuario logueado (POST /auth/cambiar-contrasenia).
 * El backend invalida el token actual, por eso al terminar se cierra la sesión.
 */
@SpringComponent
@UIScope
public class CambiarContraseniaDialog extends Dialog {

    private final AuthClient authClient;
    private final AuthenticationContext authContext;

    private final PasswordField actual = new PasswordField("Contraseña actual");
    private final PasswordField nueva = new PasswordField("Contraseña nueva");
    private final PasswordField repetir = new PasswordField("Repetir contraseña nueva");

    public CambiarContraseniaDialog(AuthClient authClient, AuthenticationContext authContext) {
        this.authClient = authClient;
        this.authContext = authContext;

        setHeaderTitle("Cambiar contraseña");
        nueva.setMinLength(8);
        nueva.setMaxLength(64);
        nueva.setHelperText("Entre 8 y 64 caracteres. Después vas a tener que volver a iniciar sesión.");

        VerticalLayout campos = new VerticalLayout(actual, nueva, repetir);
        campos.setPadding(false);
        campos.getChildren().forEach(c -> ((PasswordField) c).setWidthFull());
        add(campos);

        Button guardar = new Button("Guardar", e -> guardar());
        guardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", e -> close()), guardar);

        addOpenedChangeListener(e -> limpiar());
    }

    private void guardar() {
        if (!esValido()) {
            return;
        }
        try {
            authClient.cambiarContrasenia(new CambioContraseniaRequest(actual.getValue(), nueva.getValue()));
            close();
            avisarYCerrarSesion();
        } catch (RestClientException e) {
            Notificaciones.error("No se pudo cambiar la contraseña", e);
        }
    }

    private boolean esValido() {
        String clave = nueva.getValue();
        if (actual.getValue().isEmpty()) {
            actual.setInvalid(true);
            Notificaciones.error("Ingresá tu contraseña actual.");
            return false;
        }
        if (clave.isBlank() || clave.length() < 8 || clave.length() > 64) {
            nueva.setInvalid(true);
            Notificaciones.error("La contraseña nueva debe tener entre 8 y 64 caracteres.");
            return false;
        }
        if (!clave.equals(repetir.getValue())) {
            repetir.setInvalid(true);
            Notificaciones.error("Las contraseñas nuevas no coinciden.");
            return false;
        }
        return true;
    }

    /** El token ya no sirve: se avisa y se cierra la sesión para volver a entrar con la clave nueva. */
    private void avisarYCerrarSesion() {
        ConfirmDialog aviso = new ConfirmDialog("Contraseña cambiada",
                "Tu contraseña se cambió correctamente. Volvé a iniciar sesión con la contraseña nueva.",
                "Ir al login", e -> authContext.logout());
        aviso.setCloseOnEsc(false);
        aviso.open();
    }

    private void limpiar() {
        actual.clear();
        nueva.clear();
        repetir.clear();
        actual.setInvalid(false);
        nueva.setInvalid(false);
        repetir.setInvalid(false);
    }
}
