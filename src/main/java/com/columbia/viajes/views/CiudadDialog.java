package com.columbia.viajes.views;

import com.columbia.viajes.client.CiudadClient;
import com.columbia.viajes.dto.request.CiudadRequest;
import com.columbia.viajes.dto.response.CiudadResponse;
import com.columbia.viajes.views.base.Notificaciones;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.web.client.RestClientException;

/**
 * Alta y edición de una ciudad. Si se recibe una ciudad es edición; si es null, alta.
 * Las validaciones replican las de CiudadRequest del backend.
 */
public class CiudadDialog extends Dialog {

    private final CiudadClient client;
    private final CiudadResponse ciudad;
    private final Runnable alGuardar;

    private final TextField nombre = new TextField("Nombre");

    public CiudadDialog(CiudadClient client, CiudadResponse ciudad, Runnable alGuardar) {
        this.client = client;
        this.ciudad = ciudad;
        this.alGuardar = alGuardar;

        setHeaderTitle(esEdicion() ? "Editar ciudad" : "Nueva ciudad");

        nombre.setRequired(true);
        nombre.setMinLength(2);
        nombre.setMaxLength(100);
        nombre.setWidth("320px");
        if (esEdicion()) {
            nombre.setValue(ciudad.nombre());
        }
        add(nombre);

        Button guardar = new Button("Guardar", e -> guardar());
        guardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", e -> close()), guardar);
    }

    private boolean esEdicion() {
        return ciudad != null;
    }

    private void guardar() {
        String valor = nombre.getValue().trim();
        if (valor.length() < 2 || valor.length() > 100) {
            nombre.setInvalid(true);
            Notificaciones.error("El nombre debe tener entre 2 y 100 caracteres.");
            return;
        }
        try {
            CiudadRequest request = new CiudadRequest(valor);
            if (esEdicion()) {
                client.actualizar(ciudad.id(), request);
                Notificaciones.exito("Ciudad actualizada.");
            } else {
                client.crear(request);
                Notificaciones.exito("Ciudad creada.");
            }
            alGuardar.run();
            close();
        } catch (RestClientException e) {
            Notificaciones.error("No se pudo guardar la ciudad", e);
        }
    }
}
