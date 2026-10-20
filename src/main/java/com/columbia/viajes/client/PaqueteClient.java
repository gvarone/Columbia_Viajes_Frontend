package com.columbia.viajes.client;

import com.columbia.viajes.dto.response.PaqueteResponse;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PaqueteClient {

    private final RestClient restClient;

    public PaqueteClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<PaqueteResponse> listar() {
        return restClient.get()
                .uri("/paquetes")
                .retrieve()
                .body(new ParameterizedTypeReference<List<PaqueteResponse>>() {});
    }
}
