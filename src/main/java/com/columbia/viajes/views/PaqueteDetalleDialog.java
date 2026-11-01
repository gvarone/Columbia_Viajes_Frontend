package com.columbia.viajes.views;

import com.columbia.viajes.client.VueloClient;
import com.columbia.viajes.dto.response.HotelResponse;
import com.columbia.viajes.dto.response.PaqueteResponse;
import com.columbia.viajes.dto.response.VueloResponse;
import com.columbia.viajes.views.base.Formatos;
import com.columbia.viajes.views.base.Notificaciones;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.web.client.RestClientException;

/** Detalle de un paquete: descripción, hotel y vuelos de ida y vuelta. */
public class PaqueteDetalleDialog extends Dialog {

    /**
     * @param alReservar acción del botón "Reservar"; null para no mostrarlo
     */
    public PaqueteDetalleDialog(PaqueteResponse paquete, HotelResponse hotel,
                                VueloClient vueloClient, Runnable alReservar) {
        setHeaderTitle(paquete.nombre());
        setMaxWidth("640px");

        VerticalLayout contenido = new VerticalLayout();
        contenido.setPadding(false);

        if (paquete.descripcion() != null && !paquete.descripcion().isBlank()) {
            contenido.add(new Paragraph(paquete.descripcion()));
        }

        contenido.add(new H4("Hotel"));
        if (hotel != null) {
            contenido.add(new Paragraph(hotel.nombre() + " · " + hotel.ciudadNombre()),
                    new Paragraph(hotel.direccion() + " · Tel. " + hotel.telefono()));
        }
        contenido.add(new Paragraph("Estadía: " + Formatos.fecha(paquete.fechaLlegadaHotel())
                + " al " + Formatos.fecha(paquete.fechaPartidaHotel())));

        contenido.add(new H4("Vuelos"));
        try {
            contenido.add(new Paragraph("Ida: " + vuelo(vueloClient.obtener(paquete.vueloIdaId()))),
                    new Paragraph("Vuelta: " + vuelo(vueloClient.obtener(paquete.vueloVueltaId()))));
        } catch (RestClientException e) {
            Notificaciones.error("No se pudieron cargar los vuelos", e);
        }

        contenido.add(new H4("Precio"));
        contenido.add(new Paragraph("Desde " + Formatos.precio(paquete.precio())
                + ". Primera clase y pensión completa suman un 10% cada una."));
        add(contenido);

        Button cerrar = new Button("Cerrar", e -> close());
        getFooter().add(cerrar);
        if (alReservar != null) {
            Button reservar = new Button("Reservar", e -> {
                close();
                alReservar.run();
            });
            reservar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            getFooter().add(reservar);
        }
    }

    private static String vuelo(VueloResponse v) {
        return "N° " + v.id() + " · " + v.ciudadOrigenNombre() + " → " + v.ciudadDestinoNombre()
                + " · sale " + Formatos.fechaHora(v.fechaSalida())
                + " · llega " + Formatos.fechaHora(v.fechaLlegada());
    }
}
