package com.columbia.viajes.views;

import com.columbia.viajes.dto.request.RegistroClienteRequest;
import com.columbia.viajes.dto.request.TuristaRequest;
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
 * Lo comparten el alta del Vendedor (TuristaDialog) y el registro público (RegistroView).
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

    /** ayudaContrasenia: texto bajo el campo de contraseña (null para no mostrar ninguno). */
    public TuristaForm(String ayudaContrasenia) {
        setPadding(false);

        email.setRequired(true);
        email.setMinLength(5);
        email.setMaxLength(120);

        contrasenia.setRequired(true);
        contrasenia.setMinLength(8);
        contrasenia.setMaxLength(64);
        contrasenia.setHelperText(ayudaContrasenia);
        repetirContrasenia.setRequired(true);

        add(new FormLayout(nombre, apellidos, direccion, telefono, email),
                new H4("Cuenta para iniciar sesión"),
                new FormLayout(nombreUsuario, contrasenia, repetirContrasenia));
    }

    /** Devuelve los errores encontrados (vacío si todo está bien) y marca los campos inválidos. */
    public List<String> validar() {
        List<String> errores = new ArrayList<>();
        for (TextField campo : List.of(nombre, apellidos, direccion, telefono, nombreUsuario)) {
            Validaciones.largo(campo, errores);
        }
        Validaciones.email(email, errores);
        // La contraseña no se recorta: los espacios forman parte de ella
        String clave = contrasenia.getValue();
        if (clave.isBlank() || clave.length() < 8 || clave.length() > 64) {
            contrasenia.setInvalid(true);
            errores.add("Contraseña: entre 8 y 64 caracteres.");
        } else if (!clave.equals(repetirContrasenia.getValue())) {
            repetirContrasenia.setInvalid(true);
            errores.add("Las contraseñas no coinciden.");
        }
        return errores;
    }

    /** Arma el request con los valores cargados. Llamar después de validar(). */
    public RegistroClienteRequest armarRequest() {
        TuristaRequest datos = new TuristaRequest(
                nombre.getValue().trim(), apellidos.getValue().trim(), direccion.getValue().trim(),
                email.getValue().trim(), telefono.getValue().trim());
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
