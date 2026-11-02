package com.columbia.viajes.views;

import com.columbia.viajes.client.ClienteAbm;
import com.columbia.viajes.dto.request.VueloRequest;
import com.columbia.viajes.dto.response.CiudadResponse;
import com.columbia.viajes.dto.response.VueloResponse;
import com.columbia.viajes.views.base.AbmDialog;
import com.columbia.viajes.views.base.Formatos;
import com.columbia.viajes.views.base.Validaciones;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Alta y edición de un vuelo. Replica las validaciones de VueloRequest y las reglas
 * de VueloService del backend: origen distinto de destino, salida futura y llegada
 * posterior a la salida. VueloResponse trae los nombres de las ciudades y no sus IDs:
 * al editar se las busca por nombre (en la BD el nombre de ciudad es único).
 */
public class VueloDialog extends AbmDialog<VueloResponse, VueloRequest> {

    private final ComboBox<CiudadResponse> origen = new ComboBox<>("Origen");
    private final ComboBox<CiudadResponse> destino = new ComboBox<>("Destino");
    private final DateTimePicker salida = new DateTimePicker("Salida");
    private final DateTimePicker llegada = new DateTimePicker("Llegada");
    private final IntegerField plazasTurista = new IntegerField("Plazas turista");
    private final IntegerField plazasPrimera = new IntegerField("Plazas primera");

    public VueloDialog(ClienteAbm<VueloResponse, VueloRequest> client, List<CiudadResponse> ciudades,
                       VueloResponse vuelo, Runnable alGuardar) {
        super(client, vuelo, "vuelo", alGuardar);

        for (ComboBox<CiudadResponse> ciudad : List.of(origen, destino)) {
            ciudad.setRequired(true);
            ciudad.setItems(ciudades);
            ciudad.setItemLabelGenerator(CiudadResponse::nombre);
        }
        for (DateTimePicker fecha : List.of(salida, llegada)) {
            fecha.setRequiredIndicatorVisible(true);
            fecha.setLocale(Formatos.LOCALE_AR);
        }
        for (IntegerField plazas : List.of(plazasTurista, plazasPrimera)) {
            plazas.setRequiredIndicatorVisible(true);
            plazas.setMin(0);
            plazas.setStepButtonsVisible(true);
        }

        if (esEdicion()) {
            buscarPorNombre(ciudades, getItem().ciudadOrigenNombre(), origen);
            buscarPorNombre(ciudades, getItem().ciudadDestinoNombre(), destino);
            salida.setValue(getItem().fechaSalida());
            llegada.setValue(getItem().fechaLlegada());
            plazasTurista.setValue(getItem().plazasTuristaTotales());
            plazasPrimera.setValue(getItem().plazasPrimeraTotales());
        }

        FormLayout formulario = new FormLayout(origen, destino, salida, llegada, plazasTurista, plazasPrimera);
        formulario.setColspan(salida, 2);
        formulario.setColspan(llegada, 2);
        formulario.setMaxWidth("600px");
        add(formulario);
    }

    private static void buscarPorNombre(List<CiudadResponse> ciudades, String nombre,
                                        ComboBox<CiudadResponse> campo) {
        ciudades.stream()
                .filter(c -> c.nombre().equals(nombre))
                .findFirst()
                .ifPresent(campo::setValue);
    }

    @Override
    protected List<String> validar() {
        List<String> errores = new ArrayList<>();
        Validaciones.seleccionado(origen, errores);
        Validaciones.seleccionado(destino, errores);
        Validaciones.fechaHora(salida, errores);
        Validaciones.fechaHora(llegada, errores);
        Validaciones.entero(plazasTurista, errores);
        Validaciones.entero(plazasPrimera, errores);
        if (!errores.isEmpty()) {
            return errores;
        }

        // Reglas entre campos (las mismas que VueloService del backend)
        if (origen.getValue().id().equals(destino.getValue().id())) {
            destino.setInvalid(true);
            errores.add("El origen y el destino no pueden ser la misma ciudad.");
        }
        if (salida.getValue().isBefore(LocalDateTime.now())) {
            salida.setInvalid(true);
            errores.add("La salida tiene que ser una fecha futura.");
        }
        if (!llegada.getValue().isAfter(salida.getValue())) {
            llegada.setInvalid(true);
            errores.add("La llegada tiene que ser posterior a la salida.");
        }
        return errores;
    }

    @Override
    protected VueloRequest armarRequest() {
        return new VueloRequest(origen.getValue().id(), destino.getValue().id(),
                salida.getValue(), llegada.getValue(),
                plazasTurista.getValue(), plazasPrimera.getValue());
    }
}
