package com.columbia.viajes.views;

import com.columbia.viajes.client.ClienteAbm;
import com.columbia.viajes.dto.request.HotelRequest;
import com.columbia.viajes.dto.response.CiudadResponse;
import com.columbia.viajes.dto.response.HotelResponse;
import com.columbia.viajes.views.base.AbmDialog;
import com.columbia.viajes.views.base.Validaciones;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import java.util.ArrayList;
import java.util.List;

/**
 * Alta y edición de un hotel. Las validaciones replican las de HotelRequest del backend.
 * HotelResponse trae el nombre de la ciudad y no su ID: al editar se la busca por nombre
 * (en la BD el nombre de ciudad es único).
 */
public class HotelDialog extends AbmDialog<HotelResponse, HotelRequest> {

    private final TextField nombre = new TextField("Nombre");
    private final TextField direccion = new TextField("Dirección");
    private final ComboBox<CiudadResponse> ciudad = new ComboBox<>("Ciudad");
    private final TextField telefono = new TextField("Teléfono");
    private final IntegerField plazas = new IntegerField("Plazas totales");

    public HotelDialog(ClienteAbm<HotelResponse, HotelRequest> client, List<CiudadResponse> ciudades,
                       HotelResponse hotel, Runnable alGuardar) {
        super(client, hotel, "hotel", alGuardar);

        nombre.setRequired(true);
        nombre.setMinLength(2);
        nombre.setMaxLength(100);
        direccion.setRequired(true);
        direccion.setMinLength(5);
        direccion.setMaxLength(150);
        telefono.setRequired(true);
        telefono.setMinLength(7);
        telefono.setMaxLength(20);
        ciudad.setRequired(true);
        ciudad.setItems(ciudades);
        ciudad.setItemLabelGenerator(CiudadResponse::nombre);
        plazas.setRequiredIndicatorVisible(true);
        plazas.setMin(0);
        plazas.setStepButtonsVisible(true);

        if (esEdicion()) {
            nombre.setValue(getItem().nombre());
            direccion.setValue(getItem().direccion());
            telefono.setValue(getItem().telefono());
            plazas.setValue(getItem().plazasTotales());
            ciudades.stream()
                    .filter(c -> c.nombre().equals(getItem().ciudadNombre()))
                    .findFirst()
                    .ifPresent(ciudad::setValue);
        }

        FormLayout formulario = new FormLayout(nombre, ciudad, direccion, telefono, plazas);
        formulario.setColspan(direccion, 2);
        formulario.setMaxWidth("600px");
        add(formulario);
    }

    @Override
    protected List<String> validar() {
        List<String> errores = new ArrayList<>();
        Validaciones.largo(nombre, errores);
        Validaciones.seleccionado(ciudad, errores);
        Validaciones.largo(direccion, errores);
        Validaciones.largo(telefono, errores);
        Validaciones.entero(plazas, errores);
        return errores;
    }

    @Override
    protected HotelRequest armarRequest() {
        return new HotelRequest(nombre.getValue().trim(), direccion.getValue().trim(),
                ciudad.getValue().id(), telefono.getValue().trim(), plazas.getValue());
    }
}
