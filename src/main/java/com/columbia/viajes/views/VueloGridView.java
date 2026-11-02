package com.columbia.viajes.views;

import com.columbia.viajes.client.CiudadClient;
import com.columbia.viajes.client.VueloClient;
import com.columbia.viajes.dto.request.VueloRequest;
import com.columbia.viajes.dto.response.CiudadResponse;
import com.columbia.viajes.dto.response.VueloResponse;
import com.columbia.viajes.views.base.AbmDialog;
import com.columbia.viajes.views.base.AbmGridView;
import com.columbia.viajes.views.base.Formatos;
import com.columbia.viajes.views.base.MainLayout;
import com.columbia.viajes.views.base.Notificaciones;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import java.util.List;
import org.springframework.web.client.RestClientException;

/** Vuelos: todos los roles consultan; el Admin además hace el ABM. */
@Route(value = "vuelos", layout = MainLayout.class)
@PageTitle("Vuelos | Columbia Viajes")
@PermitAll
public class VueloGridView extends AbmGridView<VueloResponse, VueloRequest> {

    private final CiudadClient ciudadClient;
    private List<CiudadResponse> ciudades = List.of();

    public VueloGridView(VueloClient client, CiudadClient ciudadClient) {
        super(client, "Vuelos", "Nuevo vuelo");
        this.ciudadClient = ciudadClient;
    }

    @Override
    protected void configurarColumnas() {
        grid.addColumn(VueloResponse::id).setHeader("N° vuelo").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(VueloResponse::ciudadOrigenNombre).setHeader("Origen").setSortable(true);
        grid.addColumn(VueloResponse::ciudadDestinoNombre).setHeader("Destino").setSortable(true);
        grid.addColumn(v -> Formatos.fechaHora(v.fechaSalida())).setHeader("Salida")
                .setComparator(VueloResponse::fechaSalida);
        grid.addColumn(v -> Formatos.fechaHora(v.fechaLlegada())).setHeader("Llegada")
                .setComparator(VueloResponse::fechaLlegada);
        grid.addColumn(VueloResponse::plazasTuristaTotales).setHeader("Plazas turista").setSortable(true);
        grid.addColumn(VueloResponse::plazasPrimeraTotales).setHeader("Plazas primera").setSortable(true);
    }

    /** Además de los vuelos, el Admin necesita las ciudades para los desplegables del diálogo. */
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
    protected AbmDialog<VueloResponse, VueloRequest> crearDialogo(VueloResponse vuelo) {
        return new VueloDialog(client, ciudades, vuelo, this::cargar);
    }

    @Override
    protected String descripcion(VueloResponse vuelo) {
        return "el vuelo N° " + vuelo.id() + " (" + vuelo.ciudadOrigenNombre() + " → "
                + vuelo.ciudadDestinoNombre() + ", " + Formatos.fechaHora(vuelo.fechaSalida()) + ")";
    }
}
