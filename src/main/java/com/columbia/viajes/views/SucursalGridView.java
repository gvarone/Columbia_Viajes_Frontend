package com.columbia.viajes.views;

import com.columbia.viajes.client.SucursalClient;
import com.columbia.viajes.dto.response.SucursalResponse;
import com.columbia.viajes.security.Roles;
import com.columbia.viajes.views.base.MainLayout;
import com.columbia.viajes.views.base.Notificaciones;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.web.client.RestClientException;

@Route(value = "sucursales", layout = MainLayout.class)
@PageTitle("Sucursales | Columbia Viajes")
@RolesAllowed({Roles.ADMIN, Roles.DUENIO})
public class SucursalGridView extends VerticalLayout implements BeforeEnterObserver {

    private final SucursalClient client;
    private final Grid<SucursalResponse> grid = new Grid<>();

    public SucursalGridView(SucursalClient client) {
        this.client = client;

        grid.addColumn(SucursalResponse::id).setHeader("Código").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(SucursalResponse::direccion).setHeader("Dirección").setSortable(true);
        grid.addColumn(SucursalResponse::email).setHeader("Email").setSortable(true);
        grid.addColumn(SucursalResponse::telefono).setHeader("Teléfono").setSortable(true);

        add(new H2("Sucursales"), grid);
        setSizeFull();
        grid.setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            grid.setItems(client.listar());
        } catch (RestClientException e) {
            Notificaciones.error("No se pudieron cargar las sucursales", e);
        }
    }
}
