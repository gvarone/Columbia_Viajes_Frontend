package com.columbia.viajes.views;

import com.columbia.viajes.security.SesionUsuario;
import com.columbia.viajes.views.base.MainLayout;
import com.columbia.viajes.views.base.ProtectedView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
public class HomeView extends ProtectedView {

    public HomeView() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();

        H1 titulo = new H1("Bienvenido a Columbia Viajes");
        
        // Obtenemos el nombre del usuario logueado desde la sesión
        String nombreUsuario = SesionUsuario.getUsuario() != null ? SesionUsuario.getUsuario().nombre() : "Usuario";
        Paragraph saludo = new Paragraph("Sesión iniciada como: " + nombreUsuario);

        add(titulo, saludo);
    }
}