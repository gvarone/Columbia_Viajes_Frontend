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
├── Application.java                  Clase principal
├── client/                           Clientes HTTP hacia el backend (RestClient / OpenFeign)
│   ├── UsuarioClient.java
│   └── SucursalClient.java
├── dto/                              DTOs y Records (Request / Response mapeados del JSON backend)
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   └── SucursalResponse.java
├── security/                         Seguridad, control de sesión y roles
│   └── SesionUsuario.java
└── views/                            Pantallas y vistas Vaadin
├── base/
│   ├── MainLayout.java           Esqueleto general (Header, Drawer, Menú lateral)
│   └── ProtectedView.java        Clase base para vistas que exigen autenticación
├── HomeView.java
├── LoginView.java
├── SucursalGridView.java
└── UsuarioGridView.java
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

La primera ejecución tarda varios minutos porque Vaadin descarga las dependencias del frontend.

3. Abrir `http://localhost:8081/`.

## Funcionalidad actual

- **`Autenticación`**: Pantalla de login (/login) con soporte para envío mediante la tecla Enter y validación de credenciales contra el backend.

- **`Gestión de Sesión`**: Almacenamiento seguro del token y datos del usuario en la sesión de Vaadin, con redirección automática si el token expira.

- **`Navegación y Layout`**: Estructura responsive con menú lateral (SideNav) adaptativo según el rol del usuario ("Administrador", "Vendedor", "Dueño", "Cliente").

- **`Grillas`**: Visualización de catálogos y entidades (Usuarios, Sucursales) mediante componentes de datos de Vaadin conectados a sus respectivos clientes HTTP.

## Problemas conocidos / notas

- Si la tabla aparece con columnas vacías, los nombres de los campos de `UsuarioResponse` no coinciden con los del JSON del backend.

## Pendiente

- [ ] Completar pantallas de gestión para hoteles, vuelos y turistas.
- [ ] Carga, cancelación y consulta de reservas de viajes.
- [ ] Mejoras estéticas y de notificaciones en formularios de alta/modificación.

