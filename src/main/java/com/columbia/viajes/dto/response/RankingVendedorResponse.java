package com.columbia.viajes.dto.response;

import java.math.BigDecimal;

/** Total vendido por un vendedor. El backend lo devuelve ordenado de mayor a menor. */
public record RankingVendedorResponse(
        Integer vendedorId,
        String nombre,
        BigDecimal totalVendido,
        Integer sucursalId
) {}
