package com.columbia.viajes.dto;

public record LoginResponse(String mensaje, String nombre, Integer idRol, String token) {}