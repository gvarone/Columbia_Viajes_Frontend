package com.columbia.viajes.views;

import com.columbia.viajes.dto.request.RegistroClienteRequest;
import com.columbia.viajes.dto.request.TuristaRequest;
import com.columbia.viajes.dto.response.TuristaResponse;
import com.columbia.viajes.views.base.Validaciones;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import java.util.ArrayList;
import java.util.List;

/**
 * Campos de un turista y de su cuenta Cliente (todo turista tiene cuenta).
 * Lo comparten el alta y la edición de TuristaDialog y el registro público (RegistroView).
 * En la edición se usa sin los campos de la cuenta (el backend solo edita los datos personales).
 * Las validaciones replican las de RegistroClienteRequest y TuristaRequest del backend.
 */
public class TuristaForm extends VerticalLayout {

    private final TextField nombre = campo("Nombre", 2, 100);
    private final TextField apellidos = campo("Apellidos", 2, 100);
    private final TextField direccion = campo("Dirección", 5, 150);
    private final TextField telefono = campo("Teléfono", 7, 20);
    private final EmailField email = new EmailField("Email");
    private final TextField nombreUsuario = campo("Nombre de usuario", 2, 100);
    private final PasswordField contrasenia = new PasswordField("Contraseña");
    private final PasswordField repetirContrasenia = new PasswordField("Repetir contraseña");

    private final boolean conCuenta;

    /** Formulario completo: datos personales + cuenta. ayudaContrasenia puede ser null. */
    public TuristaForm(String ayudaContrasenia) {
        this(true, ayudaContrasenia);
    }

    /**
     * @param conCuenta        false para mostrar solo los datos personales (edición)
     * @param ayudaContrasenia texto bajo el campo de contraseña (null para no mostrar ninguno)
     */
    public TuristaForm(boolean conCuenta, String ayudaContrasenia) {
        this.conCuenta = conCuenta;
        setPadding(false);

        email.setRequired(true);
        email.setMinLength(5);
        email.setMaxLength(120);

        contrasenia.setRequired(true);
        contrasenia.setMinLength(8);
        contrasenia.setMaxLength(64);
        contrasenia.setHelperText(ayudaContrasenia);
        repetirContrasenia.setRequired(true);

        add(new FormLayout(nombre, apellidos, direccion, telefono, email));
        if (conCuenta) {
            add(new H4("Cuenta para iniciar sesión"),
                    new FormLayout(nombreUsuario, contrasenia, repetirContrasenia));
        }
    }

    /** Completa los datos personales con un turista existente (edición). */
    public void cargar(TuristaResponse turista) {
        nombre.setValue(turista.nombre());
        apellidos.setValue(turista.apellidos());
        direccion.setValue(turista.direccion());
        telefono.setValue(turista.telefono());
        email.setValue(turista.email());
    }

    /** Devuelve los errores encontrados (vacío si todo está bien) y marca los campos inválidos. */
    public List<String> validar() {
        List<String> errores = new ArrayList<>();
        for (TextField campo : List.of(nombre, apellidos, direccion, telefono)) {
            Validaciones.largo(campo, errores);
        }
        Validaciones.email(email, errores);
        if (conCuenta) {
            Validaciones.largo(nombreUsuario, errores);
            Validaciones.contrasenia(contrasenia, repetirContrasenia, errores);
        }
        return errores;
    }

    /**
     * Arma el request con los valores cargados. Llamar después de validar().
     * Sin cuenta (edición), usuario y contraseña van null.
     */
    public RegistroClienteRequest armarRequest() {
        TuristaRequest datos = new TuristaRequest(
                nombre.getValue().trim(), apellidos.getValue().trim(), direccion.getValue().trim(),
                email.getValue().trim(), telefono.getValue().trim());
        if (!conCuenta) {
            return new RegistroClienteRequest(null, null, datos);
        }
        return new RegistroClienteRequest(nombreUsuario.getValue().trim(), contrasenia.getValue(), datos);
    }

    private static TextField campo(String etiqueta, int min, int max) {
        TextField campo = new TextField(etiqueta);
        campo.setRequired(true);
        campo.setMinLength(min);
        campo.setMaxLength(max);
        return campo;
    }
}
