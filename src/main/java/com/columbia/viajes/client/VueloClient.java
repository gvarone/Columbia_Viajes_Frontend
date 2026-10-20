package com.columbia.viajes.client;

import com.columbia.viajes.dto.response.VueloResponse;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class VueloClient {

    private final RestClient restClient;

    public VueloClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<VueloResponse> listar() {
        return restClient.get()
                .uri("/vuelos")
                .retrieve()
                .body(new ParameterizedTypeReference<List<VueloResponse>>() {});
    }
}
