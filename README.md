# Columbia Viajes - Frontend

Frontend web del sistema Columbia Viajes, desarrollado con **Vaadin Flow** (interfaz escrita en Java). Es un proyecto **independiente del backend**: no accede a la base de datos, toda la información la obtiene consumiendo la API REST del backend.

Repositorio del backend: `Columbia_Viajes_Backend`

## Stack

| Tecnología | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 4.1.1 |
| Vaadin | 25.3.1 |
| Maven | 3.9+ |

## Arquitectura

```
Navegador  →  Front (Vaadin, :8081)  →  API REST Backend (Spring Boot, :8080)  →  MySQL
```

- Las llamadas al backend las hace el **servidor Vaadin** (no el navegador), por lo que no se necesita configurar CORS.
- El front no tiene base de datos propia.

## Estructura del proyecto

```
src/main/java/com/columbia/viajes/
├── Application.java        Clase principal
├── client/                 Clientes HTTP hacia el backend
│   └── UsuarioClient.java
├── dto/                    DTOs propios del front (copia de los campos del JSON del backend)
│   ├── UsuarioResponse.java
│   ├── LoginRequest.java       (aún sin usar)
│   └── LoginResponse.java      (aún sin usar)
└── views/                  Pantallas Vaadin
    └── UsuariosView.java
```

## Configuración

Archivo `src/main/resources/application.properties`:

```properties
server.port=8081
backend.url=http://localhost:8080/api
```

- `server.port`: el front corre en el 8081 porque el backend usa el 8080.
- `backend.url`: URL base de la API del backend.

## Cómo ejecutarlo

### Requisitos
- JDK 21
- Maven
- Backend corriendo en `http://localhost:8080` (con MySQL levantado)

### Pasos

1. Levantar primero el backend.
2. Desde la carpeta de este proyecto:

```bash
mvn spring-boot:run
```

Si tenés varias versiones de Java instaladas, forzar la 21:

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 mvn spring-boot:run
```

La primera ejecución tarda varios minutos porque Vaadin descarga las dependencias del frontend.

3. Abrir `http://localhost:8081/usuarios`.

## Funcionalidad actual

- **`/usuarios`**: tabla (`Grid`) con el listado de usuarios obtenido desde `GET /api/usuarios` del backend. Si el backend no responde, muestra una notificación de error.

Con esto queda verificada la conexión front ↔ back.

## Problemas conocidos / notas

- El wrapper de Maven (`mvnw`) no funciona porque falta la carpeta `.mvn/`. Usar el `mvn` del sistema, o regenerarlo con `mvn wrapper:wrapper`.
- Si el backend responde `401`/`403`, es Spring Security rechazando la llamada. Hay que ajustar la configuración de seguridad del backend o autenticar desde el front.
- Si la tabla aparece con columnas vacías, los nombres de los campos de `UsuarioResponse` no coinciden con los del JSON del backend.
- El login del backend (`POST /api/auth/login`) devuelve `mensaje`, `nombre`, `idRol` y `token: null`, es decir, no emite token. Los DTOs `LoginRequest` y `LoginResponse` quedaron preparados pero la pantalla de login todavía no está implementada.

## Pendiente

- [ ] Pantalla de login y manejo de sesión.
- [ ] Control de acceso por rol (cliente, vendedor, administrador, dueño).
- [ ] ABM de sucursales, hoteles, vuelos y turistas.
- [ ] Carga y consulta de reservas.
- [ ] Definir con el backend el esquema de autenticación (token, sesión o Basic Auth).
