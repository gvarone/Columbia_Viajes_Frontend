# Columbia Viajes - Frontend

Frontend web del sistema **Columbia Viajes** (agencia de viajes: sucursales, hoteles, vuelos, paquetes, turistas y reservas), desarrollado con **Vaadin Flow**: toda la interfaz está escrita en Java.

Trabajo grupal de Programación 2 (Tecnicatura Superior en Informática Aplicada, INSPT-UTN).

Es un proyecto **independiente del backend**: no tiene base de datos propia y toda la información la obtiene de la API REST del backend (repositorio `Columbia_Viajes_Backend`).

## Stack

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 21 | Lenguaje |
| Spring Boot | 4.1.1 | Aplicación, inyección de dependencias, `RestClient` |
| Spring Security | (de Spring Boot) | Login, sesión y control de acceso por rol |
| Vaadin Flow | 25.3.1 | Interfaz web en Java (tema Aura) |
| Maven | wrapper incluido (`./mvnw`) | Build |

## Arquitectura

```
Navegador  →  Front Vaadin (:8081)  →  API REST Backend (:8080)  →  MySQL (:3307)
```

- Las llamadas al backend las hace el **servidor Vaadin**, no el navegador (por eso no hace falta CORS). Las imágenes de los paquetes también las pide el servidor y se las entrega al navegador.
- El login lo valida el backend (`POST /auth/login`), que devuelve un JWT. El front lo guarda en la sesión de Spring Security y lo agrega en cada llamada (`Authorization: Bearer ...`). Si el backend responde 401 (token vencido), el front cierra la sesión.

### Capas

| Capa | Paquete | Responsabilidad |
|---|---|---|
| Presentación | `views`, `views/base` | Pantallas, grillas y formularios (Vaadin) |
| Acceso a la API | `client` | Un `@Service` por recurso del backend; arman los requests HTTP |
| Datos | `dto/request`, `dto/response`, `dto/enums` | Records que replican los DTOs del backend |
| Seguridad | `security` | Spring Security: login contra el backend, sesión y roles |
| Configuración | `config` | `RestClient` único con el token y manejo de sesión vencida |

## Estructura

```
src/main/java/com/columbia/viajes/
├── Application.java              Clase principal (tema Aura + styles.css)
├── config/                       RestClient hacia el backend, manejo de sesión vencida
├── client/                       Clientes HTTP: Paquete, Reserva, Turista, Usuario, Hotel, Vuelo,
│                                 Ciudad, Sucursal, Rol, Auth. ClienteAbm<T, R> = contrato de ABM
├── dto/
│   ├── request/                  Lo que se envía al backend (records)
│   ├── response/                 Lo que devuelve el backend (records). Identificable = tiene id()
│   └── enums/                    ClaseVuelo, RegimenHospedaje
├── security/                     SecurityConfig, BackendAuthenticationProvider, SesionUsuario, Roles
└── views/
    ├── base/                     MainLayout (menú según rol), AbmGridView y AbmDialog (bases de
    │                             los ABM), Notificaciones, Formatos, Validaciones
    ├── LoginView, RegistroView, HomeView, PerfilView, CambiarContraseniaDialog
    ├── PaqueteCatalogoView, PaqueteDetalleDialog       Catálogo con tarjetas y filtros
    ├── NuevaReservaView, ReservaGridView, EditarReservaDialog
    ├── RankingVendedoresView
    └── *GridView + *Dialog       ABM de ciudades, sucursales, hoteles, vuelos, paquetes,
                                  usuarios y turistas (sobre AbmGridView / AbmDialog)
```

### Diseño de los ABM

Todas las pantallas de alta, baja y modificación comparten dos clases base abstractas:

- **`AbmGridView<T, R>`**: grilla, botón "Nuevo", columna editar/eliminar y confirmación de borrado. Cada subclase define sus columnas, su diálogo y cómo se describe un registro.
- **`AbmDialog<T, R>`**: formulario de alta (sin registro) o edición (con registro); decide si crear o actualizar y muestra los errores del backend. Cada subclase define sus campos, `validar()` y `armarRequest()`.
- Trabajan sobre la interfaz **`ClienteAbm<T, R>`** (`listar`, `crear`, `actualizar`, `eliminar`), que implementa el cliente de cada entidad.

Es el patrón *Template Method*: la base fija los pasos y cada entidad completa lo propio (herencia + polimorfismo).

## Configuración

`src/main/resources/application.properties`:

```properties
server.port=8081
backend.url=http://localhost:8080/api
```

`backend.url` también se puede pasar como variable de entorno: `BACKEND_URL=http://...`.

## Cómo ejecutarlo

### Requisitos
- JDK 21
- Backend corriendo en `http://localhost:8080` con su base MySQL (ver el README del backend)

### Pasos
1. Levantar primero el backend.
2. Desde la carpeta de este proyecto:
   ```bash
   ./mvnw            # equivale a ./mvnw spring-boot:run
   ```
   La primera vez tarda varios minutos: Vaadin descarga las dependencias del frontend.
3. Abrir `http://localhost:8081/`.

### Con Docker
```bash
docker build -t columbia-viajes-front .
docker run -p 8081:8081 -e BACKEND_URL=http://host.docker.internal:8080/api \
    --add-host=host.docker.internal:host-gateway columbia-viajes-front
```

### Usuarios de prueba
El `schema.sql` del backend trae las cuentas `prueba_admin` (Administrador), `prueba_duenio` (Dueño), `vendedor_a1`, `vendedor_a2`, `vendedor_b1` (Vendedores) y `juan_viajes` (Cliente). Las contraseñas no se publican: ver el README del backend para asignarlas. También se puede crear un Cliente desde "Crear cuenta" en el login.

## Funcionalidad por rol

| Pantalla | Cliente | Vendedor | Administrador | Dueño |
|---|---|---|---|---|
| Registro ("Crear cuenta") | sin sesión | | | |
| Inicio, Mi perfil, cambio de contraseña | ✓ | ✓ | ✓ | ✓ |
| Catálogo de paquetes (tarjetas, filtros, detalle) | ✓ | solo su sucursal, + Reservar | ✓ | ✓ |
| Hacer reserva | | ✓ | | |
| Reservas | "Mis reservas" (consulta) | su sucursal: editar y cancelar | todas: editar y cancelar | todas (consulta) |
| Turistas | | alta y consulta | ABM | consulta |
| Vuelos, Hoteles | consulta | consulta | ABM | consulta |
| Sucursales, Ciudades, Gestión de paquetes | | | ABM | consulta |
| Usuarios | | | alta de vendedores; editar y eliminar vendedores y clientes | consulta |
| Ranking de vendedores | | | | ✓ |

- Las grillas se pueden ordenar haciendo clic en el título de cada columna.
- Los formularios validan los datos antes de enviarlos, con las mismas reglas que el backend; si el backend rechaza algo, se muestra su mensaje.
- El menú y los botones se adaptan al rol, pero el control real lo hacen las anotaciones de acceso de cada vista y el backend.
