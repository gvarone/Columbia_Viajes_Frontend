package com.columbia.viajes.views;

import com.columbia.viajes.client.PaqueteClient;
import com.columbia.viajes.client.ReservaClient;
import com.columbia.viajes.client.TuristaClient;
import com.columbia.viajes.client.UsuarioClient;
import com.columbia.viajes.dto.enums.ClaseVuelo;
import com.columbia.viajes.dto.enums.RegimenHospedaje;
import com.columbia.viajes.dto.request.ReservaRequest;
import com.columbia.viajes.dto.response.PaqueteResponse;
import com.columbia.viajes.dto.response.TuristaResponse;
import com.columbia.viajes.security.Roles;
import com.columbia.viajes.views.base.Formatos;
import com.columbia.viajes.views.base.MainLayout;
import com.columbia.viajes.views.base.Notificaciones;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import java.util.List;
import org.springframework.web.client.RestClientException;

/**
 * Alta de reserva de un paquete. Solo Vendedor: el backend toma el vendedor del token,
 * calcula el precio final y valida que el paquete sea de su sucursal.
 */
@Route(value = "reservas/nueva", layout = MainLayout.class)
@PageTitle("Nueva reserva | Columbia Viajes")
@RolesAllowed(Roles.VENDEDOR)
public class NuevaReservaView extends VerticalLayout implements BeforeEnterObserver {

    /** Query param para llegar con un paquete preseleccionado (desde el catálogo). */
    public static final String PARAMETRO_PAQUETE = "paquete";

    private final ReservaClient reservaClient;
    private final TuristaClient turistaClient;
    private final PaqueteClient paqueteClient;
    private final UsuarioClient usuarioClient;

    private final ComboBox<TuristaResponse> turista = new ComboBox<>("Turista");
    private final ComboBox<PaqueteResponse> paquete = new ComboBox<>("Paquete");
    private final RadioButtonGroup<ClaseVuelo> clase = new RadioButtonGroup<>("Clase de vuelo");
    private final RadioButtonGroup<RegimenHospedaje> regimen = new RadioButtonGroup<>("Régimen de hospedaje");

    public NuevaReservaView(ReservaClient reservaClient, TuristaClient turistaClient,
                            PaqueteClient paqueteClient, UsuarioClient usuarioClient) {
        this.reservaClient = reservaClient;
        this.turistaClient = turistaClient;
        this.paqueteClient = paqueteClient;
        this.usuarioClient = usuarioClient;
        setPadding(true);
        setMaxWidth("720px");

        turista.setItemLabelGenerator(t -> t.nombre() + " " + t.apellidos());
        turista.setRequired(true);
        turista.setWidthFull();

        Button nuevoTurista = new Button("Nuevo turista", e ->
                new TuristaDialog(turistaClient, creado -> {
                    cargarTuristas();
                    turista.setValue(creado);
                }).open());
        HorizontalLayout filaTurista = new HorizontalLayout(turista, nuevoTurista);
        filaTurista.setWidthFull();
        filaTurista.setAlignItems(FlexComponent.Alignment.END);
        filaTurista.expand(turista);

        paquete.setItemLabelGenerator(p -> p.nombre() + " · " + Formatos.fecha(p.fechaLlegadaHotel())
                + " al " + Formatos.fecha(p.fechaPartidaHotel()) + " · desde " + Formatos.precio(p.precio()));
        paquete.setHelperText("Precio base. Primera clase y pensión completa suman un 10% cada una.");
        paquete.setRequired(true);
        paquete.setWidthFull();

        clase.setItems(ClaseVuelo.values());
        clase.setItemLabelGenerator(ClaseVuelo::getEtiqueta);
        clase.setValue(ClaseVuelo.TURISTA);
        regimen.setItems(RegimenHospedaje.values());
        regimen.setItemLabelGenerator(RegimenHospedaje::getEtiqueta);
        regimen.setValue(RegimenHospedaje.MEDIA_PENSION);

        Button guardar = new Button("Confirmar reserva", e -> guardar());
        guardar.addThemeVariants(ButtonVariant.PRIMARY);

        add(new H2("Nueva reserva"), filaTurista, paquete, clase, regimen, guardar);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        cargarTuristas();
        try {
            List<PaqueteResponse> paquetes = paquetesDeMiSucursal(paqueteClient.listar());
            paquete.setItems(paquetes);
            preseleccionarPaquete(event, paquetes);
        } catch (RestClientException e) {
            Notificaciones.error("No se pudieron cargar los paquetes", e);
        }
    }

    private void preseleccionarPaquete(BeforeEnterEvent event, List<PaqueteResponse> paquetes) {
        event.getLocation().getQueryParameters().getSingleParameter(PARAMETRO_PAQUETE)
                .flatMap(id -> paquetes.stream().filter(p -> String.valueOf(p.id()).equals(id)).findFirst())
                .ifPresent(paquete::setValue);
    }

    private void cargarTuristas() {
        try {
            turista.setItems(turistaClient.listar());
        } catch (RestClientException e) {
            Notificaciones.error("No se pudieron cargar los turistas", e);
        }
    }

    /** El backend solo acepta paquetes de la sucursal del vendedor logueado. */
    private List<PaqueteResponse> paquetesDeMiSucursal(List<PaqueteResponse> todos) {
        Integer miSucursal = usuarioClient.miPerfil().usuario().sucursalId();
        if (miSucursal == null) {
            Notificaciones.error("Tu usuario no tiene una sucursal asignada.");
            return List.of();
        }
        return todos.stream().filter(p -> miSucursal.equals(p.sucursalId())).toList();
    }

    private void guardar() {
        if (turista.isEmpty() || paquete.isEmpty() || clase.isEmpty() || regimen.isEmpty()) {
            Notificaciones.error("Completá turista, paquete, clase y régimen.");
            return;
        }
        var request = new ReservaRequest(
                paquete.getValue().id(),
                turista.getValue().id(),
                clase.getValue(),
                regimen.getValue(),
                null);
        try {
            var creada = reservaClient.crear(request);
            Notificaciones.exito("Reserva N° " + creada.id() + " creada. Precio final: "
                    + Formatos.precio(creada.precioReserva()));
            UI.getCurrent().navigate(ReservaGridView.class);
        } catch (RestClientException ex) {
            Notificaciones.error("No se pudo crear la reserva", ex);
        }
    }
}
