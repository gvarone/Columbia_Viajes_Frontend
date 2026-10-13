package com.columbia.viajes.views;

import com.columbia.viajes.client.SucursalClient;
import com.columbia.viajes.dto.response.SucursalResponse;
import com.columbia.viajes.security.SesionUsuario;
import com.columbia.viajes.views.base.MainLayout;
import com.columbia.viajes.views.base.ProtectedView;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "sucursales", layout = MainLayout.class)
@PageTitle("Sucursales")
public class SucursalGridView extends ProtectedView {

    private final SucursalClient client;
    private final Grid<SucursalResponse> grid = new Grid<>();

    public SucursalGridView(SucursalClient client) {
        this.client = client;

        // Columnas del Grid
        grid.addColumn(SucursalResponse::id).setHeader("ID");
        grid.addColumn(SucursalResponse::direccion).setHeader("Dirección");
        grid.addColumn(SucursalResponse::email).setHeader("email");
        grid.addColumn(SucursalResponse::telefono).setHeader("Teléfono");

        add(new H2("Sucursales"), grid);
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