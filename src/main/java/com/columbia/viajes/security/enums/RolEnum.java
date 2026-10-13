package com.columbia.viajes.security.enums;

public enum RolEnum {
    ADMIN("Administrador"),
    VENDEDOR("Vendedor"),
    CLIENTE("Cliente"),
    DUENIO("Dueño");

    private final String nombreEnDb;

    RolEnum(String nombreEnDb) {
        this.nombreEnDb = nombreEnDb;
    }

    public String getNombreEnDb() {
        return nombreEnDb;
    }

    public static RolEnum desdeDb(String rolStr) {
        if (rolStr == null) return null;
        for (RolEnum r : RolEnum.values()) {
            if (r.nombreEnDb.equalsIgnoreCase(rolStr.trim())) {
                return r;
            }
        }
        return null;
    }
}