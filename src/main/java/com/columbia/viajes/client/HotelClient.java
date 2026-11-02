package com.columbia.viajes.client;

import com.columbia.viajes.dto.request.HotelRequest;
import com.columbia.viajes.dto.response.HotelResponse;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class HotelClient implements ClienteAbm<HotelResponse, HotelRequest> {

    private final RestClient restClient;

    public HotelClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
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

    @Override
    public HotelResponse crear(HotelRequest request) {
        return restClient.post()
                .uri("/hoteles")
                .body(request)
                .retrieve()
                .body(HotelResponse.class);
    }

    @Override
    public HotelResponse actualizar(Integer id, HotelRequest request) {
        return restClient.put()
                .uri("/hoteles/{id}", id)
                .body(request)
                .retrieve()
                .body(HotelResponse.class);
    }

    @Override
    public void eliminar(Integer id) {
        restClient.delete()
                .uri("/hoteles/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}
