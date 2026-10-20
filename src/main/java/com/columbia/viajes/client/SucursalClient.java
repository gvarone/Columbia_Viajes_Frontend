package com.columbia.viajes.client;

import com.columbia.viajes.dto.response.SucursalResponse;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class SucursalClient {

    private final RestClient restClient;

    public SucursalClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<SucursalResponse> listar() {
        return restClient.get()
                .uri("/sucursales")
                .retrieve()
                .body(new ParameterizedTypeReference<List<SucursalResponse>>() {});
    }
}
