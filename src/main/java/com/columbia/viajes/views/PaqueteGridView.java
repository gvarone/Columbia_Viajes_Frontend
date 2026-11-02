package com.columbia.viajes.views;

import com.columbia.viajes.client.HotelClient;
import com.columbia.viajes.client.PaqueteClient;
import com.columbia.viajes.client.SucursalClient;
import com.columbia.viajes.client.VueloClient;
import com.columbia.viajes.dto.request.PaqueteRequest;
import com.columbia.viajes.dto.response.HotelResponse;
import com.columbia.viajes.dto.response.PaqueteResponse;
import com.columbia.viajes.dto.response.SucursalResponse;
import com.columbia.viajes.dto.response.VueloResponse;
import com.columbia.viajes.security.Roles;
import com.columbia.viajes.views.base.AbmDialog;
import com.columbia.viajes.views.base.AbmGridView;
import com.columbia.viajes.views.base.Formatos;
import com.columbia.viajes.views.base.MainLayout;
import com.columbia.viajes.views.base.Notificaciones;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import java.util.List;
import org.springframework.web.client.RestClientException;

/**
 * Gestión de paquetes en grilla (el catálogo con tarjetas es PaqueteCatalogoView).
 * El Admin hace el ABM; el Dueño solo consulta.
 */
@Route(value = "paquetes/gestion", layout = MainLayout.class)
@PageTitle("Gestión de paquetes | Columbia Viajes")
@RolesAllowed({Roles.ADMIN, Roles.DUENIO})
public class PaqueteGridView extends AbmGridView<PaqueteResponse, PaqueteRequest> {

    private final PaqueteClient paqueteClient;
    private final HotelClient hotelClient;
    private final SucursalClient sucursalClient;
    private final VueloClient vueloClient;

    // Datos para mostrar nombres en la grilla y para los desplegables del diálogo
    private List<HotelResponse> hoteles = List.of();
    private List<SucursalResponse> sucursales = List.of();
    private List<VueloResponse> vuelos = List.of();

    public PaqueteGridView(PaqueteClient client, HotelClient hotelClient,
                           SucursalClient sucursalClient, VueloClient vueloClient) {
        super(client, "Paquetes", "Nuevo paquete");
        this.paqueteClient = client;
        this.hotelClient = hotelClient;
        this.sucursalClient = sucursalClient;
        this.vueloClient = vueloClient;
    }

    @Override
    protected void configurarColumnas() {
        grid.addColumn(PaqueteResponse::id).setHeader("Código").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(PaqueteResponse::nombre).setHeader("Nombre").setSortable(true);
        grid.addColumn(this::hotel).setHeader("Hotel").setSortable(true);
        grid.addColumn(p -> Formatos.fecha(p.fechaLlegadaHotel()) + " al " + Formatos.fecha(p.fechaPartidaHotel()))
                .setHeader("Estadía").setComparator(PaqueteResponse::fechaLlegadaHotel);
        grid.addColumn(p -> Formatos.precio(p.precio())).setHeader("Precio")
                .setComparator(PaqueteResponse::precio).setTextAlign(ColumnTextAlign.END);
        grid.addColumn(this::sucursal).setHeader("Sucursal").setSortable(true);
        grid.addColumn(p -> p.imagen() != null ? "Sí" : "No").setHeader("Imagen")
                .setAutoWidth(true).setFlexGrow(0).setSortable(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            hoteles = hotelClient.listar();
            sucursales = sucursalClient.listar();
            if (puedeEditar()) {
                vuelos = vueloClient.listar();
            }
        } catch (RestClientException e) {
            Notificaciones.error("No se pudieron cargar todos los datos", e);
        }
        super.beforeEnter(event);
    }

    @Override
    protected AbmDialog<PaqueteResponse, PaqueteRequest> crearDialogo(PaqueteResponse paquete) {
        return new PaqueteDialog(paqueteClient, sucursales, hoteles, vuelos, paquete, this::cargar);
    }

    @Override
    protected String descripcion(PaqueteResponse paquete) {
        return "el paquete \"" + paquete.nombre() + "\"";
    }

    private String hotel(PaqueteResponse paquete) {
        return hoteles.stream()
                .filter(h -> h.id().equals(paquete.hotelId()))
                .map(HotelResponse::nombre)
                .findFirst()
                .orElse("#" + paquete.hotelId());
    }

    private String sucursal(PaqueteResponse paquete) {
        return sucursales.stream()
                .filter(s -> s.id().equals(paquete.sucursalId()))
                .map(SucursalResponse::direccion)
                .findFirst()
                .orElse("#" + paquete.sucursalId());
    }
}
