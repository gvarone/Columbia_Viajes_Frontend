package com.columbia.viajes.views;

import com.columbia.viajes.client.SucursalClient;
import com.columbia.viajes.dto.request.SucursalRequest;
import com.columbia.viajes.dto.response.SucursalResponse;
import com.columbia.viajes.security.Roles;
import com.columbia.viajes.views.base.AbmDialog;
import com.columbia.viajes.views.base.AbmGridView;
import com.columbia.viajes.views.base.MainLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

/** ABM de sucursales. El Admin puede crear, editar y eliminar; el Dueño solo consulta. */
@Route(value = "sucursales", layout = MainLayout.class)
@PageTitle("Sucursales | Columbia Viajes")
@RolesAllowed({Roles.ADMIN, Roles.DUENIO})
public class SucursalGridView extends AbmGridView<SucursalResponse, SucursalRequest> {

    public SucursalGridView(SucursalClient client) {
        super(client, "Sucursales", "Nueva sucursal");
    }

    @Override
    protected void configurarColumnas() {
        grid.addColumn(SucursalResponse::id).setHeader("Código").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(SucursalResponse::direccion).setHeader("Dirección").setSortable(true);
        grid.addColumn(SucursalResponse::email).setHeader("Email").setSortable(true);
        grid.addColumn(SucursalResponse::telefono).setHeader("Teléfono").setSortable(true);
    }

    @Override
    protected AbmDialog<SucursalResponse, SucursalRequest> crearDialogo(SucursalResponse sucursal) {
        return new SucursalDialog(client, sucursal, this::cargar);
    }

    @Override
    protected String descripcion(SucursalResponse sucursal) {
        return "la sucursal de \"" + sucursal.direccion() + "\"";
    }
}
