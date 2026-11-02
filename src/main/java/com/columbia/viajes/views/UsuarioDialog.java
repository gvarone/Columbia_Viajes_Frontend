package com.columbia.viajes.views;

import com.columbia.viajes.client.ClienteAbm;
import com.columbia.viajes.dto.request.UsuarioRequest;
import com.columbia.viajes.dto.response.RolResponse;
import com.columbia.viajes.dto.response.SucursalResponse;
import com.columbia.viajes.dto.response.UsuarioResponse;
import com.columbia.viajes.security.enums.RolEnum;
import com.columbia.viajes.views.base.AbmDialog;
import com.columbia.viajes.views.base.Validaciones;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Alta de Vendedores y edición de Vendedores y Clientes (lo que el backend le permite al Admin).
 * - Alta: el rol es siempre Vendedor; pide contraseña y sucursal.
 * - Edición: el rol no se cambia (el backend no deja pasar entre Cliente y Vendedor) ni la
 *   contraseña (la cambia cada usuario desde "Mi perfil"). Los datos de turista de un Cliente
 *   no se editan acá.
 * Replica las validaciones de UsuarioRequest / UsuarioActualizacionRequest del backend.
 */
public class UsuarioDialog extends AbmDialog<UsuarioResponse, UsuarioRequest> {

    private final RolResponse rol;

    private final TextField nombre = new TextField("Nombre de usuario");
    private final TextField rolMostrado = new TextField("Rol");
    private final PasswordField contrasenia = new PasswordField("Contraseña");
    private final PasswordField repetirContrasenia = new PasswordField("Repetir contraseña");
    private final ComboBox<SucursalResponse> sucursal = new ComboBox<>("Sucursal");

    /** @param rol rol del usuario: Vendedor en un alta, el actual en una edición */
    public UsuarioDialog(ClienteAbm<UsuarioResponse, UsuarioRequest> client, RolResponse rol,
                         List<SucursalResponse> sucursales, UsuarioResponse usuario, Runnable alGuardar) {
        super(client, usuario, usuario == null ? "vendedor" : "usuario", alGuardar);
        this.rol = rol;

        nombre.setRequired(true);
        nombre.setMinLength(2);
        nombre.setMaxLength(100);
        rolMostrado.setValue(rol.nombre());
        rolMostrado.setReadOnly(true);
        contrasenia.setRequired(true);
        contrasenia.setMaxLength(64);
        contrasenia.setHelperText("Entre 8 y 64 caracteres. El vendedor puede cambiarla desde \"Mi perfil\".");
        repetirContrasenia.setRequired(true);
        sucursal.setRequired(true);
        sucursal.setItems(sucursales);
        sucursal.setItemLabelGenerator(SucursalResponse::direccion);

        FormLayout formulario = new FormLayout(nombre, rolMostrado);
        if (esEdicion()) {
            nombre.setValue(getItem().nombre());
            sucursales.stream()
                    .filter(s -> Objects.equals(s.id(), getItem().sucursalId()))
                    .findFirst()
                    .ifPresent(sucursal::setValue);
        } else {
            formulario.add(contrasenia, repetirContrasenia);
        }
        if (esVendedor()) {
            formulario.add(sucursal);
            formulario.setColspan(sucursal, 2);
        }
        formulario.setMaxWidth("600px");
        add(formulario);
    }

    /** Solo los Vendedores tienen sucursal (el backend la exige para ellos y la rechaza para el resto). */
    private boolean esVendedor() {
        return RolEnum.VENDEDOR.getNombreEnDb().equals(rol.nombre());
    }

    @Override
    protected List<String> validar() {
        List<String> errores = new ArrayList<>();
        Validaciones.largo(nombre, errores);
        if (!esEdicion()) {
            Validaciones.contrasenia(contrasenia, repetirContrasenia, errores);
        }
        if (esVendedor()) {
            Validaciones.seleccionado(sucursal, errores);
        }
        return errores;
    }

    /** En la edición la contraseña va null: UsuarioClient.actualizar no la envía. */
    @Override
    protected UsuarioRequest armarRequest() {
        return new UsuarioRequest(
                nombre.getValue().trim(),
                esEdicion() ? null : contrasenia.getValue(),
                rol.id(),
                esVendedor() ? sucursal.getValue().id() : null,
                null);
    }
}
