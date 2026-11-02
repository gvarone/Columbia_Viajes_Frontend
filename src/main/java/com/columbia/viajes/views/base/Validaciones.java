package com.columbia.viajes.views.base;

import com.vaadin.flow.component.textfield.EmailField;
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
}
