package com.columbia.viajes.views;

import com.columbia.viajes.client.PaqueteClient;
import com.columbia.viajes.dto.request.PaqueteRequest;
import com.columbia.viajes.dto.response.HotelResponse;
import com.columbia.viajes.dto.response.Identificable;
import com.columbia.viajes.dto.response.PaqueteResponse;
import com.columbia.viajes.dto.response.SucursalResponse;
import com.columbia.viajes.dto.response.VueloResponse;
import com.columbia.viajes.views.base.AbmDialog;
import com.columbia.viajes.views.base.Formatos;
import com.columbia.viajes.views.base.Notificaciones;
import com.columbia.viajes.views.base.Validaciones;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.server.streams.UploadHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.web.client.RestClientException;

/**
 * Alta y edición de un paquete (vuelo ida + vuelo vuelta + hotel + fechas + precio).
 * Replica las validaciones de PaqueteRequest y PaqueteService del backend.
 * La imagen es opcional: se sube después de guardar los datos (despuesDeGuardar),
 * tanto en el alta como en la edición. En la edición, si no se elige otra, queda la actual.
 */
public class PaqueteDialog extends AbmDialog<PaqueteResponse, PaqueteRequest> {

    /** Límite del backend (spring.servlet.multipart.max-file-size). */
    private static final int TAMANIO_MAXIMO_IMAGEN = 5 * 1024 * 1024;

    private final PaqueteClient paqueteClient;

    private final TextField nombre = new TextField("Nombre");
    private final TextArea descripcion = new TextArea("Descripción");
    private final ComboBox<SucursalResponse> sucursal = new ComboBox<>("Sucursal");
    private final ComboBox<HotelResponse> hotel = new ComboBox<>("Hotel");
    private final DatePicker llegadaHotel = new DatePicker("Llegada al hotel");
    private final DatePicker partidaHotel = new DatePicker("Partida del hotel");
    private final ComboBox<VueloResponse> vueloIda = new ComboBox<>("Vuelo de ida");
    private final ComboBox<VueloResponse> vueloVuelta = new ComboBox<>("Vuelo de vuelta");
    private final BigDecimalField precio = new BigDecimalField("Precio");

    // Imagen elegida en el Upload (null = no se eligió ninguna)
    private String nombreImagen;
    private byte[] contenidoImagen;

    public PaqueteDialog(PaqueteClient client, List<SucursalResponse> sucursales, List<HotelResponse> hoteles,
                         List<VueloResponse> vuelos, PaqueteResponse paquete, Runnable alGuardar) {
        super(client, paquete, "paquete", alGuardar);
        this.paqueteClient = client;

        nombre.setRequired(true);
        nombre.setMinLength(2);
        nombre.setMaxLength(100);
        descripcion.setMaxLength(1000);
        descripcion.setHelperText("Opcional, hasta 1000 caracteres.");

        sucursal.setRequired(true);
        sucursal.setItems(sucursales);
        sucursal.setItemLabelGenerator(SucursalResponse::direccion);
        hotel.setRequired(true);
        hotel.setItems(hoteles);
        hotel.setItemLabelGenerator(h -> h.nombre() + " (" + h.ciudadNombre() + ")");
        for (ComboBox<VueloResponse> vuelo : List.of(vueloIda, vueloVuelta)) {
            vuelo.setRequired(true);
            vuelo.setItems(vuelos);
            vuelo.setItemLabelGenerator(PaqueteDialog::etiquetaVuelo);
        }
        for (DatePicker fecha : List.of(llegadaHotel, partidaHotel)) {
            fecha.setRequiredIndicatorVisible(true);
            fecha.setLocale(Formatos.LOCALE_AR);
        }
        precio.setRequiredIndicatorVisible(true);
        precio.setPrefixComponent(new Span("$"));

        if (esEdicion()) {
            PaqueteResponse p = getItem();
            nombre.setValue(p.nombre());
            descripcion.setValue(p.descripcion() == null ? "" : p.descripcion());
            seleccionarPorId(sucursal, sucursales, p.sucursalId());
            seleccionarPorId(hotel, hoteles, p.hotelId());
            seleccionarPorId(vueloIda, vuelos, p.vueloIdaId());
            seleccionarPorId(vueloVuelta, vuelos, p.vueloVueltaId());
            llegadaHotel.setValue(p.fechaLlegadaHotel());
            partidaHotel.setValue(p.fechaPartidaHotel());
            precio.setValue(p.precio());
        }

        FormLayout formulario = new FormLayout(nombre, sucursal, descripcion, hotel, precio,
                llegadaHotel, partidaHotel, vueloIda, vueloVuelta);
        formulario.setColspan(descripcion, 2);
        formulario.setColspan(hotel, 2);
        formulario.setColspan(vueloIda, 2);
        formulario.setColspan(vueloVuelta, 2);
        formulario.setMaxWidth("700px");

        add(new VerticalLayout(formulario, crearUpload()));
    }

    private Upload crearUpload() {
        Upload upload = new Upload(UploadHandler.inMemory((metadata, contenido) -> {
            nombreImagen = metadata.fileName();
            contenidoImagen = contenido;
        }));
        upload.setMaxFiles(1);
        upload.setMaxFileSize(TAMANIO_MAXIMO_IMAGEN);
        upload.setAcceptedFileTypes("image/jpeg", "image/png", ".jpg", ".jpeg", ".png");
        upload.setUploadButton(new Button("Elegir imagen"));
        upload.setDropLabel(new Span(esEdicion() && getItem().imagen() != null
                ? "Imagen (JPEG o PNG, hasta 5 MB). Si no elegís otra, queda la actual."
                : "Imagen opcional (JPEG o PNG, hasta 5 MB)."));
        upload.addFileRejectedListener(e -> Notificaciones.error("La imagen tiene que ser JPEG o PNG de hasta 5 MB."));
        upload.addFileRemovedListener(e -> {
            nombreImagen = null;
            contenidoImagen = null;
        });
        return upload;
    }

    @Override
    protected List<String> validar() {
        List<String> errores = new ArrayList<>();
        Validaciones.largo(nombre, errores);
        Validaciones.largoMaximo(descripcion, errores);
        Validaciones.seleccionado(sucursal, errores);
        Validaciones.seleccionado(hotel, errores);
        Validaciones.montoNoNegativo(precio, errores);
        Validaciones.fecha(llegadaHotel, errores);
        Validaciones.fecha(partidaHotel, errores);
        Validaciones.seleccionado(vueloIda, errores);
        Validaciones.seleccionado(vueloVuelta, errores);
        if (!errores.isEmpty()) {
            return errores;
        }

        // Regla entre campos (la misma que PaqueteService del backend)
        if (!partidaHotel.getValue().isAfter(llegadaHotel.getValue())) {
            partidaHotel.setInvalid(true);
            errores.add("La partida del hotel tiene que ser posterior a la llegada.");
        }
        return errores;
    }

    @Override
    protected PaqueteRequest armarRequest() {
        String texto = descripcion.getValue().trim();
        return new PaqueteRequest(nombre.getValue().trim(), texto.isEmpty() ? null : texto,
                llegadaHotel.getValue(), partidaHotel.getValue(), precio.getValue(),
                sucursal.getValue().id(), vueloIda.getValue().id(), vueloVuelta.getValue().id(),
                hotel.getValue().id());
    }

    /** Si se eligió una imagen, se sube ahora que el paquete ya existe y tiene ID. */
    @Override
    protected void despuesDeGuardar(PaqueteResponse guardado) {
        if (contenidoImagen == null) {
            return;
        }
        try {
            paqueteClient.actualizarImagen(guardado.id(), nombreImagen, contenidoImagen);
        } catch (RestClientException e) {
            Notificaciones.error("El paquete se guardó, pero no se pudo subir la imagen", e);
        }
    }

    private static String etiquetaVuelo(VueloResponse v) {
        return "N° " + v.id() + " · " + v.ciudadOrigenNombre() + " → " + v.ciudadDestinoNombre()
                + " · " + Formatos.fechaHora(v.fechaSalida());
    }

    private static <E extends Identificable> void seleccionarPorId(
            ComboBox<E> campo, List<E> opciones, Integer id) {
        opciones.stream()
                .filter(o -> Objects.equals(o.id(), id))
                .findFirst()
                .ifPresent(campo::setValue);
    }
}
