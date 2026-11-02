package com.columbia.viajes.views;

import com.columbia.viajes.client.CiudadClient;
import com.columbia.viajes.dto.response.CiudadResponse;
import com.columbia.viajes.security.Roles;
import com.columbia.viajes.security.SesionUsuario;
import com.columbia.viajes.views.base.MainLayout;
import com.columbia.viajes.views.base.Notificaciones;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
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

import static com.columbia.viajes.security.enums.RolEnum.ADMIN;

/**
 * ABM de ciudades. El Admin puede crear, editar y eliminar; el Dueño solo consulta.
 */
@Route(value = "ciudades", layout = MainLayout.class)
@PageTitle("Ciudades | Columbia Viajes")
@RolesAllowed({Roles.ADMIN, Roles.DUENIO})
public class CiudadGridView extends VerticalLayout implements BeforeEnterObserver {

    private final CiudadClient client;
    private final Grid<CiudadResponse> grid = new Grid<>();

    public CiudadGridView(CiudadClient client) {
        this.client = client;
        boolean puedeEditar = SesionUsuario.tieneRol(ADMIN);

        grid.addColumn(CiudadResponse::id).setHeader("Código").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(CiudadResponse::nombre).setHeader("Nombre");
        if (puedeEditar) {
            grid.addComponentColumn(this::crearAcciones).setHeader("Acciones").setAutoWidth(true).setFlexGrow(0);
        }

        Button nueva = new Button("Nueva ciudad", VaadinIcon.PLUS.create(),
                e -> new CiudadDialog(client, null, this::cargar).open());
        nueva.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        nueva.setVisible(puedeEditar);

        HorizontalLayout encabezado = new HorizontalLayout(new H2("Ciudades"), nueva);
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
            Notificaciones.error("No se pudieron cargar las ciudades", e);
        }
    }

    private HorizontalLayout crearAcciones(CiudadResponse ciudad) {
        Button editar = new Button(VaadinIcon.EDIT.create(),
                e -> new CiudadDialog(client, ciudad, this::cargar).open());
        editar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        editar.setTooltipText("Editar");

        Button eliminar = new Button(VaadinIcon.TRASH.create(), e -> confirmarEliminar(ciudad));
        eliminar.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
        eliminar.setTooltipText("Eliminar");

        return new HorizontalLayout(editar, eliminar);
    }

    private void confirmarEliminar(CiudadResponse ciudad) {
        ConfirmDialog confirmacion = new ConfirmDialog("Eliminar ciudad",
                "¿Eliminar la ciudad \"" + ciudad.nombre() + "\"? No se puede si tiene hoteles o vuelos asociados.",
                "Eliminar", e -> eliminar(ciudad));
        confirmacion.setCancelable(true);
        confirmacion.setCancelText("Cancelar");
        confirmacion.setConfirmButtonTheme("error primary");
        confirmacion.open();
    }

    private void eliminar(CiudadResponse ciudad) {
        try {
            client.eliminar(ciudad.id());
            Notificaciones.exito("Ciudad eliminada.");
            cargar();
        } catch (RestClientException e) {
            Notificaciones.error("No se pudo eliminar la ciudad", e);
        }
    }
}
