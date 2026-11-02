package com.columbia.viajes.views.base;

import com.columbia.viajes.client.ClienteAbm;
import com.columbia.viajes.dto.response.Identificable;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import java.util.List;
import org.springframework.web.client.RestClientException;

/**
 * Base de los diálogos de alta y edición. Si recibe un item es edición; si es null, alta.
 * La subclase agrega sus campos en el constructor (y los completa si es edición),
 * valida y arma el request; esta clase decide si crear o actualizar y maneja los errores.
 *
 * T: DTO de respuesta, R: DTO de request.
 */
public abstract class AbmDialog<T extends Identificable, R> extends Dialog {

    private final ClienteAbm<T, R> client;
    private final T item;
    private final Runnable alGuardar;

    /** @param entidad nombre en singular y minúscula, para el título ("ciudad") */
    protected AbmDialog(ClienteAbm<T, R> client, T item, String entidad, Runnable alGuardar) {
        this.client = client;
        this.item = item;
        this.alGuardar = alGuardar;

        setHeaderTitle(esEdicion() ? "Editar " + entidad : "Alta de " + entidad);

        Button guardar = new Button("Guardar", e -> guardar());
        guardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getFooter().add(new Button("Cancelar", e -> close()), guardar);
    }

    /** Devuelve los errores encontrados (vacío si todo está bien) y marca los campos inválidos. */
    protected abstract List<String> validar();

    /** Arma el request con los valores cargados. Se llama solo si validar() no dio errores. */
    protected abstract R armarRequest();

    protected boolean esEdicion() {
        return item != null;
    }

    /** El registro que se está editando (null en un alta). */
    protected T getItem() {
        return item;
    }

    private void guardar() {
        List<String> errores = validar();
        if (!errores.isEmpty()) {
            Notificaciones.error(String.join(" ", errores));
            return;
        }
        try {
            if (esEdicion()) {
                client.actualizar(item.id(), armarRequest());
                Notificaciones.exito("Cambios guardados.");
            } else {
                client.crear(armarRequest());
                Notificaciones.exito("Alta registrada.");
            }
            alGuardar.run();
            close();
        } catch (RestClientException e) {
            Notificaciones.error("No se pudo guardar", e);
        }
    }
}
