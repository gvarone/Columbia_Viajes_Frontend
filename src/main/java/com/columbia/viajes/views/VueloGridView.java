package com.columbia.viajes.views;

import com.columbia.viajes.client.VueloClient;
import com.columbia.viajes.dto.response.VueloResponse;
import com.columbia.viajes.views.base.Formatos;
import com.columbia.viajes.views.base.MainLayout;
import com.columbia.viajes.views.base.Notificaciones;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.web.client.RestClientException;

@Route(value = "vuelos", layout = MainLayout.class)
@PageTitle("Vuelos | Columbia Viajes")
@PermitAll
public class VueloGridView extends VerticalLayout implements BeforeEnterObserver {

    private final VueloClient client;
    private final Grid<VueloResponse> grid = new Grid<>();

    public VueloGridView(VueloClient client) {
        this.client = client;

        grid.addColumn(VueloResponse::id).setHeader("N° vuelo").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(VueloResponse::ciudadOrigenNombre).setHeader("Origen").setSortable(true);
        grid.addColumn(VueloResponse::ciudadDestinoNombre).setHeader("Destino").setSortable(true);
        grid.addColumn(v -> Formatos.fechaHora(v.fechaSalida())).setHeader("Salida")
                .setComparator(VueloResponse::fechaSalida);
        grid.addColumn(v -> Formatos.fechaHora(v.fechaLlegada())).setHeader("Llegada")
                .setComparator(VueloResponse::fechaLlegada);
        grid.addColumn(VueloResponse::plazasTuristaTotales).setHeader("Plazas turista").setSortable(true);
        grid.addColumn(VueloResponse::plazasPrimeraTotales).setHeader("Plazas primera").setSortable(true);

        add(new H2("Vuelos"), grid);
        setSizeFull();
        grid.setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            grid.setItems(client.listar());
        } catch (RestClientException e) {
            Notificaciones.error("No se pudieron cargar los vuelos", e);
        }
    }
}
