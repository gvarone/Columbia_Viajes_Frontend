package com.columbia.viajes.views;

import com.columbia.viajes.client.TuristaClient;
import com.columbia.viajes.dto.response.TuristaResponse;
import com.columbia.viajes.views.base.Notificaciones;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.web.client.RestClientException;

/** Alta de turista con su cuenta Cliente, hecha por personal de la sucursal. */
public class TuristaDialog extends Dialog {

    private final TuristaForm formulario =
            new TuristaForm("El turista puede cambiarla después de iniciar sesión.");

    public TuristaDialog(TuristaClient client, Consumer<TuristaResponse> alCrear) {
        setHeaderTitle("Nuevo turista");
        add(formulario);

        Button guardar = new Button("Guardar", e -> {
            List<String> errores = formulario.validar();
            if (!errores.isEmpty()) {
                Notificaciones.error(String.join(" ", errores));
                return;
            }
            try {
                TuristaResponse creado = client.crear(formulario.armarRequest());
                Notificaciones.exito("Turista creado.");
                alCrear.accept(creado);
                close();
            } catch (RestClientException ex) {
                Notificaciones.error("No se pudo crear el turista", ex);
            }
        });
        guardar.addThemeVariants(ButtonVariant.PRIMARY);
        getFooter().add(new Button("Cancelar", e -> close()), guardar);
    }
}
