package com.columbia.viajes.views.base;

import com.columbia.viajes.client.ClienteAbm;
import com.columbia.viajes.dto.response.Identificable;
import com.columbia.viajes.security.SesionUsuario;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import org.springframework.web.client.RestClientException;

import static com.columbia.viajes.security.enums.RolEnum.ADMIN;

/**
 * Base de las vistas de ABM: grilla, botón "Nuevo", columna de acciones
 * (editar/eliminar) y confirmación de borrado. Cada subclase define sus columnas,
 * su diálogo de alta/edición y cómo se describe un registro.
 * La subclase lleva @Route y la anotación de acceso.
 *
 * T: DTO de respuesta, R: DTO de request.
 */
public abstract class AbmGridView<T extends Identificable, R> extends VerticalLayout
        implements BeforeEnterObserver {

    protected final ClienteAbm<T, R> client;
    protected final Grid<T> grid = new Grid<>();
    private final String titulo;

    /**
     * @param titulo     título de la vista, en plural ("Ciudades")
     * @param textoNuevo texto del botón de alta ("Nueva ciudad")
     */
    protected AbmGridView(ClienteAbm<T, R> client, String titulo, String textoNuevo) {
        this.client = client;
        this.titulo = titulo;

        configurarColumnas();
        if (puedeEditar()) {
            grid.addComponentColumn(this::crearAcciones).setHeader("Acciones").setAutoWidth(true).setFlexGrow(0);
        }

        Button nuevo = new Button(textoNuevo, VaadinIcon.PLUS.create(), e -> abrirDialogo(null));
        nuevo.addThemeVariants(ButtonVariant.PRIMARY);
        nuevo.setVisible(puedeEditar());

        HorizontalLayout encabezado = new HorizontalLayout(new H2(titulo), nuevo);
        encabezado.setWidthFull();
        encabezado.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        encabezado.setAlignItems(FlexComponent.Alignment.CENTER);

        add(encabezado, grid);
        setSizeFull();
        grid.setSizeFull();
    }

    /**
     * Agrega las columnas de la entidad a {@link #grid}. Se llama desde el constructor
     * de esta clase, antes que el de la subclase: no usar acá campos propios de la
     * subclase (sí se pueden leer dentro de las lambdas, que se evalúan después).
     */
    protected abstract void configurarColumnas();

    /** Diálogo de alta (item null) o de edición. Al guardar tiene que llamar a {@link #cargar()}. */
    protected abstract AbmDialog<T, R> crearDialogo(T item);

    /** Descripción para los mensajes, con artículo: la ciudad "CABA". */
    protected abstract String descripcion(T item);

    /**
     * Si un registro puntual se puede editar/eliminar (además de {@link #puedeEditar()}).
     * Por defecto todos; una subclase lo sobrescribe si el backend restringe algunos.
     */
    protected boolean puedeModificar(T item) {
        return true;
    }

    /** Por defecto solo el Admin edita; el Dueño consulta. Una subclase puede cambiarlo. */
    protected boolean puedeEditar() {
        return SesionUsuario.tieneRol(ADMIN);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        cargar();
    }

    protected void cargar() {
        try {
            grid.setItems(client.listar());
        } catch (RestClientException e) {
            Notificaciones.error("No se pudieron cargar " + titulo.toLowerCase(), e);
        }
    }

    private void abrirDialogo(T item) {
        crearDialogo(item).open();
    }

    private HorizontalLayout crearAcciones(T item) {
        if (!puedeModificar(item)) {
            return new HorizontalLayout();
        }
        Button editar = new Button(VaadinIcon.EDIT.create(), e -> abrirDialogo(item));
        editar.addThemeVariants(ButtonVariant.TERTIARY);
        editar.setTooltipText("Editar");

        Button eliminar = new Button(VaadinIcon.TRASH.create(), e -> confirmarEliminar(item));
        eliminar.addThemeVariants(ButtonVariant.TERTIARY, ButtonVariant.ERROR);
        eliminar.setTooltipText("Eliminar");

        return new HorizontalLayout(editar, eliminar);
    }

    private void confirmarEliminar(T item) {
        ConfirmDialog confirmacion = new ConfirmDialog("Confirmar eliminación",
                "¿Eliminar " + descripcion(item) + "? Si otros registros lo usan, no se va a poder eliminar.",
                "Eliminar", e -> eliminar(item));
        confirmacion.setCancelable(true);
        confirmacion.setCancelText("Cancelar");
        confirmacion.setConfirmButtonTheme("error primary");
        confirmacion.open();
    }

    private void eliminar(T item) {
        try {
            client.eliminar(item.id());
            Notificaciones.exito("Se eliminó " + descripcion(item) + ".");
            cargar();
        } catch (RestClientException e) {
            Notificaciones.error("No se pudo eliminar " + descripcion(item), e);
        }
    }
}
