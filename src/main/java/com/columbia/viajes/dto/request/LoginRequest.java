package com.columbia.viajes.dto.request;

public record LoginRequest(
    String nombre,
    String contrasenia
) {}