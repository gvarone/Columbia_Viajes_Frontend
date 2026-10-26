package com.columbia.viajes.dto.request;

public record LoginRequest(
    String identificador,
    String contrasenia
) {}