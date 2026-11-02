package com.columbia.viajes.views;

import com.columbia.viajes.client.CiudadClient;
import com.columbia.viajes.client.HotelClient;
import com.columbia.viajes.dto.request.HotelRequest;
import com.columbia.viajes.dto.response.CiudadResponse;
import com.columbia.viajes.dto.response.HotelResponse;
import com.columbia.viajes.views.base.AbmDialog;
import com.columbia.viajes.views.base.AbmGridView;
import com.columbia.viajes.views.base.MainLayout;
import com.columbia.viajes.views.base.Notificaciones;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import java.util.List;
import org.springframework.web.client.RestClientException;

/** Hoteles: todos los roles consultan; el Admin además hace el ABM. */
@Route(value = "hoteles", layout = MainLayout.class)
@PageTitle("Hoteles | Columbia Viajes")
@PermitAll
public class HotelGridView extends AbmGridView<HotelResponse, HotelRequest> {

    private final CiudadClient ciudadClient;
    private List<CiudadResponse> ciudades = List.of();

    public HotelGridView(HotelClient client, CiudadClient ciudadClient) {
        super(client, "Hoteles", "Nuevo hotel");
        this.ciudadClient = ciudadClient;
    }

    @Override
    protected void configurarColumnas() {
        grid.addColumn(HotelResponse::id).setHeader("Código").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(HotelResponse::nombre).setHeader("Nombre").setSortable(true);
        grid.addColumn(HotelResponse::direccion).setHeader("Dirección").setSortable(true);
        grid.addColumn(HotelResponse::ciudadNombre).setHeader("Ciudad").setSortable(true);
        grid.addColumn(HotelResponse::telefono).setHeader("Teléfono").setSortable(true);
        grid.addColumn(HotelResponse::plazasTotales).setHeader("Plazas").setSortable(true);
    }

    /** Además de los hoteles, el Admin necesita las ciudades para el desplegable del diálogo. */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        super.beforeEnter(event);
        if (puedeEditar()) {
            try {
                ciudades = ciudadClient.listar();
            } catch (RestClientException e) {
                Notificaciones.error("No se pudieron cargar las ciudades", e);
            }
        }
    }

    @Override
    protected AbmDialog<HotelResponse, HotelRequest> crearDialogo(HotelResponse hotel) {
        return new HotelDialog(client, ciudades, hotel, this::cargar);
    }

    @Override
    protected String descripcion(HotelResponse hotel) {
        return "el hotel \"" + hotel.nombre() + "\"";
    }
}
