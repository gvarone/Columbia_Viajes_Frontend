package com.columbia.viajes.security;

/**
 * Nombres de rol para usar en las anotaciones de acceso (@RolesAllowed).
 * Coinciden con RolEnum.name(); la autoridad de Spring Security es "ROLE_" + nombre.
 */
public final class Roles {

    public static final String ADMIN = "ADMIN";
    public static final String VENDEDOR = "VENDEDOR";
    public static final String CLIENTE = "CLIENTE";
    public static final String DUENIO = "DUENIO";

    private Roles() {}
}
