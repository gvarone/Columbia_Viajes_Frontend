package com.columbia.viajes.client;

import com.columbia.viajes.dto.request.LoginRequest;
import com.columbia.viajes.dto.response.LoginResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AuthClient {

    private final RestClient restClient;

    public AuthClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public LoginResponse login(LoginRequest request) {
        return restClient.post()
                .uri("/auth/login")
                .body(request)
                .retrieve()
                .body(LoginResponse.class);
    }

    /**
     * Invalida el JWT en el backend (incrementa la versión de autenticación del
     * usuario, por lo que cierra la sesión en todos sus dispositivos).
     * Se ignoran los errores: el logout local se hace igual.
     */
    public void logout(String token) {
        try {
            restClient.post()
                    .uri("/auth/logout")
                    .headers(h -> h.setBearerAuth(token))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RuntimeException e) {
            // Token ya vencido o backend caído: no impide cerrar la sesión del front
        }
    }
}
