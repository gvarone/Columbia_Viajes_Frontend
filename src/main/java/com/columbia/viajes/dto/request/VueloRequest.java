package com.columbia.viajes.dto.request;

import java.time.LocalDateTime;

public record VueloRequest(
        Integer idCiudadOrigen,
        Integer idCiudadDestino,
        LocalDateTime fechaSalida,
        LocalDateTime fechaLlegada,
        Integer plazasTuristaTotales,
        Integer plazasPrimeraTotales
) {}
