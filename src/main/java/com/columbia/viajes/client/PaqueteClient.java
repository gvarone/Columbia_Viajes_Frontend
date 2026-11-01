package com.columbia.viajes.client;

import com.columbia.viajes.dto.response.PaqueteResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PaqueteClient {

    private final RestClient restClient;

    public PaqueteClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<PaqueteResponse> listar() {
        return listar(null, null, null);
    }

    /** Filtros opcionales (null = sin filtrar), los aplica el backend. */
    public List<PaqueteResponse> listar(String nombre, Integer idCiudad, Integer idHotel) {
        String nombreFiltro = nombre == null || nombre.isBlank() ? null : nombre.trim();
        return restClient.get()
                .uri(uri -> uri.path("/paquetes")
                        .queryParamIfPresent("nombre", Optional.ofNullable(nombreFiltro))
                        .queryParamIfPresent("idCiudad", Optional.ofNullable(idCiudad))
                        .queryParamIfPresent("idHotel", Optional.ofNullable(idHotel))
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<PaqueteResponse>>() {});
    }

    /** Imagen PNG del paquete. El backend responde 404 si no tiene. */
    public byte[] imagen(Integer id) {
        return restClient.get()
                .uri("/paquetes/{id}/imagen", id)
                .accept(MediaType.IMAGE_PNG)
                .retrieve()
                .body(byte[].class);
    }
}
