package com.columbia.viajes.client;

import com.columbia.viajes.dto.response.Identificable;
import java.util.List;

/**
 * Operaciones de ABM sobre un recurso del backend.
 * T: DTO de respuesta, R: DTO de request (alta y edición usan el mismo).
 * La implementan los clientes de las entidades que tienen ABM, y la usan
 * AbmGridView y AbmDialog sin conocer la entidad concreta.
 */
public interface ClienteAbm<T extends Identificable, R> {

    List<T> listar();

    T crear(R request);

    T actualizar(Integer id, R request);

    void eliminar(Integer id);
}
