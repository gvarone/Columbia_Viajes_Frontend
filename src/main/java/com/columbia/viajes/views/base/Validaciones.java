package com.columbia.viajes.views.base;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import java.math.BigDecimal;
import java.util.List;

/**
 * Validaciones comunes de formularios. Replican las del backend (@NotBlank, @Size, @Email)
 * para avisar antes de llamar a la API. Si el campo no es válido lo marca y agrega el error.
 */
public final class Validaciones {

    private Validaciones() {}

    /** Largo entre el mínimo y el máximo configurados en el campo (setMinLength/setMaxLength). */
    public static void largo(TextField campo, List<String> errores) {
        int largo = campo.getValue().trim().length();
        if (largo < campo.getMinLength() || largo > campo.getMaxLength()) {
            campo.setInvalid(true);
            errores.add(campo.getLabel() + ": entre " + campo.getMinLength()
                    + " y " + campo.getMaxLength() + " caracteres.");
        }
    }

    /** Email obligatorio y con formato válido (el EmailField valida el formato). */
    public static void email(EmailField campo, List<String> errores) {
        if (campo.getValue().trim().isEmpty() || campo.isInvalid()) {
            campo.setInvalid(true);
            errores.add(campo.getLabel() + ": ingresá un email válido.");
        }
    }

    /** Opción obligatoria en un desplegable (@NotNull sobre un ID). */
    public static void seleccionado(ComboBox<?> campo, List<String> errores) {
        if (campo.isEmpty()) {
            campo.setInvalid(true);
            errores.add(campo.getLabel() + ": elegí una opción.");
        }
    }

    /** Número entero obligatorio y no menor al mínimo configurado en el campo (setMin). */
    public static void entero(IntegerField campo, List<String> errores) {
        Integer valor = campo.getValue();
        if (valor == null || valor < campo.getMin()) {
            campo.setInvalid(true);
            errores.add(campo.getLabel() + ": ingresá un número mayor o igual a " + campo.getMin() + ".");
        }
    }

    /** Fecha y hora obligatoria (@NotNull). El DateTimePicker marca inválido un texto mal escrito. */
    public static void fechaHora(DateTimePicker campo, List<String> errores) {
        if (campo.isEmpty() || campo.isInvalid()) {
            campo.setInvalid(true);
            errores.add(campo.getLabel() + ": ingresá fecha y hora.");
        }
    }

    /** Fecha obligatoria (@NotNull). El DatePicker marca inválido un texto mal escrito. */
    public static void fecha(DatePicker campo, List<String> errores) {
        if (campo.isEmpty() || campo.isInvalid()) {
            campo.setInvalid(true);
            errores.add(campo.getLabel() + ": ingresá una fecha.");
        }
    }

    /** Monto obligatorio y no negativo (@NotNull + @DecimalMin("0.0")). */
    public static void montoNoNegativo(BigDecimalField campo, List<String> errores) {
        BigDecimal valor = campo.getValue();
        if (valor == null || valor.signum() < 0) {
            campo.setInvalid(true);
            errores.add(campo.getLabel() + ": ingresá un monto mayor o igual a 0.");
        }
    }

    /** Texto opcional con largo máximo (@Size(max) sin @NotBlank). */
    public static void largoMaximo(TextArea campo, List<String> errores) {
        if (campo.getValue().trim().length() > campo.getMaxLength()) {
            campo.setInvalid(true);
            errores.add(campo.getLabel() + ": hasta " + campo.getMaxLength() + " caracteres.");
        }
    }

    /**
     * Contraseña obligatoria de 8 a 64 caracteres y repetida igual. No se recorta:
     * los espacios forman parte de la contraseña.
     */
    public static void contrasenia(PasswordField campo, PasswordField repetir, List<String> errores) {
        String clave = campo.getValue();
        if (clave.isBlank() || clave.length() < 8 || clave.length() > 64) {
            campo.setInvalid(true);
            errores.add(campo.getLabel() + ": entre 8 y 64 caracteres.");
        } else if (!clave.equals(repetir.getValue())) {
            repetir.setInvalid(true);
            errores.add("Las contraseñas no coinciden.");
        }
    }
}
