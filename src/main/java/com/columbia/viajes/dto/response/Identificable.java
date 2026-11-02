package com.columbia.viajes.dto.response;

/**
 * DTO de respuesta con ID. Los records que tienen un componente "Integer id"
 * ya cumplen la interfaz: solo hay que agregar "implements Identificable".
 */
public interface Identificable {

    Integer id();
}
