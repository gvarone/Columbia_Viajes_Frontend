package com.columbia.viajes.views;

import com.columbia.viajes.client.ClienteAbm;
import com.columbia.viajes.dto.request.SucursalRequest;
import com.columbia.viajes.dto.response.SucursalResponse;
import com.columbia.viajes.views.base.AbmDialog;
import com.columbia.viajes.views.base.Validaciones;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import java.util.ArrayList;
import java.util.List;

/** Alta y edición de una sucursal. Las validaciones replican las de SucursalRequest del backend. */
public class SucursalDialog extends AbmDialog<SucursalResponse, SucursalRequest> {

    private final TextField direccion = new TextField("Dirección");
    private final EmailField email = new EmailField("Email");
    private final TextField telefono = new TextField("Teléfono");

    public SucursalDialog(ClienteAbm<SucursalResponse, SucursalRequest> client,
                          SucursalResponse sucursal, Runnable alGuardar) {
        super(client, sucursal, "sucursal", alGuardar);

        direccion.setRequired(true);
        direccion.setMinLength(5);
        direccion.setMaxLength(150);
        email.setRequired(true);
        email.setMinLength(5);
        email.setMaxLength(120);
        telefono.setRequired(true);
        telefono.setMinLength(7);
        telefono.setMaxLength(20);

        if (esEdicion()) {
            direccion.setValue(getItem().direccion());
            email.setValue(getItem().email());
            telefono.setValue(getItem().telefono());
        }

        FormLayout formulario = new FormLayout(direccion, email, telefono);
        formulario.setColspan(direccion, 2);
        formulario.setMaxWidth("500px");
        add(formulario);
    }

    @Override
    protected List<String> validar() {
        List<String> errores = new ArrayList<>();
        Validaciones.largo(direccion, errores);
        Validaciones.email(email, errores);
        Validaciones.largo(telefono, errores);
        return errores;
    }

    @Override
    protected SucursalRequest armarRequest() {
        return new SucursalRequest(direccion.getValue().trim(), email.getValue().trim(),
                telefono.getValue().trim());
    }
}
