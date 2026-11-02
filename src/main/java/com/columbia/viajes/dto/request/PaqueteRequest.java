package com.columbia.viajes.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaqueteRequest(
        String nombre,
        String descripcion,
        LocalDate fechaLlegadaHotel,
        LocalDate fechaPartidaHotel,
        BigDecimal precio,
        Integer idSucursal,
        Integer idVueloIda,
        Integer idVueloVuelta,
        Integer idHotel
) {}
