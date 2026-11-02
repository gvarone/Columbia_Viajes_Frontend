package com.columbia.viajes.views.base;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
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
}
