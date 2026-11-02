package com.columbia.viajes.views;

import com.columbia.viajes.client.ReservaClient;
import com.columbia.viajes.dto.enums.ClaseVuelo;
import com.columbia.viajes.dto.enums.RegimenHospedaje;
import com.columbia.viajes.dto.request.ReservaRequest;
import com.columbia.viajes.dto.response.ReservaResponse;
import com.columbia.viajes.views.base.Formatos;
import com.columbia.viajes.views.base.Notificaciones;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import java.util.function.Consumer;
import org.springframework.web.client.RestClientException;

/**
 * Edición de una reserva: solo clase de vuelo y régimen de hospedaje.
 * Para cambiar turista o paquete se cancela la reserva y se crea otra.
 * El backend recalcula el precio.
 */
public class EditarReservaDialog extends Dialog {

    public EditarReservaDialog(ReservaClient client, ReservaResponse reserva, String turista,
                               String paquete, Consumer<ReservaResponse> alGuardar) {
        setHeaderTitle("Editar reserva N° " + reserva.id());

        RadioButtonGroup<ClaseVuelo> clase = new RadioButtonGroup<>("Clase de vuelo");
        clase.setItems(ClaseVuelo.values());
        clase.setItemLabelGenerator(ClaseVuelo::getEtiqueta);
        clase.setValue(reserva.claseVuelo());

        RadioButtonGroup<RegimenHospedaje> regimen = new RadioButtonGroup<>("Régimen de hospedaje");
        regimen.setItems(RegimenHospedaje.values());
        regimen.setItemLabelGenerator(RegimenHospedaje::getEtiqueta);
        regimen.setValue(reserva.regimenHospedaje());

        VerticalLayout contenido = new VerticalLayout(
                new Paragraph("Turista: " + turista),
                new Paragraph("Paquete: " + paquete),
                new Paragraph("Precio actual: " + Formatos.precio(reserva.precioReserva())),
                clase, regimen);
        contenido.setPadding(false);
        add(contenido);

        Button guardar = new Button("Guardar", e -> {
            if (clase.isEmpty() || regimen.isEmpty()) {
                Notificaciones.error("Elegí clase y régimen.");
                return;
            }
            // Se reenvían paquete, turista y vendedor sin cambios: el backend exige el vendedor
            // cuando edita un Admin, y para un Vendedor lo ignora (mantiene el de la reserva).
            var request = new ReservaRequest(reserva.paqueteId(), reserva.turistaId(),
                    clase.getValue(), regimen.getValue(), reserva.vendedorId());
            try {
                ReservaResponse actualizada = client.actualizar(reserva.id(), request);
                Notificaciones.exito("Reserva actualizada. Nuevo precio: "
                        + Formatos.precio(actualizada.precioReserva()));
                alGuardar.accept(actualizada);
                close();
            } catch (RestClientException ex) {
                Notificaciones.error("No se pudo actualizar la reserva", ex);
            }
        });
        guardar.addThemeVariants(ButtonVariant.PRIMARY);
        getFooter().add(new Button("Cancelar", e -> close()), guardar);
    }
}
