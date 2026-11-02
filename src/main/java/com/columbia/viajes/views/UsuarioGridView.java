package com.columbia.viajes.views;

import com.columbia.viajes.client.SucursalClient;
import com.columbia.viajes.client.UsuarioClient;
import com.columbia.viajes.dto.response.SucursalResponse;
import com.columbia.viajes.dto.response.UsuarioResponse;
import com.columbia.viajes.security.Roles;
import com.columbia.viajes.views.base.Formatos;
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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.web.client.RestClientException;

@Route(value = "usuarios", layout = MainLayout.class)
@PageTitle("Usuarios | Columbia Viajes")
@RolesAllowed({Roles.ADMIN, Roles.DUENIO})
public class UsuarioGridView extends VerticalLayout implements BeforeEnterObserver {

    private final UsuarioClient usuarioClient;
    private final SucursalClient sucursalClient;
    private final Grid<UsuarioResponse> grid = new Grid<>();
    private Map<Integer, SucursalResponse> sucursales = Map.of();

    public UsuarioGridView(UsuarioClient usuarioClient, SucursalClient sucursalClient) {
        this.usuarioClient = usuarioClient;
        this.sucursalClient = sucursalClient;

        grid.addColumn(UsuarioResponse::id).setHeader("ID").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(UsuarioResponse::nombre).setHeader("Nombre").setSortable(true);
        grid.addColumn(UsuarioResponse::rolNombre).setHeader("Rol").setSortable(true);
        grid.addColumn(u -> Formatos.fechaHora(u.fechaCreacion())).setHeader("Fecha de creación")
                .setComparator(UsuarioResponse::fechaCreacion);
        grid.addColumn(this::sucursal).setHeader("Sucursal").setSortable(true);

        add(new H2("Usuarios"), grid);
        setSizeFull();
        grid.setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            sucursales = sucursalClient.listar().stream()
                    .collect(Collectors.toMap(SucursalResponse::id, Function.identity()));
            grid.setItems(usuarioClient.listar());
        } catch (RestClientException e) {
            Notificaciones.error("No se pudieron cargar los usuarios", e);
        }
    }

    private String sucursal(UsuarioResponse usuario) {
        if (usuario.sucursalId() == null) {
            return "";
        }
        SucursalResponse s = sucursales.get(usuario.sucursalId());
        return s != null ? s.direccion() : "#" + usuario.sucursalId();
    }
}
