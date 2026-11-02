package com.columbia.viajes.views;

import com.columbia.viajes.client.ClienteAbm;
import com.columbia.viajes.dto.request.CiudadRequest;
import com.columbia.viajes.dto.response.CiudadResponse;
import com.columbia.viajes.views.base.AbmDialog;
import com.columbia.viajes.views.base.Validaciones;
import com.vaadin.flow.component.textfield.TextField;
import java.util.ArrayList;
import java.util.List;

/** Alta y edición de una ciudad. Las validaciones replican las de CiudadRequest del backend. */
public class CiudadDialog extends AbmDialog<CiudadResponse, CiudadRequest> {

    private final TextField nombre = new TextField("Nombre");

    public CiudadDialog(ClienteAbm<CiudadResponse, CiudadRequest> client, CiudadResponse ciudad, Runnable alGuardar) {
        super(client, ciudad, "ciudad", alGuardar);

        nombre.setRequired(true);
        nombre.setMinLength(2);
        nombre.setMaxLength(100);
        nombre.setWidth("320px");
        if (esEdicion()) {
            nombre.setValue(getItem().nombre());
        }
        add(nombre);
    }

    @Override
    protected List<String> validar() {
        List<String> errores = new ArrayList<>();
        Validaciones.largo(nombre, errores);
        return errores;
    }

    @Override
    protected CiudadRequest armarRequest() {
        return new CiudadRequest(nombre.getValue().trim());
    }
}
