package com.columbia.viajes.client;

import com.columbia.viajes.dto.request.VueloRequest;
import com.columbia.viajes.dto.response.VueloResponse;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class VueloClient implements ClienteAbm<VueloResponse, VueloRequest> {

    private final RestClient restClient;

    public VueloClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<VueloResponse> listar() {
        return restClient.get()
                .uri("/vuelos")
                .retrieve()
                .body(new ParameterizedTypeReference<List<VueloResponse>>() {});
    }

    public VueloResponse obtener(Integer id) {
        return restClient.get()
                .uri("/vuelos/{id}", id)
                .retrieve()
                .body(VueloResponse.class);
    }

    @Override
    public VueloResponse crear(VueloRequest request) {
        return restClient.post()
                .uri("/vuelos")
                .body(request)
                .retrieve()
                .body(VueloResponse.class);
    }

    @Override
    public VueloResponse actualizar(Integer id, VueloRequest request) {
        return restClient.put()
                .uri("/vuelos/{id}", id)
                .body(request)
                .retrieve()
                .body(VueloResponse.class);
    }

    @Override
    public void eliminar(Integer id) {
        restClient.delete()
                .uri("/vuelos/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}
