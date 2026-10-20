package com.columbia.viajes.client;

import com.columbia.viajes.dto.request.ReservaRequest;
import com.columbia.viajes.dto.response.ReservaResponse;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ReservaClient {

    private final RestClient restClient;

    public ReservaClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<ReservaResponse> listar() {
        return restClient.get()
                .uri("/paquetes-reservas")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ReservaResponse>>() {});
    }

    public ReservaResponse crear(ReservaRequest request) {
        return restClient.post()
                .uri("/paquetes-reservas")
                .body(request)
                .retrieve()
                .body(ReservaResponse.class);
    }

    public void eliminar(Integer id) {
        restClient.delete()
                .uri("/paquetes-reservas/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}
