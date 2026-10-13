package com.columbia.viajes.client;

import com.columbia.viajes.dto.response.SucursalResponse;
import com.columbia.viajes.security.SesionUsuario;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class SucursalClient {

    private final RestClient restClient;

    public SucursalClient(@Value("${backend.url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public List<SucursalResponse> listar() {
        // 1. Obtenemos el JWT activo de la sesión de Vaadin
        String token = SesionUsuario.getToken();

        if (token == null) {
            throw new IllegalStateException("No hay un token activo en la sesión.");
        }

        // 2. Ejecutamos la petición añadiendo el header de autorización
        return restClient.get()
                .uri("/sucursales")
                .header("Authorization", "Bearer " + token) // <--- Aquí inyectamos el JWT
                .retrieve()
                .body(new ParameterizedTypeReference<List<SucursalResponse>>() {});
    }
}