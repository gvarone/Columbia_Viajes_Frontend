package com.columbia.viajes.dto.enums;

public enum RegimenHospedaje {
    MEDIA_PENSION("Media pensión"),
    PENSION_COMPLETA("Pensión completa");

    private final String etiqueta;

    RegimenHospedaje(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
