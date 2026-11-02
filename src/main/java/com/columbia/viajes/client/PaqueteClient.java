package com.columbia.viajes.client;

import com.columbia.viajes.dto.request.PaqueteRequest;
import com.columbia.viajes.dto.response.PaqueteResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PaqueteClient implements ClienteAbm<PaqueteResponse, PaqueteRequest> {

    private final RestClient restClient;

    public PaqueteClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public List<PaqueteResponse> listar() {
        return listar(null, null, null);
    }

    /** Filtros opcionales (null = sin filtrar), los aplica el backend. */
    public List<PaqueteResponse> listar(String nombre, Integer idCiudad, Integer idHotel) {
        String nombreFiltro = nombre == null || nombre.isBlank() ? null : nombre.trim();
        return restClient.get()
                .uri(uri -> uri.path("/paquetes")
                        .queryParamIfPresent("nombre", Optional.ofNullable(nombreFiltro))
                        .queryParamIfPresent("idCiudad", Optional.ofNullable(idCiudad))
                        .queryParamIfPresent("idHotel", Optional.ofNullable(idHotel))
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<PaqueteResponse>>() {});
    }

    /** Imagen PNG del paquete. El backend responde 404 si no tiene. */
    public byte[] imagen(Integer id) {
        return restClient.get()
                .uri("/paquetes/{id}/imagen", id)
                .accept(MediaType.IMAGE_PNG)
                .retrieve()
                .body(byte[].class);
    }

    /**
     * El alta es multipart (parte "paquete" en JSON + "imagen" opcional). Acá se manda
     * solo el JSON; la imagen se sube aparte con actualizarImagen (igual que en la edición).
     */
    @Override
    public PaqueteResponse crear(PaqueteRequest request) {
        MultipartBodyBuilder partes = new MultipartBodyBuilder();
        partes.part("paquete", request).contentType(MediaType.APPLICATION_JSON);
        return restClient.post()
                .uri("/paquetes")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(partes.build())
                .retrieve()
                .body(PaqueteResponse.class);
    }

    @Override
    public PaqueteResponse actualizar(Integer id, PaqueteRequest request) {
        return restClient.put()
                .uri("/paquetes/{id}", id)
                .body(request)
                .retrieve()
                .body(PaqueteResponse.class);
    }

    /** Reemplaza la imagen del paquete. El backend acepta JPEG o PNG y la guarda como PNG. */
    public PaqueteResponse actualizarImagen(Integer id, String nombreArchivo, byte[] contenido) {
        MultipartBodyBuilder partes = new MultipartBodyBuilder();
        partes.part("imagen", new ByteArrayResource(contenido)).filename(nombreArchivo);
        return restClient.put()
                .uri("/paquetes/{id}/imagen", id)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(partes.build())
                .retrieve()
                .body(PaqueteResponse.class);
    }

    @Override
    public void eliminar(Integer id) {
        restClient.delete()
                .uri("/paquetes/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}
