package com.columbia.viajes.views;

import com.columbia.viajes.client.PaqueteClient;
import com.columbia.viajes.client.ReservaClient;
import com.columbia.viajes.client.TuristaClient;
import com.columbia.viajes.client.UsuarioClient;
import com.columbia.viajes.dto.response.PaqueteResponse;
import com.columbia.viajes.dto.response.ReservaResponse;
import com.columbia.viajes.dto.response.TuristaResponse;
import com.columbia.viajes.dto.response.UsuarioResponse;
import com.columbia.viajes.security.Roles;
import com.columbia.viajes.security.SesionUsuario;
import com.columbia.viajes.views.base.Formatos;
import com.columbia.viajes.views.base.MainLayout;
import com.columbia.viajes.views.base.Notificaciones;
import com.vaadin.flow.component.UI;
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
import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.web.client.RestClientException;

import static com.columbia.viajes.security.enums.RolEnum.ADMIN;
import static com.columbia.viajes.security.enums.RolEnum.CLIENTE;
import static com.columbia.viajes.security.enums.RolEnum.VENDEDOR;

/**
 * Listado de reservas. El backend ya filtra: el Cliente ve solo las suyas y el
 * Vendedor solo las de su sucursal. Vendedor y Admin pueden editar y cancelar;
 * el Dueño y el Cliente solo consultan.
 */
@Route(value = "reservas", layout = MainLayout.class)
@PageTitle("Reservas | Columbia Viajes")
@RolesAllowed({Roles.CLIENTE, Roles.VENDEDOR, Roles.ADMIN, Roles.DUENIO})
public class ReservaGridView extends VerticalLayout implements BeforeEnterObserver {

    private final ReservaClient reservaClient;
    private final TuristaClient turistaClient;
    private final PaqueteClient paqueteClient;
    private final UsuarioClient usuarioClient;

    private final Grid<ReservaResponse> grid = new Grid<>();
    private Map<Integer, TuristaResponse> turistas = Map.of();
    private Map<Integer, PaqueteResponse> paquetes = Map.of();
    private Map<Integer, UsuarioResponse> vendedores = Map.of();

    // El Cliente no tiene permiso para leer /turistas ni /usuarios/vendedores
    private final boolean esCliente = SesionUsuario.tieneRol(CLIENTE);

    public ReservaGridView(ReservaClient reservaClient, TuristaClient turistaClient,
                           PaqueteClient paqueteClient, UsuarioClient usuarioClient) {
        this.reservaClient = reservaClient;
        this.turistaClient = turistaClient;
        this.paqueteClient = paqueteClient;
        this.usuarioClient = usuarioClient;

        grid.addColumn(ReservaResponse::id).setHeader("N°").setAutoWidth(true).setFlexGrow(0).setSortable(true);
        grid.addColumn(r -> Formatos.fechaHora(r.fechaReserva())).setHeader("Fecha")
                .setComparator(ReservaResponse::fechaReserva);
        if (!esCliente) {
            grid.addColumn(this::turista).setHeader("Turista").setSortable(true);
        }
        grid.addColumn(this::paquete).setHeader("Paquete").setSortable(true);
        grid.addColumn(this::estadia).setHeader("Estadía").setComparator(this::inicioEstadia);
        grid.addColumn(r -> r.claseVuelo() != null ? r.claseVuelo().getEtiqueta() : "")
                .setHeader("Clase").setSortable(true);
        grid.addColumn(r -> r.regimenHospedaje() != null ? r.regimenHospedaje().getEtiqueta() : "")
                .setHeader("Régimen").setSortable(true);
        grid.addColumn(r -> Formatos.precio(r.precioReserva())).setHeader("Precio")
                .setComparator(ReservaResponse::precioReserva);
        if (!esCliente) {
            grid.addColumn(this::vendedor).setHeader("Vendedor").setSortable(true);
        }

        if (SesionUsuario.tieneRol(VENDEDOR, ADMIN)) {
            grid.addComponentColumn(r -> {
                Button editar = new Button("Editar", e -> editar(r));
                editar.addThemeVariants(ButtonVariant.TERTIARY);
                Button cancelar = new Button("Cancelar", e -> confirmarCancelacion(r));
                cancelar.addThemeVariants(ButtonVariant.ERROR, ButtonVariant.TERTIARY);
                return new HorizontalLayout(editar, cancelar);
            }).setAutoWidth(true).setFlexGrow(0);
        }

        HorizontalLayout encabezado = new HorizontalLayout(new H2(esCliente ? "Mis reservas" : "Reservas"));
        if (SesionUsuario.tieneRol(VENDEDOR)) {
            Button nueva = new Button("Nueva reserva", VaadinIcon.PLUS.create(),
                    e -> UI.getCurrent().navigate(NuevaReservaView.class));
            nueva.addThemeVariants(ButtonVariant.PRIMARY);
            encabezado.add(nueva);
        }
        encabezado.setWidthFull();
        encabezado.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        encabezado.setAlignItems(FlexComponent.Alignment.CENTER);

        add(encabezado, grid);
        setSizeFull();
        grid.setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            paquetes = paqueteClient.listar().stream()
                    .collect(Collectors.toMap(PaqueteResponse::id, Function.identity()));
            if (!esCliente) {
                turistas = turistaClient.listar().stream()
                        .collect(Collectors.toMap(TuristaResponse::id, Function.identity()));
                vendedores = usuarioClient.listarVendedores().stream()
                        .collect(Collectors.toMap(UsuarioResponse::id, Function.identity()));
            }
        } catch (RestClientException e) {
            // Sin estos datos la grilla muestra IDs en vez de nombres
            Notificaciones.error("No se pudieron cargar todos los datos", e);
        }
        cargarReservas();
    }

    private void cargarReservas() {
        try {
            grid.setItems(reservaClient.listar());
        } catch (RestClientException e) {
            Notificaciones.error("No se pudieron cargar las reservas", e);
        }
    }

    private void editar(ReservaResponse reserva) {
        new EditarReservaDialog(reservaClient, reserva, turista(reserva), paquete(reserva),
                actualizada -> cargarReservas()).open();
    }

    private void confirmarCancelacion(ReservaResponse reserva) {
        ConfirmDialog dialogo = new ConfirmDialog(
                "Cancelar reserva N° " + reserva.id(),
                "¿Seguro que querés cancelar la reserva de " + turista(reserva) + "? Esta acción no se puede deshacer.",
                "Cancelar reserva",
                e -> cancelar(reserva));
        dialogo.setConfirmButtonTheme("error primary");
        dialogo.setCancelable(true);
        dialogo.setCancelText("Volver");
        dialogo.open();
    }

    private void cancelar(ReservaResponse reserva) {
        try {
            reservaClient.eliminar(reserva.id());
            Notificaciones.exito("Reserva cancelada.");
            cargarReservas();
        } catch (RestClientException e) {
            Notificaciones.error("No se pudo cancelar la reserva", e);
        }
    }

    private String turista(ReservaResponse r) {
        TuristaResponse t = turistas.get(r.turistaId());
        return t != null ? t.nombre() + " " + t.apellidos() : "#" + r.turistaId();
    }

    private String paquete(ReservaResponse r) {
        PaqueteResponse p = paquetes.get(r.paqueteId());
        return p != null ? p.nombre() : "#" + r.paqueteId();
    }

    private String estadia(ReservaResponse r) {
        PaqueteResponse p = paquetes.get(r.paqueteId());
        return p != null ? Formatos.fecha(p.fechaLlegadaHotel()) + " al " + Formatos.fecha(p.fechaPartidaHotel()) : "";
    }

    /** Para ordenar la columna Estadía por fecha y no por el texto. */
    private LocalDate inicioEstadia(ReservaResponse r) {
        PaqueteResponse p = paquetes.get(r.paqueteId());
        return p != null ? p.fechaLlegadaHotel() : null;
    }

    private String vendedor(ReservaResponse r) {
        UsuarioResponse u = vendedores.get(r.vendedorId());
        return u != null ? u.nombre() : "#" + r.vendedorId();
    }
}
