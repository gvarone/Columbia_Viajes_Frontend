package com.columbia.viajes.views;

import com.columbia.viajes.client.UsuarioClient;
import com.columbia.viajes.dto.UsuarioResponse;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("usuarios")
@PageTitle("Usuarios")
public class UsuariosView extends VerticalLayout {

    public UsuariosView(UsuarioClient client) {
        Grid<UsuarioResponse> grid = new Grid<>();
        grid.addColumn(UsuarioResponse::id).setHeader("ID");
        grid.addColumn(UsuarioResponse::nombre).setHeader("Nombre");
        grid.addColumn(UsuarioResponse::rolNombre).setHeader("Rol");
        grid.addColumn(UsuarioResponse::fechaCreacion).setHeader("Fecha de Creación");
        grid.addColumn(UsuarioResponse::sucursalId).setHeader("Sucursal");

        try {
            grid.setItems(client.listar());
        } catch (Exception e) {
            Notification.show("No se pudo conectar con el backend: " + e.getMessage());
        }

        add(new H2("Usuarios"), grid);
        setSizeFull();
    }
}