package com.columbia.viajes.views.base;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

/** Notificaciones comunes y traducción de errores del backend a mensajes legibles. */
public final class Notificaciones {

    private Notificaciones() {}

    public static void exito(String mensaje) {
        Notification.show(mensaje, 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.SUCCESS);
    }

    public static void error(String mensaje) {
        Notification.show(mensaje, 5000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.ERROR);
    }

    public static void error(String contexto, RestClientException e) {
        error(contexto + ": " + mensajeBackend(e));
    }

    /**
     * El backend responde texto plano ("No tenés permiso...") o, en errores de
     * validación, una lista JSON de strings: ["campo: mensaje", ...].
     */
    static String mensajeBackend(RestClientException e) {
        if (e instanceof ResourceAccessException) {
            return "no se pudo conectar con el servidor";
        }
        if (e instanceof HttpStatusCodeException http) {
            String cuerpo = http.getResponseBodyAsString().trim();
            if (cuerpo.isEmpty()) {
                return http.getStatusText();
            }
            if (cuerpo.startsWith("[") && cuerpo.endsWith("]")) {
                return cuerpo.substring(1, cuerpo.length() - 1)
                        .replace("\",\"", "; ")
                        .replace("\"", "");
            }
            return cuerpo;
        }
        return e.getMessage();
    }
}
