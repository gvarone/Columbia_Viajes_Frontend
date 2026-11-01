package com.columbia.viajes.client;

import com.columbia.viajes.dto.response.CiudadResponse;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CiudadClient {

    private final RestClient restClient;

    public CiudadClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<CiudadResponse> listar() {
        return restClient.get()
                .uri("/ciudades")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CiudadResponse>>() {});
    }
}
