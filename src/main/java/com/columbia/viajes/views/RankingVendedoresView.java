package com.columbia.viajes.views;

import com.columbia.viajes.client.ReservaClient;
import com.columbia.viajes.client.SucursalClient;
import com.columbia.viajes.dto.response.RankingVendedorResponse;
import com.columbia.viajes.dto.response.SucursalResponse;
import com.columbia.viajes.security.Roles;
import com.columbia.viajes.views.base.Formatos;
import com.columbia.viajes.views.base.MainLayout;
import com.columbia.viajes.views.base.Notificaciones;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.client.RestClientException;

/**
 * Ranking de vendedores por total vendido. El cálculo y el orden los hace el backend;
 * esta vista solo agrega la posición y la dirección de la sucursal.
 */
@Route(value = "ranking-vendedores", layout = MainLayout.class)
@PageTitle("Ranking de vendedores | Columbia Viajes")
@RolesAllowed({Roles.DUENIO})
public class RankingVendedoresView extends VerticalLayout implements BeforeEnterObserver {

    private final ReservaClient reservaClient;
    private final SucursalClient sucursalClient;
    private final Grid<RankingVendedorResponse> grid = new Grid<>();

    private List<RankingVendedorResponse> ranking = List.of();
    private Map<Integer, String> sucursales = Map.of();

    public RankingVendedoresView(ReservaClient reservaClient, SucursalClient sucursalClient) {
        this.reservaClient = reservaClient;
        this.sucursalClient = sucursalClient;

        grid.addColumn(r -> ranking.indexOf(r) + 1).setHeader("Puesto").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(RankingVendedorResponse::nombre).setHeader("Vendedor");
        grid.addColumn(r -> sucursales.getOrDefault(r.sucursalId(), "Sucursal " + r.sucursalId()))
                .setHeader("Sucursal");
        grid.addColumn(r -> Formatos.precio(r.totalVendido())).setHeader("Total vendido")
                .setTextAlign(ColumnTextAlign.END);

        add(new H2("Ranking de vendedores"), grid);
        setSizeFull();
        grid.setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            sucursales = sucursalClient.listar().stream()
                    .collect(Collectors.toMap(SucursalResponse::id, SucursalResponse::direccion));
            ranking = reservaClient.rankingVendedores();
            grid.setItems(ranking);
        } catch (RestClientException e) {
            Notificaciones.error("No se pudo cargar el ranking de vendedores", e);
        }
    }
}
