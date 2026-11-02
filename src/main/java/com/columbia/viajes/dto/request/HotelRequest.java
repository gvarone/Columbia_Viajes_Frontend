package com.columbia.viajes.dto.request;

public record HotelRequest(
        String nombre,
        String direccion,
        Integer idCiudad,
        String telefono,
        Integer plazasTotales
) {}
