package com.columbia.viajes.views;

import com.columbia.viajes.security.SesionUsuario;
import com.columbia.viajes.views.base.MainLayout;
import com.columbia.viajes.views.base.ProtectedView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Inicio | Columbia Viajes")
public class HomeView extends ProtectedView {

    public HomeView() {
        setPadding(true);
        setSpacing(true);

        var usuario = SesionUsuario.getUsuario();
        var rol = SesionUsuario.getRol();
        String nombre = usuario != null ? usuario.nombre() : "Usuario";

        H1 titulo = new H1("Bienvenido, " + nombre);
        titulo.getStyle().set("margin", "0");
        Paragraph subtitulo = new Paragraph(
                "Sesión iniciada como " + rol + ".");
        subtitulo.getStyle().set("color", "var(--lumo-secondary-text-color)");

        add(titulo, subtitulo);

        add(seccion("Reservas"), grilla(
                tarjeta(VaadinIcon.PLUS_CIRCLE, "Hacer reserva",
                        "Cargar una reserva de vuelo y/o hotel para un turista.",
                        "reservas/nueva", true),
                tarjeta(VaadinIcon.CLOSE_CIRCLE, "Cancelar reserva",
                        "Dar de baja una reserva existente.",
                        "reservas/cancelar", false),
                tarjeta(VaadinIcon.LIST, "Consultar reservas",
                        "Ver y buscar las reservas cargadas.",
                        "reservas", false)));

        add(seccion("Catálogo"), grilla(
                tarjeta(VaadinIcon.USERS, "Turistas",
                        "Registrar y consultar turistas.", "turistas", false),
                tarjeta(VaadinIcon.AIRPLANE, "Vuelos",
                        "Vuelos disponibles y plazas.", "vuelos", false),
                tarjeta(VaadinIcon.BUILDING, "Hoteles",
                        "Hoteles disponibles y plazas.", "hoteles", false)));

        if (SesionUsuario.esAdmin()) {
            add(seccion("Administración"), grilla(
                    tarjeta(VaadinIcon.USER_CARD, "Usuarios",
                            "Alta y gestión de usuarios del sistema.", "usuarios", false),
                    tarjeta(VaadinIcon.OFFICE, "Sucursales",
                            "Alta y gestión de sucursales.", "sucursales", false)));
        }
    }

    private H3 seccion(String texto) {
        H3 h = new H3(texto);
        h.getStyle().set("margin", "var(--lumo-space-l) 0 0 0");
        return h;
    }

    private Div grilla(Div... tarjetas) {
        Div grilla = new Div(tarjetas);
        grilla.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fill, minmax(240px, 1fr))")
                .set("gap", "var(--lumo-space-m)")
                .set("width", "100%");
        return grilla;
    }

    private Div tarjeta(VaadinIcon icono, String titulo, String descripcion,
                        String ruta, boolean destacada) {
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
        card.addClickListener(e -> UI.getCurrent().navigate(ruta));
        return card;
    }
}