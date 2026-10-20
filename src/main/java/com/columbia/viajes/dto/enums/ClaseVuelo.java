package com.columbia.viajes.dto.enums;

public enum ClaseVuelo {
    TURISTA("Turista"),
    PRIMERA("Primera");

    private final String etiqueta;

    ClaseVuelo(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
