package com.columbia.viajes.client;

import com.columbia.viajes.dto.response.HotelResponse;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class HotelClient {

    private final RestClient restClient;

    public HotelClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<HotelResponse> listar() {
        return restClient.get()
                .uri("/hoteles")
                .retrieve()
                .body(new ParameterizedTypeReference<List<HotelResponse>>() {});
    }

    public HotelResponse obtener(Integer id) {
        return restClient.get()
                .uri("/hoteles/{id}", id)
                .retrieve()
                .body(HotelResponse.class);
    }
}
