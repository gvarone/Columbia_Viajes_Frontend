package com.columbia.viajes.views;

import com.columbia.viajes.client.TuristaClient;
import com.columbia.viajes.dto.request.RegistroClienteRequest;
import com.columbia.viajes.dto.response.TuristaResponse;
import com.columbia.viajes.security.Roles;
import com.columbia.viajes.security.SesionUsuario;
import com.columbia.viajes.views.base.AbmDialog;
import com.columbia.viajes.views.base.AbmGridView;
import com.columbia.viajes.views.base.MainLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import static com.columbia.viajes.security.enums.RolEnum.ADMIN;
import static com.columbia.viajes.security.enums.RolEnum.VENDEDOR;

/**
 * Turistas (clientes). Vendedor y Admin dan de alta; editar y eliminar solo el Admin,
 * porque el backend no le permite PUT/DELETE al Vendedor. El Dueño consulta.
 */
@Route(value = "turistas", layout = MainLayout.class)
@PageTitle("Turistas | Columbia Viajes")
@RolesAllowed({Roles.VENDEDOR, Roles.ADMIN, Roles.DUENIO})
public class TuristaGridView extends AbmGridView<TuristaResponse, RegistroClienteRequest> {

    public TuristaGridView(TuristaClient client) {
        super(client, "Turistas", "Nuevo turista");
    }

    @Override
    protected void configurarColumnas() {
        grid.addColumn(TuristaResponse::id).setHeader("Código").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(TuristaResponse::nombre).setHeader("Nombre").setSortable(true);
        grid.addColumn(TuristaResponse::apellidos).setHeader("Apellidos").setSortable(true);
        grid.addColumn(TuristaResponse::direccion).setHeader("Dirección").setSortable(true);
        grid.addColumn(TuristaResponse::email).setHeader("Email").setSortable(true);
        grid.addColumn(TuristaResponse::telefono).setHeader("Teléfono").setSortable(true);
    }

    /** El alta usa el registro público, que el backend permite a cualquier rol. */
    @Override
    protected boolean puedeCrear() {
        return SesionUsuario.tieneRol(VENDEDOR, ADMIN);
    }

    @Override
    protected AbmDialog<TuristaResponse, RegistroClienteRequest> crearDialogo(TuristaResponse turista) {
        return new TuristaDialog(client, turista, guardado -> cargar());
    }

    @Override
    protected String descripcion(TuristaResponse turista) {
        return "el turista \"" + turista.nombre() + " " + turista.apellidos() + "\" y su cuenta";
    }
}
