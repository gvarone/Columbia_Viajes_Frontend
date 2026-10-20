package com.columbia.viajes.views.base;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Formato de fechas y montos para mostrar en pantalla. */
public final class Formatos {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Locale AR = Locale.of("es", "AR");

    private Formatos() {}

    public static String fecha(LocalDate fecha) {
        return fecha == null ? "" : FECHA.format(fecha);
    }

    public static String fechaHora(LocalDateTime fechaHora) {
        return fechaHora == null ? "" : FECHA_HORA.format(fechaHora);
    }

    public static String precio(BigDecimal monto) {
        return monto == null ? "" : NumberFormat.getCurrencyInstance(AR).format(monto);
    }
}
