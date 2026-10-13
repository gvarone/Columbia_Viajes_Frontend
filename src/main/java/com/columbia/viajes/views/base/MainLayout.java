package com.columbia.viajes.views.base;

import com.columbia.viajes.security.SesionUsuario;
import com.columbia.viajes.views.HomeView;
import com.columbia.viajes.views.UsuarioGridView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;

public class MainLayout extends AppLayout {

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        DrawerToggle toggle = new DrawerToggle();

        H1 logo = new H1("Columbia Viajes");
        logo.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", "0");
        
        var usuario = SesionUsuario.getUsuario();
        
        String datos = "";
        if (usuario != null) {
            var rol = SesionUsuario.getRol();
            String nombreRol = (rol != null) ? rol.getNombreEnDb() : "Sin rol";
            datos = usuario.nombre() + " · " + nombreRol;
        }

        Span datosUsuario = new Span(datos);
        datosUsuario.getStyle().set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "var(--lumo-font-size-s)");

        Button logout = new Button("Cerrar sesión", VaadinIcon.SIGN_OUT.create(), e -> {
            SesionUsuario.cerrarSesion();
            getUI().ifPresent(ui -> ui.navigate("login"));
        });
        logout.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout header = new HorizontalLayout(toggle, logo, datosUsuario, logout);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.getStyle().set("padding", "0 var(--lumo-space-m)");

        addToNavbar(header);
    }

    private void createDrawer() {
        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("Inicio", HomeView.class, VaadinIcon.HOME.create()));

        SideNavItem reservas = new SideNavItem("Reservas");
        reservas.setPrefixComponent(VaadinIcon.CALENDAR_CLOCK.create());
        reservas.addItem(new SideNavItem("Hacer reserva", "reservas/nueva"));
        reservas.addItem(new SideNavItem("Cancelar reserva", "reservas/cancelar"));
        reservas.addItem(new SideNavItem("Consultar reservas", "reservas"));
        nav.addItem(reservas);

        SideNavItem catalogo = new SideNavItem("Catálogo");
        catalogo.setPrefixComponent(VaadinIcon.AIRPLANE.create());
        catalogo.addItem(new SideNavItem("Turistas", "turistas"));
        catalogo.addItem(new SideNavItem("Vuelos", "vuelos"));
        catalogo.addItem(new SideNavItem("Hoteles", "hoteles"));
        nav.addItem(catalogo);

        if (SesionUsuario.estaLogueado() && SesionUsuario.esAdmin()) {
            SideNavItem admin = new SideNavItem("Administración");
            admin.setPrefixComponent(VaadinIcon.COG.create());
            admin.addItem(new SideNavItem("Usuarios", UsuarioGridView.class));
            admin.addItem(new SideNavItem("Sucursales", "sucursales"));
            nav.addItem(admin);
        }

        addToDrawer(nav);
    }
}