package com.columbia.viajes.views;

import com.columbia.viajes.client.RolClient;
import com.columbia.viajes.client.SucursalClient;
import com.columbia.viajes.client.UsuarioClient;
import com.columbia.viajes.dto.request.UsuarioRequest;
import com.columbia.viajes.dto.response.RolResponse;
import com.columbia.viajes.dto.response.SucursalResponse;
import com.columbia.viajes.dto.response.UsuarioResponse;
import com.columbia.viajes.security.Roles;
import com.columbia.viajes.security.enums.RolEnum;
import com.columbia.viajes.views.base.AbmDialog;
import com.columbia.viajes.views.base.AbmGridView;
import com.columbia.viajes.views.base.Formatos;
import com.columbia.viajes.views.base.MainLayout;
import com.columbia.viajes.views.base.Notificaciones;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Set;
import org.springframework.web.client.RestClientException;

/**
 * Usuarios del sistema. El Admin da de alta Vendedores y edita/elimina Vendedores y Clientes
 * (el backend no le permite gestionar Administradores ni Dueños). El Dueño solo consulta.
 * Los Clientes se dan de alta desde Turistas o con el registro público.
 */
@Route(value = "usuarios", layout = MainLayout.class)
@PageTitle("Usuarios | Columbia Viajes")
@RolesAllowed({Roles.ADMIN, Roles.DUENIO})
public class UsuarioGridView extends AbmGridView<UsuarioResponse, UsuarioRequest> {

    /** Roles que el backend deja gestionar al Admin. */
    private static final Set<String> ROLES_GESTIONABLES =
            Set.of(RolEnum.CLIENTE.getNombreEnDb(), RolEnum.VENDEDOR.getNombreEnDb());

    private final SucursalClient sucursalClient;
    private final RolClient rolClient;

    private List<SucursalResponse> sucursales = List.of();
    private List<RolResponse> roles = List.of();

    public UsuarioGridView(UsuarioClient client, SucursalClient sucursalClient, RolClient rolClient) {
        super(client, "Usuarios", "Nuevo vendedor");
        this.sucursalClient = sucursalClient;
        this.rolClient = rolClient;
    }

    @Override
    protected void configurarColumnas() {
        grid.addColumn(UsuarioResponse::id).setHeader("ID").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(UsuarioResponse::nombre).setHeader("Nombre").setSortable(true);
        grid.addColumn(UsuarioResponse::rolNombre).setHeader("Rol").setSortable(true);
        grid.addColumn(u -> Formatos.fechaHora(u.fechaCreacion())).setHeader("Fecha de creación")
                .setComparator(UsuarioResponse::fechaCreacion);
        grid.addColumn(this::sucursal).setHeader("Sucursal").setSortable(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            sucursales = sucursalClient.listar();
            if (puedeEditar()) {
                roles = rolClient.listar();
            }
        } catch (RestClientException e) {
            Notificaciones.error("No se pudieron cargar todos los datos", e);
        }
        super.beforeEnter(event);
    }

    /** Administradores y Dueños se muestran pero no se pueden editar ni eliminar desde acá. */
    @Override
    protected boolean puedeModificar(UsuarioResponse usuario) {
        return ROLES_GESTIONABLES.contains(usuario.rolNombre());
    }

    @Override
    protected AbmDialog<UsuarioResponse, UsuarioRequest> crearDialogo(UsuarioResponse usuario) {
        String nombreRol = usuario == null ? RolEnum.VENDEDOR.getNombreEnDb() : usuario.rolNombre();
        RolResponse rol = roles.stream()
                .filter(r -> r.nombre().equals(nombreRol))
                .findFirst()
                .orElse(new RolResponse(null, nombreRol));
        return new UsuarioDialog(client, rol, sucursales, usuario, this::cargar);
    }

    @Override
    protected String descripcion(UsuarioResponse usuario) {
        return "el usuario \"" + usuario.nombre() + "\" (" + usuario.rolNombre() + ")";
    }

    private String sucursal(UsuarioResponse usuario) {
        if (usuario.sucursalId() == null) {
            return "";
        }
        return sucursales.stream()
                .filter(s -> s.id().equals(usuario.sucursalId()))
                .map(SucursalResponse::direccion)
                .findFirst()
                .orElse("#" + usuario.sucursalId());
    }
}
