package com.columbia.viajes.views;

import com.columbia.viajes.security.SesionUsuario;
import com.columbia.viajes.views.base.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import java.util.ArrayList;
import java.util.List;

import static com.columbia.viajes.security.enums.RolEnum.ADMIN;
import static com.columbia.viajes.security.enums.RolEnum.DUENIO;
import static com.columbia.viajes.security.enums.RolEnum.VENDEDOR;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Inicio | Columbia Viajes")
@PermitAll
public class HomeView extends VerticalLayout {

    public HomeView() {
        setPadding(true);
        setSpacing(true);

        String nombre = SesionUsuario.getUsuario().map(u -> u.nombre()).orElse("Usuario");
        String rol = SesionUsuario.getRol() != null ? SesionUsuario.getRol().getNombreEnDb() : "";

        H1 titulo = new H1("Bienvenido, " + nombre);
        titulo.getStyle().set("margin", "0");
        Paragraph subtitulo = new Paragraph("Sesión iniciada como " + rol + ".");
        subtitulo.getStyle().set("color", "var(--lumo-secondary-text-color)");
        add(titulo, subtitulo);

        if (SesionUsuario.tieneRol(VENDEDOR, ADMIN, DUENIO)) {
            List<Div> reservas = new ArrayList<>();
            if (SesionUsuario.tieneRol(VENDEDOR)) {
                reservas.add(tarjeta(VaadinIcon.PLUS_CIRCLE, "Hacer reserva",
                        "Cargar una reserva de paquete para un turista.", NuevaReservaView.class, true));
            }
            reservas.add(tarjeta(VaadinIcon.LIST, "Consultar reservas",
                    SesionUsuario.tieneRol(DUENIO) ? "Ver las reservas cargadas."
                            : "Ver y cancelar las reservas cargadas.",
                    ReservaGridView.class, false));
            add(seccion("Reservas"), grilla(reservas));
        }

        List<Div> catalogo = new ArrayList<>();
        if (SesionUsuario.tieneRol(VENDEDOR, ADMIN)) {
            catalogo.add(tarjeta(VaadinIcon.USERS, "Turistas",
                    "Registrar y consultar turistas.", TuristaGridView.class, false));
        }
        catalogo.add(tarjeta(VaadinIcon.AIRPLANE, "Vuelos",
                "Vuelos disponibles y plazas.", VueloGridView.class, false));
        catalogo.add(tarjeta(VaadinIcon.BUILDING, "Hoteles",
                "Hoteles disponibles y plazas.", HotelGridView.class, false));
        add(seccion("Catálogo"), grilla(catalogo));

        if (SesionUsuario.tieneRol(ADMIN, DUENIO)) {
            add(seccion("Administración"), grilla(List.of(
                    tarjeta(VaadinIcon.USER_CARD, "Usuarios",
                            "Usuarios del sistema.", UsuarioGridView.class, false),
                    tarjeta(VaadinIcon.OFFICE, "Sucursales",
                            "Sucursales de la cadena.", SucursalGridView.class, false))));
        }
    }

    private H3 seccion(String texto) {
        H3 h = new H3(texto);
        h.getStyle().set("margin", "var(--lumo-space-l) 0 0 0");
        return h;
    }

    private Div grilla(List<Div> tarjetas) {
        Div grilla = new Div(tarjetas.toArray(Div[]::new));
        grilla.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fill, minmax(240px, 1fr))")
                .set("gap", "var(--lumo-space-m)")
                .set("width", "100%");
        return grilla;
    }

    private Div tarjeta(VaadinIcon icono, String titulo, String descripcion,
                        Class<? extends Component> destino, boolean destacada) {
        Icon icon = icono.create();
        icon.setSize("var(--lumo-icon-size-l)");
        icon.setColor(destacada ? "var(--lumo-primary-color)" : "var(--lumo-contrast-60pct)");

        H4 h = new H4(titulo);
        h.getStyle().set("margin", "var(--lumo-space-s) 0 var(--lumo-space-xs) 0");

        Paragraph p = new Paragraph(descripcion);
        p.getStyle().set("margin", "0")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-s)");

        Div card = new Div(icon, h, p);
        card.getStyle()
                .set("padding", "var(--lumo-space-m)")
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("background", "var(--lumo-base-color)")
                .set("box-shadow", "var(--lumo-box-shadow-s)")
                .set("border", destacada
                        ? "2px solid var(--lumo-primary-color)"
                        : "1px solid var(--lumo-contrast-10pct)")
                .set("cursor", "pointer");
        card.addClickListener(e -> UI.getCurrent().navigate(destino));
        return card;
    }
}
