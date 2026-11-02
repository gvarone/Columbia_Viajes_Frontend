package com.columbia.viajes.views;

import com.columbia.viajes.client.CiudadClient;
import com.columbia.viajes.dto.request.CiudadRequest;
import com.columbia.viajes.dto.response.CiudadResponse;
import com.columbia.viajes.security.Roles;
import com.columbia.viajes.views.base.AbmDialog;
import com.columbia.viajes.views.base.AbmGridView;
import com.columbia.viajes.views.base.MainLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

/** ABM de ciudades. El Admin puede crear, editar y eliminar; el Dueño solo consulta. */
@Route(value = "ciudades", layout = MainLayout.class)
@PageTitle("Ciudades | Columbia Viajes")
@RolesAllowed({Roles.ADMIN, Roles.DUENIO})
public class CiudadGridView extends AbmGridView<CiudadResponse, CiudadRequest> {

    public CiudadGridView(CiudadClient client) {
        super(client, "Ciudades", "Nueva ciudad");
    }

    @Override
    protected void configurarColumnas() {
        grid.addColumn(CiudadResponse::id).setHeader("Código").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(CiudadResponse::nombre).setHeader("Nombre").setSortable(true);
    }

    @Override
    protected AbmDialog<CiudadResponse, CiudadRequest> crearDialogo(CiudadResponse ciudad) {
        return new CiudadDialog(client, ciudad, this::cargar);
    }

    @Override
    protected String descripcion(CiudadResponse ciudad) {
        return "la ciudad \"" + ciudad.nombre() + "\"";
    }
}
