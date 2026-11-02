package com.columbia.viajes.dto.request;

/** Alta de usuario (POST /usuarios). turista solo para Clientes; idSucursal solo para Vendedores. */
public record UsuarioRequest(
        String nombre,
        String contrasenia,
        Integer idRol,
        Integer idSucursal,
        TuristaRequest turista
) {}
