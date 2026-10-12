package com.columbia.viajes.views.base;

import com.columbia.viajes.security.SesionUsuario;
import com.columbia.viajes.views.HomeView;
import com.columbia.viajes.views.UsuarioGridView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        // Título o Logo de la app en la barra superior
        H1 logo = new H1("Columbia Viajes");
        logo.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        // Botón para colapsar/expandir el menú lateral (drawer)
        DrawerToggle toggle = new DrawerToggle();

        // Botón de Cerrar Sesión global en la esquina superior
        Button logoutButton = new Button("Cerrar Sesión", e -> {
            SesionUsuario.cerrarSesion();
            getUI().ifPresent(ui -> ui.navigate("login"));
        });
        logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout header = new HorizontalLayout(toggle, logo, logoutButton);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo); // Hace que el logo ocupe el espacio intermedio
        header.setWidthFull();
        header.getStyle().set("padding", "0 var(--lumo-space-m)");

        addToNavbar(header);
    }

    private void createDrawer() {
        // Enlaces de navegación hacia tus vistas protegidas
        RouterLink homeLink = new RouterLink("Inicio", HomeView.class);
        RouterLink usuariosLink = new RouterLink("Usuarios", UsuarioGridView.class);

        VerticalLayout drawerLayout = new VerticalLayout(homeLink, usuariosLink);
        drawerLayout.setPadding(true);

        addToDrawer(drawerLayout);
    }
}