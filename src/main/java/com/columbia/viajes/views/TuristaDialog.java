package com.columbia.viajes.views;

import com.columbia.viajes.client.TuristaClient;
import com.columbia.viajes.dto.request.TuristaRequest;
import com.columbia.viajes.dto.response.TuristaResponse;
import com.columbia.viajes.views.base.Notificaciones;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.web.client.RestClientException;

/**
 * Alta de turista (sin cuenta de usuario: idUsuario = null).
 * Las validaciones replican las de TuristaRequest del backend.
 */
public class TuristaDialog extends Dialog {

    private final TextField nombre = campo("Nombre", 2, 100);
    private final TextField apellidos = campo("Apellidos", 2, 100);
    private final TextField direccion = campo("Dirección", 5, 150);
    private final TextField telefono = campo("Teléfono", 7, 20);
    private final EmailField email = new EmailField("Email");

    public TuristaDialog(TuristaClient client, Consumer<TuristaResponse> alCrear) {
        setHeaderTitle("Nuevo turista");

        email.setRequired(true);
        email.setMinLength(5);
        email.setMaxLength(120);

        add(new FormLayout(nombre, apellidos, direccion, telefono, email));

        Button guardar = new Button("Guardar", e -> {
            List<String> errores = validar();
            if (!errores.isEmpty()) {
                Notificaciones.error(String.join(" ", errores));
                return;
            }
            try {
                TuristaResponse creado = client.crear(new TuristaRequest(
                        nombre.getValue().trim(), apellidos.getValue().trim(), direccion.getValue().trim(),
                        email.getValue().trim(), telefono.getValue().trim(), null));
                Notificaciones.exito("Turista creado.");
                alCrear.accept(creado);
                close();
            } catch (RestClientException ex) {
                Notificaciones.error("No se pudo crear el turista", ex);
            }
        });
        guardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", e -> close()), guardar);
    }

    private static TextField campo(String etiqueta, int min, int max) {
        TextField campo = new TextField(etiqueta);
        campo.setRequired(true);
        campo.setMinLength(min);
        campo.setMaxLength(max);
        return campo;
    }

    private List<String> validar() {
        List<String> errores = new ArrayList<>();
        for (TextField campo : List.of(nombre, apellidos, direccion, telefono)) {
            int largo = campo.getValue().trim().length();
            if (largo < campo.getMinLength() || largo > campo.getMaxLength()) {
                campo.setInvalid(true);
                errores.add(campo.getLabel() + ": entre " + campo.getMinLength()
                        + " y " + campo.getMaxLength() + " caracteres.");
            }
        }
        String mail = email.getValue().trim();
        if (mail.isEmpty() || email.isInvalid()) {
            email.setInvalid(true);
            errores.add("Email: ingresá un email válido.");
        }
        return errores;
    }
}
