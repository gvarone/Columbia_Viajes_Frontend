package com.columbia.viajes.client;

import com.columbia.viajes.dto.response.RolResponse;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class RolClient {

    private final RestClient restClient;

    public RolClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<RolResponse> listar() {
        return restClient.get()
                .uri("/roles")
                .retrieve()
                .body(new ParameterizedTypeReference<List<RolResponse>>() {});
    }
}
