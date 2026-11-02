package com.columbia.viajes.views;

import com.columbia.viajes.client.ClienteAbm;
import com.columbia.viajes.dto.request.RegistroClienteRequest;
import com.columbia.viajes.dto.response.TuristaResponse;
import com.columbia.viajes.views.base.AbmDialog;
import java.util.List;
import java.util.function.Consumer;

/**
 * Alta de turista con su cuenta Cliente, o edición de sus datos personales.
 * Lo usan la grilla de Turistas y "Hacer reserva" (que preselecciona el turista creado):
 * por eso avisa al terminar con el turista guardado (alGuardar).
 */
public class TuristaDialog extends AbmDialog<TuristaResponse, RegistroClienteRequest> {

    private final TuristaForm formulario;
    private final Consumer<TuristaResponse> alGuardar;

    public TuristaDialog(ClienteAbm<TuristaResponse, RegistroClienteRequest> client, TuristaResponse turista,
                         Consumer<TuristaResponse> alGuardar) {
        super(client, turista, "turista", () -> { });
        this.alGuardar = alGuardar;
        this.formulario = new TuristaForm(!esEdicion(), "El turista puede cambiarla después de iniciar sesión.");
        if (esEdicion()) {
            formulario.cargar(turista);
        }
        add(formulario);
    }

    @Override
    protected List<String> validar() {
        return formulario.validar();
    }

    @Override
    protected RegistroClienteRequest armarRequest() {
        return formulario.armarRequest();
    }

    @Override
    protected void despuesDeGuardar(TuristaResponse guardado) {
        alGuardar.accept(guardado);
    }
}
