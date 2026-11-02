package com.columbia.viajes.views;

import com.columbia.viajes.client.TuristaClient;
import com.columbia.viajes.dto.response.TuristaResponse;
import com.columbia.viajes.security.Roles;
import com.columbia.viajes.views.base.MainLayout;
import com.columbia.viajes.views.base.Notificaciones;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.web.client.RestClientException;

@Route(value = "turistas", layout = MainLayout.class)
@PageTitle("Turistas | Columbia Viajes")
@RolesAllowed({Roles.VENDEDOR, Roles.ADMIN})
public class TuristaGridView extends VerticalLayout implements BeforeEnterObserver {

    private final TuristaClient client;
    private final Grid<TuristaResponse> grid = new Grid<>();

    public TuristaGridView(TuristaClient client) {
        this.client = client;

        grid.addColumn(TuristaResponse::id).setHeader("Código").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(TuristaResponse::nombre).setHeader("Nombre").setSortable(true);
        grid.addColumn(TuristaResponse::apellidos).setHeader("Apellidos").setSortable(true);
        grid.addColumn(TuristaResponse::direccion).setHeader("Dirección").setSortable(true);
        grid.addColumn(TuristaResponse::email).setHeader("Email").setSortable(true);
        grid.addColumn(TuristaResponse::telefono).setHeader("Teléfono").setSortable(true);

        Button nuevo = new Button("Nuevo turista", VaadinIcon.PLUS.create(),
                e -> new TuristaDialog(client, creado -> cargar()).open());
        nuevo.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout encabezado = new HorizontalLayout(new H2("Turistas"), nuevo);
        encabezado.setWidthFull();
        encabezado.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        encabezado.setAlignItems(FlexComponent.Alignment.CENTER);

        add(encabezado, grid);
        setSizeFull();
        grid.setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        cargar();
    }

    private void cargar() {
        try {
            grid.setItems(client.listar());
        } catch (RestClientException e) {
            Notificaciones.error("No se pudieron cargar los turistas", e);
        }
    }
}
