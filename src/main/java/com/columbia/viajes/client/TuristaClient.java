package com.columbia.viajes.client;

import com.columbia.viajes.dto.request.TuristaRequest;
import com.columbia.viajes.dto.response.TuristaResponse;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class TuristaClient {

    private final RestClient restClient;

    public TuristaClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<TuristaResponse> listar() {
        return restClient.get()
                .uri("/turistas")
                .retrieve()
                .body(new ParameterizedTypeReference<List<TuristaResponse>>() {});
    }

    public TuristaResponse crear(TuristaRequest request) {
        return restClient.post()
                .uri("/turistas")
                .body(request)
                .retrieve()
                .body(TuristaResponse.class);
    }
}
