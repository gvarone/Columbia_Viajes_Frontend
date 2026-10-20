
package com.columbia.viajes.dto.response;

import com.columbia.viajes.dto.enums.ClaseVuelo;
import com.columbia.viajes.dto.enums.RegimenHospedaje;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservaResponse(
        Integer id,
        Integer paqueteId,
        Integer turistaId,
        ClaseVuelo claseVuelo,
        LocalDateTime fechaReserva,
        RegimenHospedaje regimenHospedaje,
        BigDecimal precioReserva,
        Integer vendedorId
) {}
