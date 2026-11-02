package com.columbia.viajes.client;

import com.columbia.viajes.dto.request.RegistroClienteRequest;
import com.columbia.viajes.dto.response.TuristaResponse;
import com.columbia.viajes.dto.response.UsuarioResponse;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Turistas. Todo turista tiene una cuenta Cliente con su mismo ID, por eso el alta
 * y la baja pasan por la cuenta. El request de las vistas es RegistroClienteRequest
 * (cuenta + datos personales); en la edición solo se usan los datos personales.
 */
@Service
public class TuristaClient implements ClienteAbm<TuristaResponse, RegistroClienteRequest> {

    private final RestClient restClient;
    private final AuthClient authClient;

    public TuristaClient(RestClient restClient, AuthClient authClient) {
        this.restClient = restClient;
        this.authClient = authClient;
    }

    @Override
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
    @Override
    public TuristaResponse crear(RegistroClienteRequest request) {
        UsuarioResponse cuenta = authClient.registrar(request);
        return obtener(cuenta.id());
    }

    /** Edita solo los datos personales (PUT /turistas/{id} recibe un TuristaRequest). */
    @Override
    public TuristaResponse actualizar(Integer id, RegistroClienteRequest request) {
        return restClient.put()
                .uri("/turistas/{id}", id)
                .body(request.turista())
                .retrieve()
                .body(TuristaResponse.class);
    }

    /**
     * Borra la cuenta Cliente (DELETE /usuarios/{id}); el turista se borra en cascada.
     * No se usa DELETE /turistas/{id} porque deja la cuenta sin perfil.
     */
    @Override
    public void eliminar(Integer id) {
        restClient.delete()
                .uri("/usuarios/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}
