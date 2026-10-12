package com.columbia.viajes.views;

import com.columbia.viajes.client.UsuarioClient;
import com.columbia.viajes.dto.response.UsuarioResponse;
import com.columbia.viajes.security.SesionUsuario;
import com.columbia.viajes.views.base.ProtectedView;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("usuarios")
@PageTitle("Usuarios")
public class UsuarioGridView extends ProtectedView {

    private final UsuarioClient client;
    private final Grid<UsuarioResponse> grid = new Grid<>();

    public UsuarioGridView(UsuarioClient client) {
        this.client = client;

        // Columnas del Grid
        grid.addColumn(UsuarioResponse::id).setHeader("ID");
        grid.addColumn(UsuarioResponse::nombre).setHeader("Nombre");
        grid.addColumn(UsuarioResponse::rolNombre).setHeader("Rol");
        grid.addColumn(UsuarioResponse::fechaCreacion).setHeader("Fecha de Creación");
        grid.addColumn(UsuarioResponse::sucursalId).setHeader("Sucursal");

        add(new H2("Usuarios"), grid);
        setSizeFull();
        grid.setSizeFull();
    }

    @Override
    protected void onBeforeEnter(BeforeEnterEvent event) {
        // La clase base 'ProtectedView' ya validó el login. 
        try {
            grid.setItems(client.listar());
        } catch (Exception e) {
            Notification.show("No se pudo conectar con el backend o token expirado: " + e.getMessage(), 
                    5000, Notification.Position.TOP_CENTER);
            
            // Si el backend rechaza la petición por token vencido/inválido:
            SesionUsuario.cerrarSesion();
            event.rerouteTo("login");
        }
    }
}