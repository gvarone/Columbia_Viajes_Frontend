package com.columbia.viajes.client;

import com.columbia.viajes.dto.request.SucursalRequest;
import com.columbia.viajes.dto.response.SucursalResponse;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class SucursalClient implements ClienteAbm<SucursalResponse, SucursalRequest> {

    private final RestClient restClient;

    public SucursalClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<SucursalResponse> listar() {
        return restClient.get()
                .uri("/sucursales")
                .retrieve()
                .body(new ParameterizedTypeReference<List<SucursalResponse>>() {});
    }

    @Override
    public SucursalResponse crear(SucursalRequest request) {
        return restClient.post()
                .uri("/sucursales")
                .body(request)
                .retrieve()
                .body(SucursalResponse.class);
    }

    @Override
    public SucursalResponse actualizar(Integer id, SucursalRequest request) {
        return restClient.put()
                .uri("/sucursales/{id}", id)
                .body(request)
                .retrieve()
                .body(SucursalResponse.class);
    }

    @Override
    public void eliminar(Integer id) {
        restClient.delete()
                .uri("/sucursales/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}
