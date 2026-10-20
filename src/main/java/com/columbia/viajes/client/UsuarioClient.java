package com.columbia.viajes.client;

import com.columbia.viajes.dto.response.UsuarioResponse;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class UsuarioClient {

    private final RestClient restClient;

    public UsuarioClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<UsuarioResponse> listar() {
        return restClient.get()
                .uri("/usuarios")
                .retrieve()
                .body(new ParameterizedTypeReference<List<UsuarioResponse>>() {});
    }

    public List<UsuarioResponse> listarVendedores() {
        return restClient.get()
                .uri("/usuarios/vendedores")
                .retrieve()
                .body(new ParameterizedTypeReference<List<UsuarioResponse>>() {});
    }
}
