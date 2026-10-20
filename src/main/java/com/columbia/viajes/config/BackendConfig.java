package com.columbia.viajes.config;

import com.columbia.viajes.security.SesionExpiradaException;
import com.columbia.viajes.security.SesionUsuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

/**
 * RestClient único para hablar con el backend.
 * - Agrega "Authorization: Bearer <jwt>" del usuario logueado (si el request no trae uno).
 * - Si el backend responde 401 a un request autenticado, lanza SesionExpiradaException.
 */
@Configuration
public class BackendConfig {

    @Bean
    RestClient backendRestClient(@Value("${backend.url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor((request, body, execution) -> {
                    String token = SesionUsuario.getToken();
                    if (token != null && !request.getHeaders().containsHeader(HttpHeaders.AUTHORIZATION)) {
                        request.getHeaders().setBearerAuth(token);
                    }
                    return execution.execute(request, body);
                })
                .defaultStatusHandler(
                        status -> status.isSameCodeAs(HttpStatus.UNAUTHORIZED),
                        (request, response) -> {
                            if (request.getHeaders().containsHeader(HttpHeaders.AUTHORIZATION)) {
                                throw new SesionExpiradaException();
                            }
                            // Sin token (ej. login con credenciales incorrectas): error HTTP normal
                            throw HttpClientErrorException.create(response.getStatusCode(),
                                    response.getStatusText(), response.getHeaders(),
                                    response.getBody().readAllBytes(), null);
                        })
                .build();
    }
}
