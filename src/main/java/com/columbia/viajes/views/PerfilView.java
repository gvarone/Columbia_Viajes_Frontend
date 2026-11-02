package com.columbia.viajes.views;

import com.columbia.viajes.client.SucursalClient;
import com.columbia.viajes.client.UsuarioClient;
import com.columbia.viajes.dto.response.MiPerfilResponse;
import com.columbia.viajes.dto.response.SucursalResponse;
import com.columbia.viajes.dto.response.TuristaResponse;
import com.columbia.viajes.dto.response.UsuarioResponse;
import com.columbia.viajes.views.base.Formatos;
import com.columbia.viajes.views.base.MainLayout;
import com.columbia.viajes.views.base.Notificaciones;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.web.client.RestClientException;

/**
 * Datos del usuario logueado (GET /usuarios/me). Si es Cliente también muestra
 * sus datos de turista. Desde acá se cambia la contraseña.
 */
@Route(value = "perfil", layout = MainLayout.class)
@PageTitle("Mi perfil | Columbia Viajes")
@PermitAll
public class PerfilView extends VerticalLayout implements BeforeEnterObserver {

    private final UsuarioClient usuarioClient;
    private final SucursalClient sucursalClient;

    private final FormLayout datosCuenta = new FormLayout();
    private final H3 tituloTurista = new H3("Datos personales");
    private final FormLayout datosTurista = new FormLayout();

    public PerfilView(UsuarioClient usuarioClient, SucursalClient sucursalClient,
                      CambiarContraseniaDialog cambiarContraseniaDialog) {
        this.usuarioClient = usuarioClient;
        this.sucursalClient = sucursalClient;

        Button cambiarContrasenia = new Button("Cambiar contraseña", e -> cambiarContraseniaDialog.open());

        add(new H2("Mi perfil"), new H3("Cuenta"), datosCuenta, tituloTurista, datosTurista, cambiarContrasenia);
        setMaxWidth("800px");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            MiPerfilResponse perfil = usuarioClient.miPerfil();
            mostrarCuenta(perfil.usuario());
            mostrarTurista(perfil.turista());
        } catch (RestClientException e) {
            Notificaciones.error("No se pudo cargar tu perfil", e);
        }
    }

    private void mostrarCuenta(UsuarioResponse usuario) {
        datosCuenta.removeAll();
        datosCuenta.add(dato("Usuario", usuario.nombre()),
                dato("Rol", usuario.rolNombre()),
                dato("Cuenta creada", Formatos.fechaHora(usuario.fechaCreacion())));
        if (usuario.sucursalId() != null) {
            datosCuenta.add(dato("Sucursal", direccionSucursal(usuario.sucursalId())));
        }
    }

    /** turista es null si el usuario no es Cliente: en ese caso se oculta la sección. */
    private void mostrarTurista(TuristaResponse turista) {
        datosTurista.removeAll();
        tituloTurista.setVisible(turista != null);
        datosTurista.setVisible(turista != null);
        if (turista != null) {
            datosTurista.add(dato("Nombre", turista.nombre()),
                    dato("Apellidos", turista.apellidos()),
                    dato("Dirección", turista.direccion()),
                    dato("Email", turista.email()),
                    dato("Teléfono", turista.telefono()));
        }
    }

    private String direccionSucursal(Integer idSucursal) {
        return sucursalClient.listar().stream()
                .filter(s -> s.id().equals(idSucursal))
                .map(SucursalResponse::direccion)
                .findFirst()
                .orElse("Sucursal " + idSucursal);
    }

    private static TextField dato(String etiqueta, String valor) {
        TextField campo = new TextField(etiqueta);
        campo.setValue(valor == null ? "" : valor);
        campo.setReadOnly(true);
        return campo;
    }
}
