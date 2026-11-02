package com.columbia.viajes.views;

import com.columbia.viajes.security.SesionUsuario;
import com.columbia.viajes.views.base.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.card.CardVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
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
import static com.columbia.viajes.security.enums.RolEnum.CLIENTE;
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
        subtitulo.addClassName("texto-secundario");
        add(titulo, subtitulo);

        List<Card> reservas = new ArrayList<>();
        if (SesionUsuario.tieneRol(VENDEDOR)) {
            reservas.add(tarjeta(VaadinIcon.PLUS_CIRCLE, "Hacer reserva",
                    "Cargar una reserva de paquete para un turista.", NuevaReservaView.class, true));
        }
        if (SesionUsuario.tieneRol(CLIENTE)) {
            reservas.add(tarjeta(VaadinIcon.LIST, "Mis reservas",
                    "Ver los paquetes que reservaste.", ReservaGridView.class, false));
        } else {
            reservas.add(tarjeta(VaadinIcon.LIST, "Consultar reservas",
                    SesionUsuario.tieneRol(DUENIO) ? "Ver las reservas cargadas."
                            : "Ver, editar y cancelar las reservas cargadas.",
                    ReservaGridView.class, false));
        }
        add(seccion("Reservas"), grilla(reservas));

        List<Card> catalogo = new ArrayList<>();
        catalogo.add(tarjeta(VaadinIcon.SUITCASE, "Paquetes",
                SesionUsuario.tieneRol(VENDEDOR) ? "Paquetes de tu sucursal para reservar."
                        : "Paquetes de viaje disponibles.",
                PaqueteCatalogoView.class, !SesionUsuario.tieneRol(VENDEDOR)));
        if (SesionUsuario.tieneRol(VENDEDOR, ADMIN, DUENIO)) {
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
        h.addClassName("inicio-seccion");
        return h;
    }

    private Div grilla(List<Card> tarjetas) {
        Div grilla = new Div(tarjetas.toArray(Card[]::new));
        grilla.addClassName("inicio-tarjetas");
        return grilla;
    }

    /** Acceso a una sección. "destacada" marca la acción principal del rol (borde e ícono de acento). */
    private Card tarjeta(VaadinIcon icono, String titulo, String descripcion,
                         Class<? extends Component> destino, boolean destacada) {
        Icon icon = icono.create();
        icon.setSize("32px");

        Card card = new Card();
        card.addThemeVariants(CardVariant.OUTLINED);
        card.setHeaderPrefix(icon);
        card.setTitle(titulo);
        card.setSubtitle(descripcion);
        card.addClassName("inicio-tarjeta");
        if (destacada) {
            card.addClassName("inicio-tarjeta-destacada");
        }
        card.getElement().addEventListener("click", e -> UI.getCurrent().navigate(destino));
        return card;
    }
}
