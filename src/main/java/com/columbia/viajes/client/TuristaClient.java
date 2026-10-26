package com.columbia.viajes.client;

import com.columbia.viajes.dto.request.RegistroClienteRequest;
import com.columbia.viajes.dto.response.TuristaResponse;
import com.columbia.viajes.dto.response.UsuarioResponse;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class TuristaClient {

    private final RestClient restClient;
    private final AuthClient authClient;

    public TuristaClient(RestClient restClient, AuthClient authClient) {
        this.restClient = restClient;
        this.authClient = authClient;
    }

    public List<TuristaResponse> listar() {
        return restClient.get()
                .uri("/turistas")
                .retrieve()
                .body(new ParameterizedTypeReference<List<TuristaResponse>>() {});
    }

    public TuristaResponse obtener(Integer id) {
        return restClient.get()
                .uri("/turistas/{id}", id)
                .retrieve()
                .body(TuristaResponse.class);
    }

    /**
     * Alta de turista junto con su cuenta Cliente (POST /auth/registro).
     * El backend devuelve la cuenta creada; como el turista comparte su ID, se
     * consulta después para devolver sus datos. Requiere sesión iniciada.
     */
    public TuristaResponse crear(RegistroClienteRequest request) {
        UsuarioResponse cuenta = authClient.registrar(request);
        return obtener(cuenta.id());
    }
}
