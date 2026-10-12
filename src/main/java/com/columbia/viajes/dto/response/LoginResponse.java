package com.columbia.viajes.dto.response;

public record LoginResponse(
    String nombre, 
    String rolNombre, 
    String token
) {}
