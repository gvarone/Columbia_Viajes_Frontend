package com.columbia.viajes.views;

import com.columbia.viajes.client.HotelClient;
import com.columbia.viajes.dto.response.HotelResponse;
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

@Route(value = "hoteles", layout = MainLayout.class)
@PageTitle("Hoteles | Columbia Viajes")
@PermitAll
public class HotelGridView extends VerticalLayout implements BeforeEnterObserver {

    private final HotelClient client;
    private final Grid<HotelResponse> grid = new Grid<>();

    public HotelGridView(HotelClient client) {
        this.client = client;

        grid.addColumn(HotelResponse::id).setHeader("Código").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(HotelResponse::nombre).setHeader("Nombre").setSortable(true);
        grid.addColumn(HotelResponse::direccion).setHeader("Dirección").setSortable(true);
        grid.addColumn(HotelResponse::ciudadNombre).setHeader("Ciudad").setSortable(true);
        grid.addColumn(HotelResponse::telefono).setHeader("Teléfono").setSortable(true);
        grid.addColumn(HotelResponse::plazasTotales).setHeader("Plazas").setSortable(true);

        add(new H2("Hoteles"), grid);
        setSizeFull();
        grid.setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            grid.setItems(client.listar());
        } catch (RestClientException e) {
            Notificaciones.error("No se pudieron cargar los hoteles", e);
        }
    }
}
