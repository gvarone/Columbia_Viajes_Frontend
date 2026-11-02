package com.columbia.viajes.views;

import com.columbia.viajes.client.CiudadClient;
import com.columbia.viajes.client.HotelClient;
import com.columbia.viajes.client.PaqueteClient;
import com.columbia.viajes.client.UsuarioClient;
import com.columbia.viajes.client.VueloClient;
import com.columbia.viajes.dto.response.CiudadResponse;
import com.columbia.viajes.dto.response.HotelResponse;
import com.columbia.viajes.dto.response.PaqueteResponse;
import com.columbia.viajes.security.SesionUsuario;
import com.columbia.viajes.views.base.Formatos;
import com.columbia.viajes.views.base.MainLayout;
import com.columbia.viajes.views.base.Notificaciones;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.card.CardVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import jakarta.annotation.security.PermitAll;
import java.io.ByteArrayInputStream;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.web.client.RestClientException;

import static com.columbia.viajes.security.enums.RolEnum.VENDEDOR;

/**
 * Catálogo de paquetes para todos los roles, con filtros que aplica el backend.
 * El Vendedor solo ve los paquetes de su sucursal (son los únicos que puede reservar)
 * y tiene el botón "Reservar". Las imágenes pasan por el servidor Vaadin.
 */
@Route(value = "paquetes", layout = MainLayout.class)
@PageTitle("Paquetes | Columbia Viajes")
@PermitAll
public class PaqueteCatalogoView extends VerticalLayout implements BeforeEnterObserver {

    private final PaqueteClient paqueteClient;
    private final HotelClient hotelClient;
    private final CiudadClient ciudadClient;
    private final VueloClient vueloClient;
    private final UsuarioClient usuarioClient;

    private final TextField nombre = new TextField();
    private final ComboBox<CiudadResponse> ciudad = new ComboBox<>();
    private final ComboBox<HotelResponse> hotel = new ComboBox<>();
    private final Div tarjetas = new Div();

    private final boolean esVendedor = SesionUsuario.tieneRol(VENDEDOR);
    private Integer miSucursal;
    private Map<Integer, HotelResponse> hoteles = Map.of();

    public PaqueteCatalogoView(PaqueteClient paqueteClient, HotelClient hotelClient,
                               CiudadClient ciudadClient, VueloClient vueloClient,
                               UsuarioClient usuarioClient) {
        this.paqueteClient = paqueteClient;
        this.hotelClient = hotelClient;
        this.ciudadClient = ciudadClient;
        this.vueloClient = vueloClient;
        this.usuarioClient = usuarioClient;
        setPadding(true);

        nombre.setPlaceholder("Buscar por nombre");
        nombre.setPrefixComponent(VaadinIcon.SEARCH.create());
        nombre.setClearButtonVisible(true);
        nombre.setValueChangeMode(ValueChangeMode.LAZY);
        nombre.addValueChangeListener(e -> buscar());

        ciudad.setPlaceholder("Ciudad");
        ciudad.setItemLabelGenerator(CiudadResponse::nombre);
        ciudad.setClearButtonVisible(true);
        ciudad.addValueChangeListener(e -> buscar());

        hotel.setPlaceholder("Hotel");
        hotel.setItemLabelGenerator(HotelResponse::nombre);
        hotel.setClearButtonVisible(true);
        hotel.addValueChangeListener(e -> buscar());

        HorizontalLayout filtros = new HorizontalLayout(nombre, ciudad, hotel);
        filtros.setWrap(true);

        tarjetas.addClassName("catalogo-tarjetas");

        add(new H2(esVendedor ? "Paquetes de mi sucursal" : "Paquetes"), filtros, tarjetas);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            List<HotelResponse> listaHoteles = hotelClient.listar();
            hoteles = listaHoteles.stream().collect(Collectors.toMap(HotelResponse::id, Function.identity()));
            hotel.setItems(listaHoteles);
            ciudad.setItems(ciudadClient.listar());
            if (esVendedor) {
                miSucursal = usuarioClient.miPerfil().usuario().sucursalId();
            }
        } catch (RestClientException e) {
            Notificaciones.error("No se pudieron cargar los filtros", e);
        }
        buscar();
    }

    private void buscar() {
        tarjetas.removeAll();
        if (esVendedor && miSucursal == null) {
            tarjetas.add(new Paragraph("No se pudo determinar tu sucursal."));
            return;
        }
        try {
            List<PaqueteResponse> paquetes = paqueteClient.listar(nombre.getValue(),
                    ciudad.isEmpty() ? null : ciudad.getValue().id(),
                    hotel.isEmpty() ? null : hotel.getValue().id());
            if (esVendedor) {
                paquetes = paquetes.stream().filter(p -> miSucursal.equals(p.sucursalId())).toList();
            }
            if (paquetes.isEmpty()) {
                tarjetas.add(new Paragraph("No hay paquetes que coincidan con la búsqueda."));
            }
            paquetes.forEach(p -> tarjetas.add(tarjeta(p)));
        } catch (RestClientException e) {
            Notificaciones.error("No se pudieron cargar los paquetes", e);
        }
    }

    private Card tarjeta(PaqueteResponse paquete) {
        HotelResponse h = hoteles.get(paquete.hotelId());

        Card card = new Card();
        card.addThemeVariants(CardVariant.OUTLINED, CardVariant.STRETCH_MEDIA);
        card.setMedia(imagen(paquete));
        card.setTitle(paquete.nombre());

        long noches = ChronoUnit.DAYS.between(paquete.fechaLlegadaHotel(), paquete.fechaPartidaHotel());
        Span desde = new Span("Desde");
        desde.addClassName("catalogo-precio-desde");
        Span monto = new Span(Formatos.precioSinCentavos(paquete.precio()));
        monto.addClassName("catalogo-precio-monto");
        Div precio = new Div(desde, monto);
        precio.addClassName("catalogo-precio");

        card.add(dato(VaadinIcon.BUILDING, h != null ? h.nombre() + " · " + h.ciudadNombre() : ""),
                dato(VaadinIcon.CALENDAR, Formatos.fecha(paquete.fechaLlegadaHotel()) + " al "
                        + Formatos.fecha(paquete.fechaPartidaHotel())
                        + " · " + noches + (noches == 1 ? " noche" : " noches")),
                precio);

        Button detalle = new Button("Ver detalle", e -> abrirDetalle(paquete));
        card.addToFooter(detalle);
        if (esVendedor) {
            Button reservar = new Button("Reservar", e -> reservar(paquete));
            reservar.addThemeVariants(ButtonVariant.PRIMARY);
            card.addToFooter(reservar);
        }
        return card;
    }

    /** Línea con ícono y texto secundario (hotel, fechas). */
    private static Div dato(VaadinIcon icono, String texto) {
        Icon i = icono.create();
        i.setSize("16px");
        Div fila = new Div(i, new Span(texto));
        fila.addClassName("catalogo-dato");
        return fila;
    }

    /**
     * La imagen la pide el servidor Vaadin al backend y se la entrega al navegador:
     * el navegador nunca llama directo al backend.
     */
    private Component imagen(PaqueteResponse paquete) {
        if (paquete.imagen() == null) {
            Icon icono = VaadinIcon.PICTURE.create();
            icono.setSize("48px");
            Div sinImagen = new Div(icono);
            sinImagen.addClassName("catalogo-imagen");
            return sinImagen;
        }
        Integer id = paquete.id();
        DownloadHandler descarga = DownloadHandler.fromInputStream(evento -> {
            try {
                byte[] bytes = paqueteClient.imagen(id);
                return new DownloadResponse(new ByteArrayInputStream(bytes),
                        "paquete-" + id + ".png", "image/png", bytes.length);
            } catch (RuntimeException e) {
                // Imagen inexistente o backend caído: el navegador muestra el texto alternativo
                return DownloadResponse.error(404);
            }
        });
        Image img = new Image(descarga, paquete.nombre());
        img.addClassName("catalogo-imagen");
        return img;
    }

    private void abrirDetalle(PaqueteResponse paquete) {
        new PaqueteDetalleDialog(paquete, hoteles.get(paquete.hotelId()), vueloClient,
                esVendedor ? () -> reservar(paquete) : null).open();
    }

    private void reservar(PaqueteResponse paquete) {
        UI.getCurrent().navigate(NuevaReservaView.class,
                QueryParameters.of(NuevaReservaView.PARAMETRO_PAQUETE, String.valueOf(paquete.id())));
    }
}
