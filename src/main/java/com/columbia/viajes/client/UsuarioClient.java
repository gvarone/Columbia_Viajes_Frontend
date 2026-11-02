package com.columbia.viajes.client;

import com.columbia.viajes.dto.request.UsuarioActualizacionRequest;
import com.columbia.viajes.dto.request.UsuarioRequest;
import com.columbia.viajes.dto.response.MiPerfilResponse;
import com.columbia.viajes.dto.response.UsuarioResponse;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class UsuarioClient implements ClienteAbm<UsuarioResponse, UsuarioRequest> {

    private final RestClient restClient;

    public UsuarioClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
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

    /** Datos del usuario logueado (incluye su sucursal si es Vendedor). */
    public MiPerfilResponse miPerfil() {
        return restClient.get()
                .uri("/usuarios/me")
                .retrieve()
                .body(MiPerfilResponse.class);
    }

    @Override
    public UsuarioResponse crear(UsuarioRequest request) {
        return restClient.post()
                .uri("/usuarios")
                .body(request)
                .retrieve()
                .body(UsuarioResponse.class);
    }

    /**
     * El backend edita con otro DTO (UsuarioActualizacionRequest: sin contraseña ni turista).
     * Para que las vistas usen un solo tipo de request (ClienteAbm), la adaptación se hace acá.
     */
    @Override
    public UsuarioResponse actualizar(Integer id, UsuarioRequest request) {
        UsuarioActualizacionRequest cambios = new UsuarioActualizacionRequest(
                request.nombre(), request.idRol(), request.idSucursal());
        return restClient.put()
                .uri("/usuarios/{id}", id)
                .body(cambios)
                .retrieve()
                .body(UsuarioResponse.class);
    }

    @Override
    public void eliminar(Integer id) {
        restClient.delete()
                .uri("/usuarios/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}
