package com.columbia.viajes.dto.request;

import com.columbia.viajes.dto.enums.ClaseVuelo;
import com.columbia.viajes.dto.enums.RegimenHospedaje;

/** Equivale a PaqueteReservaRequest del backend. idVendedor: null si reserva un Vendedor (lo toma del token). */
public record ReservaRequest(
        Integer idPaquete,
        Integer idTurista,
        ClaseVuelo claseVuelo,
        RegimenHospedaje regimenHospedaje,
        Integer idVendedor
) {}
