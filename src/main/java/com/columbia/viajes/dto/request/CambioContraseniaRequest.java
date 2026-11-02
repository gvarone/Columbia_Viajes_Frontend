package com.columbia.viajes.dto.request;

public record CambioContraseniaRequest(
        String contraseniaActual,
        String contraseniaNueva
) {}
