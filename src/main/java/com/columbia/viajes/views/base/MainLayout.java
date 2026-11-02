package com.columbia.viajes.views.base;

import com.columbia.viajes.security.SesionUsuario;
import com.columbia.viajes.views.CiudadGridView;
import com.columbia.viajes.views.HomeView;
import com.columbia.viajes.views.HotelGridView;
import com.columbia.viajes.views.NuevaReservaView;
import com.columbia.viajes.views.PaqueteCatalogoView;
import com.columbia.viajes.views.PaqueteGridView;
import com.columbia.viajes.views.PerfilView;
import com.columbia.viajes.views.RankingVendedoresView;
import com.columbia.viajes.views.ReservaGridView;
import com.columbia.viajes.views.SucursalGridView;
import com.columbia.viajes.views.TuristaGridView;
import com.columbia.viajes.views.UsuarioGridView;
import com.columbia.viajes.views.VueloGridView;
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
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;

import static com.columbia.viajes.security.enums.RolEnum.ADMIN;
import static com.columbia.viajes.security.enums.RolEnum.CLIENTE;
import static com.columbia.viajes.security.enums.RolEnum.DUENIO;
import static com.columbia.viajes.security.enums.RolEnum.VENDEDOR;

/**
 * Layout con header y menú lateral. El menú solo muestra lo que el rol puede ver;
 * el control de acceso real está en las anotaciones de cada vista. El layout
 * necesita @PermitAll: Vaadin no deja que una vista sea más abierta que su layout.
 */
@PermitAll
public class MainLayout extends AppLayout {

    private final AuthenticationContext authContext;

    public MainLayout(AuthenticationContext authContext) {
        this.authContext = authContext;
        setPrimarySection(Section.DRAWER);
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        DrawerToggle toggle = new DrawerToggle();

        H1 logo = new H1("Columbia Viajes");
        logo.addClassName("app-logo");

        String datos = SesionUsuario.getUsuario()
                .map(u -> u.nombre() + " · " + u.rol().getNombreEnDb())
                .orElse("");
        Span datosUsuario = new Span(datos);
        datosUsuario.addClassNames("texto-secundario", "texto-chico");

        Button logout = new Button("Cerrar sesión", VaadinIcon.SIGN_OUT.create(), e -> authContext.logout());
        logout.addThemeVariants(ButtonVariant.TERTIARY);

        HorizontalLayout header = new HorizontalLayout(toggle, logo, datosUsuario, logout);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.addClassName("app-encabezado");

        addToNavbar(header);
    }

    private void createDrawer() {
        SideNav nav = new SideNav();
        nav.addItem(new SideNavItem("Inicio", HomeView.class, VaadinIcon.HOME.create()));

        SideNavItem reservas = new SideNavItem("Reservas");
        reservas.setPrefixComponent(VaadinIcon.CALENDAR_CLOCK.create());
        if (SesionUsuario.tieneRol(VENDEDOR)) {
            reservas.addItem(new SideNavItem("Hacer reserva", NuevaReservaView.class));
        }
        reservas.addItem(new SideNavItem(
                SesionUsuario.tieneRol(CLIENTE) ? "Mis reservas" : "Consultar reservas", ReservaGridView.class));
        nav.addItem(reservas);

        SideNavItem catalogo = new SideNavItem("Catálogo");
        catalogo.setPrefixComponent(VaadinIcon.AIRPLANE.create());
        catalogo.addItem(new SideNavItem("Paquetes", PaqueteCatalogoView.class));
        if (SesionUsuario.tieneRol(VENDEDOR, ADMIN)) {
            catalogo.addItem(new SideNavItem("Turistas", TuristaGridView.class));
        }
        catalogo.addItem(new SideNavItem("Vuelos", VueloGridView.class));
        catalogo.addItem(new SideNavItem("Hoteles", HotelGridView.class));
        nav.addItem(catalogo);

        if (SesionUsuario.tieneRol(ADMIN, DUENIO)) {
            SideNavItem admin = new SideNavItem("Administración");
            admin.setPrefixComponent(VaadinIcon.COG.create());
            admin.addItem(new SideNavItem("Usuarios", UsuarioGridView.class));
            admin.addItem(new SideNavItem("Sucursales", SucursalGridView.class));
            admin.addItem(new SideNavItem("Ciudades", CiudadGridView.class));
            admin.addItem(new SideNavItem("Paquetes", PaqueteGridView.class));
            if (SesionUsuario.tieneRol(DUENIO)) {
                admin.addItem(new SideNavItem("Ranking de vendedores", RankingVendedoresView.class));
            }
            nav.addItem(admin);
        }

        nav.addItem(new SideNavItem("Mi perfil", PerfilView.class, VaadinIcon.USER.create()));

        addToDrawer(nav);
    }
}
