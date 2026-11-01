package com.columbia.viajes.dto.response;

/** Respuesta de GET /usuarios/me. turista es null si el usuario no es Cliente. */
public record MiPerfilResponse(
        UsuarioResponse usuario,
        TuristaResponse turista
) {}
