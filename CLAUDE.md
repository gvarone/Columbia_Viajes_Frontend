# CLAUDE.md — Columbia Viajes (Frontend)

Versionado en el repo para compartir el contexto entre PCs. No incluir claves, tokens ni credenciales.

## Contexto académico
- Materia: Programación 2 (Tecnicatura Superior en Informática Aplicada, INSPT-UTN). Trabajo grupal.
- **Requisitos vigentes: `../Consignas`** (frameworks Java, BD relacional, ABM, arquitectura en capas, entregables).
- `../Prog.2_1erTP_03.pdf` está **obsoleto** en lo técnico (consola, sin GUI, sin BD, jar/Javadoc/UML): ignorarlo. Solo sirve como descripción del dominio (sucursales, hoteles, vuelos, turistas, reservas, 4 roles).
- Se evalúa también: POO (abstracción, encapsulamiento, herencia, polimorfismo), SOLID, uso de GitHub por todos los integrantes, y una defensa oral individual → preferir código simple y explicable sobre código "inteligente".

## Arquitectura
```
Navegador → Front Vaadin (:8081, este repo) → API REST backend (:8080, ../Columbia_Viajes_Backend) → MySQL (:3307)
```
- Front sin BD propia: todo pasa por la API REST. Las llamadas las hace el servidor Vaadin (no hace falta CORS).
- Stack: Java 21, Spring Boot 4.1.1, Vaadin 25.3.1 (Flow, UI en Java, tema Aura + `styles.css`), Maven wrapper.

## Comandos
```bash
./mvnw clean compile      # compilar
./mvnw spring-boot:run    # correr (goal por defecto de `./mvnw`); levantar antes el backend
./mvnw -o -q compile      # compilación rápida offline para verificar cambios
```
No hay tests todavía (`browserless-test-spring` está en el pom pero sin uso).

## Estructura (`src/main/java/com/columbia/viajes/`)
- `config/BackendConfig` — **único `RestClient`** hacia `backend.url`. Interceptor agrega `Authorization: Bearer <jwt>` del usuario logueado; un 401 en un request con token lanza `SesionExpiradaException`.
- `config/ManejadorErroresVaadin` — ErrorHandler de sesión: `SesionExpiradaException` en un listener → `authContext.logout()`. Durante la navegación lo maneja `views/SesionExpiradaView` (`HasErrorParameter`).
- `client/` — un `@Service` por recurso; reciben el `RestClient` por constructor, sin manejar tokens. `AuthClient` (login/logout/registrar/cambiarContrasenia).
- `dto/request`, `dto/response` — records que replican los DTOs del backend. `dto/enums` — `ClaseVuelo`, `RegimenHospedaje` (con `getEtiqueta()` para la UI).
- `security/`:
  - `SecurityConfig` — Spring Security + `VaadinSecurityConfigurer` (login view, CSRF, logout; el logout también llama a `POST /auth/logout`).
  - `BackendAuthenticationProvider` — valida credenciales contra `POST /auth/login`; principal = `UsuarioAutenticado(nombre, rol, token)`, autoridad `ROLE_<RolEnum.name()>`.
  - `SesionUsuario` — helpers estáticos de solo lectura sobre el `SecurityContext` (`getUsuario`, `getToken`, `getRol`, `tieneRol(...)`).
  - `Roles` — constantes para `@RolesAllowed` (`ADMIN`, `VENDEDOR`, `CLIENTE`, `DUENIO`), iguales a `RolEnum.name()`.
- `views/base/` — `MainLayout` (`@PermitAll`, menú según rol), `Notificaciones` (éxito/error + mensaje legible de errores del backend), `Formatos` (fechas/precios, `precioSinCentavos` para el catálogo; `LOCALE_AR` para los selectores de fecha), `Validaciones` (largo, largo máximo, email, contraseña + repetición, desplegable obligatorio, entero mínimo, monto no negativo, fecha y fecha-hora; replican `@Size`/`@Email`/`@NotNull`/`@Min` del backend).
- `views/` — `LoginView` (`LoginForm` que postea a `/login`, link a registro), `RegistroView` (registro público), `HomeView`, grillas `Reserva/Turista/Vuelo/Hotel/Usuario/SucursalGridView`, `NuevaReservaView` (acepta `?paquete=<id>` para preseleccionar), `EditarReservaDialog` (solo clase y régimen), `TuristaGridView` + `TuristaDialog` (ABM; alta con cuenta Cliente, edición solo de datos personales; también lo usa "Hacer reserva"), `PaqueteCatalogoView` (tarjetas + filtros nombre/ciudad/hotel), `PaqueteDetalleDialog` (hotel y vuelos), `RankingVendedoresView` (Dueño; el orden viene del backend, la vista agrega puesto y dirección de sucursal), `CiudadGridView` + `CiudadDialog` (ABM de referencia, ver "ABM" abajo), `PerfilView` (`GET /usuarios/me`, solo lectura; datos de turista si es Cliente) y `CambiarContraseniaDialog` (al cambiarla el backend invalida el JWT, así que avisa y hace logout).
- Imágenes de paquetes: las pide el servidor Vaadin (`PaqueteClient.imagen`) y se sirven con `DownloadHandler.fromInputStream`; el navegador nunca llama directo al backend.
- **ABM** (base en `views/base/`, ejemplo completo: ciudades):
  - `dto/response/Identificable` (`Integer id()`; un record con campo `id` la cumple con solo `implements Identificable`) y `client/ClienteAbm<T, R>` (`listar/crear/actualizar/eliminar`, la implementa el cliente de la entidad).
  - `AbmGridView<T, R>`: encabezado + botón "Nuevo" + columna editar/eliminar (solo si `puedeEditar()`, por defecto Admin) + `ConfirmDialog` de baja. La subclase pone `@Route` y acceso, y define `configurarColumnas()` (se llama desde el constructor de la base: no usar campos de la subclase fuera de lambdas), `crearDialogo(item)` y `descripcion(item)` ("la ciudad \"CABA\"").
  - Reglas entre campos (ej. vuelo: origen ≠ destino, salida futura, llegada > salida): en `validar()` después de las validaciones por campo, replicando el service del backend.
  - Desplegables de otra entidad (ej. ciudad del hotel): la grilla carga la lista en `beforeEnter` (override que llama a `super.beforeEnter`) y se la pasa al diálogo. Si el Response trae el nombre y no el ID (`HotelResponse.ciudadNombre`), al editar se preselecciona por nombre.
  - `puedeCrear()` (override opcional, por defecto = `puedeEditar()`): quién ve "Nuevo" (ej. Turistas: Vendedor y Admin crean, solo el Admin edita).
  - `puedeModificar(item)` (override opcional): oculta editar/eliminar en filas puntuales (ej. Usuarios: Administradores y Dueños, que el backend no deja gestionar al Admin).
  - Si el backend usa otro DTO para editar (ej. `UsuarioActualizacionRequest`), el cliente lo adapta en `actualizar()` y las vistas usan un solo tipo `R`.
  - `AbmDialog<T, R>`: alta si `item` es null, edición si no; decide crear/actualizar y maneja errores. Gancho opcional `despuesDeGuardar(guardado)` (ej. `PaqueteDialog` sube la imagen con `PUT /paquetes/{id}/imagen` tanto en alta como en edición; el alta manda solo la parte JSON del multipart). La subclase agrega sus campos en el constructor (los completa si `esEdicion()`), y define `validar()` (lista de errores, replica el `@Valid` del backend) y `armarRequest()`.
- `views/TuristaForm` — campos y validaciones de turista + cuenta Cliente, compartido por `TuristaDialog` y `RegistroView` (no duplicar). Con `conCuenta = false` muestra solo los datos personales (edición).
- Baja de turista: `TuristaClient.eliminar` usa `DELETE /usuarios/{id}` (borra la cuenta y el turista en cascada); `DELETE /turistas/{id}` dejaría la cuenta sin perfil.

### Matriz de acceso (anotaciones en cada vista)
| Vista | Cliente | Vendedor | Admin | Dueño |
|---|---|---|---|---|
| Login, Registro | anónimo | | | |
| Inicio | x | x | x | x |
| Hoteles, Vuelos | x | x | x (+ABM) | x |
| Paquetes (catálogo) | x | x (solo su sucursal, +Reservar) | x | x |
| Paquetes (gestión, `paquetes/gestion`) | | | x (ABM) | x (solo ver) |
| Hacer reserva | | x | | |
| Reservas | x ("Mis reservas", solo ver) | x (+editar/cancelar, solo su sucursal) | x (+editar/cancelar) | x (solo ver) |
| Turistas | | x (+alta) | x (+alta, editar, eliminar) | x (solo ver) |
| Usuarios | | | x (alta de Vendedores; editar/eliminar Vendedores y Clientes) | x (solo ver) |
| Sucursales, Ciudades | | | x (ABM) | x (solo ver) |
| Ranking de vendedores | | | | x |
| Mi perfil, cambiar contraseña | x | x | x | x |

## Convenciones
- Código, nombres y mensajes en español (`listar`, `crear`, `contrasenia`, `Duenio`).
- **Tema Aura** (no Lumo): estilos en `styles.css` con clases y variables `--vaadin-*` / `--aura-*` (nunca `--lumo-*`, con Aura no existen y el navegador las ignora). Variantes de componentes: usar las genéricas (`ButtonVariant.PRIMARY/TERTIARY/ERROR`, `NotificationVariant.SUCCESS/ERROR`, `CardVariant.OUTLINED/...`), no las `LUMO_*`. Tarjetas: componente `Card`, no `Div` con estilos a mano.
- Vistas nuevas: `@Route(value = "...", layout = MainLayout.class)` + **anotación de acceso obligatoria** (`@RolesAllowed({Roles.X})`, `@PermitAll` o `@AnonymousAllowed`; sin anotación queda denegada). Extender `VerticalLayout implements BeforeEnterObserver` y cargar datos en `beforeEnter`.
- Llamadas al backend en vistas: `try { ... } catch (RestClientException e) { Notificaciones.error("contexto", e); }`. No capturar `Exception`: `SesionExpiradaException` tiene que propagarse para el logout automático.
- Grillas: toda columna ordenable. Valores directos o texto → `.setSortable(true)`; columnas formateadas (fechas, precios) → `.setComparator(Dto::campo)` para ordenar por el valor real y no por el texto.
- El menú/botones se ocultan con `SesionUsuario.tieneRol(...)`, pero el control real es la anotación de la vista (y el backend).
- Clientes nuevos: copiar el patrón de `SucursalClient`.
- DTOs: verificar los campos contra `../Columbia_Viajes_Backend/src/main/java/com/columbia/viajes/dto/` antes de crearlos/modificarlos.
- Vaadin 25: `VaadinWebSecurity` ya no existe, usar `VaadinSecurityConfigurer`. Un layout necesita una anotación de acceso al menos tan abierta como sus vistas.
- Commits en español, en tercera persona impersonal ("Se agrega…", "Se implementa…").

## API del backend (base `/api`)
| Recurso | Endpoints | Acceso (según `SecurityConfig` del backend) |
|---|---|---|
| `/auth` | `POST /login` (body `{identificador, contrasenia}` → `{nombre, rolNombre, token}`; Cliente puede usar username o email), `POST /registro` (`RegistroClienteRequest` → `UsuarioResponse`, crea cuenta Cliente + turista), `POST /logout`, `POST /cambiar-contrasenia` | login y registro públicos |
| `/paquetes`, `/hoteles`, `/vuelos`, `/ciudades`, `/sucursales` | CRUD (`GET`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`). Filtros: `/paquetes?nombre=&idCiudad=&idHotel=`, `/hoteles?nombre=&idCiudad=`, `/ciudades?nombre=`. `POST /paquetes` es multipart (parte `paquete` JSON + `imagen` opcional); `PUT /paquetes/{id}/imagen`; `GET /paquetes/{id}/imagen` (PNG público; `PaqueteResponse.imagen` es la ruta relativa) | GET público; escritura: Admin/Dueño |
| `/turistas` | CRUD | GET: Vendedor/Admin/Dueño |
| `/paquetes-reservas` | CRUD + `GET /ranking-vendedores` (`RankingVendedorResponse`: vendedorId, nombre, totalVendido, sucursalId) | GET listado/detalle: Cliente (solo las suyas), Vendedor (su sucursal), Admin/Dueño (todas); escritura: Vendedor/Admin/Dueño; ranking: solo Dueño |
| `/usuarios` | CRUD + `GET /vendedores`, `GET /clientes`, `GET /me` (`MiPerfilResponse` = usuario + turista, turista null si no es Cliente) | `/me`: cualquier autenticado; `/vendedores`, `/clientes`: Vendedor/Admin/Dueño; resto: Admin/Dueño |
| `/roles` | `GET`, `GET /{id}` | Admin/Dueño |

Las reservas son sobre **paquetes** (`Paquete` = vuelo ida + vuelo vuelta + hotel + fechas + precio). `PaqueteReservaRequest` = `{idPaquete, idTurista, claseVuelo, regimenHospedaje, idVendedor}` (clase y régimen son `@NotNull`). `TuristaRequest` = `{nombre, apellidos, direccion, email, telefono}` (sin `idUsuario`).

## Decisiones tomadas
- **Usuarios** (reglas del backend): el Admin solo gestiona Clientes y Vendedores; la sucursal es obligatoria para Vendedor y prohibida para el resto; no se puede pasar entre Cliente y Vendedor; la edición no cambia contraseña ni datos de turista. En el front "Nuevo usuario" crea solo Vendedores: los Clientes se crean desde Turistas o el registro (no duplicar el alta).
- **Todo turista tiene cuenta Cliente** (modelo de `main` del backend: comparten ID con `@MapsId`). Se descartó la rama `gvarone` (turista con cuenta opcional), ya borrada. Alta en sucursal: `TuristaClient.crear` = `AuthClient.registrar` + `GET /turistas/{id}`. Registro público (`RegistroView`): solo `AuthClient.registrar` (sin token no puede leer `/turistas`).
- La BD local debe tener la estructura del `schema.sql` de `main` del backend (`turistas.id` = FK a `usuarios.id` con CASCADE, sin `id_usuario`). Para rehacerla: `mysql -h127.0.0.1 -P3307 -uroot -p columbia_viajes < schema.sql` (borra los datos actuales).
- **Solo el Vendedor carga reservas** (el backend toma el vendedor del token). Admin y Dueño no ven "Hacer reserva". El Cliente solo consulta (no se auto-reserva).
- Editar una reserva cambia solo clase y régimen; para cambiar turista o paquete se cancela y se crea otra.
- En el catálogo el Vendedor ve solo los paquetes de su sucursal (sucursal tomada de `GET /usuarios/me`).
- **Ranking de vendedores del Dueño**: lo calcula el backend (`GET /paquetes-reservas/ranking-vendedores`); no se calcula en el front.
- Seguridad con Spring Security + Vaadin (no mecanismo casero).

## Errores conocidos del front (al 2026-10-19)
1. `README.md` desactualizado (estructura, login, pendientes).
2. `Dockerfile` usa `--spring.profiles.active=prod` pero no existe `application-prod.properties`; `backend.url` apunta a localhost.
3. `pom.xml` incluye repositorios `vaadin-prereleases` (innecesarios con versión estable).
4. `ReservaGridView` no deja editar ni cancelar al Dueño (el backend sí lo permite): es a propósito, el Dueño solo consulta.

## Pendientes del backend (los maneja un compañero en `main` del backend; **no modificar sin consultar**)
- **Permitir al Vendedor editar turistas** (`PUT /turistas/{id}`): el dominio dice que los vendedores "administrarán las reservas y a los clientes"; hoy `PUT`/`DELETE /turistas` es solo Admin/Dueño.
- A conversar: endpoint propio para que el Vendedor cree clientes (hoy se usa el registro público, que funciona para cualquier rol).
- El Dueño tiene permisos de escritura sobre casi todo (regla Admin/Dueño); según la consigna solo consulta.
- `DELETE /turistas/{id}` borra solo el turista y deja la cuenta Cliente sin perfil.
- Un 404/400 esperado a veces llega como 401 (despacho de errores sin regla para `DispatcherType.ERROR`).
- JWT dura 15 min (`900000`) pero el comentario dice 24 h; con 15 min el front desloguea seguido.
- No se validan plazas disponibles de vuelos/hoteles al reservar.
- El filtro JWT responde 401 sin cuerpo; `mvnw` sin permiso de ejecución (correr con `sh ./mvnw`).

## Pendientes según la consigna
**Funcionalidad por rol**:
- **Cliente**: registro, consulta de vuelos/hoteles/paquetes y "Mis reservas" hechos; y "Mi perfil" (solo lectura) hechos.
- **Vendedor**: alta, edición (clase/régimen) y cancelación de reservas, catálogo con "Reservar" y alta de turistas hechos. Editar turistas: el front lo tiene (lo usa el Admin), pero el backend no le permite `PUT /turistas` al Vendedor; cuando lo habilite, agregar `VENDEDOR` en `TuristaGridView.puedeEditar()`.
- **Administrador**: ABM de ciudades, sucursales, hoteles, vuelos, paquetes y usuarios hechos. Los ABM los edita solo el Admin; el Dueño consulta.
- **Dueño**: ranking de vendedores hecho.
- Cambio de contraseña: hecho (desde "Mi perfil", todos los roles).

**Entregables** (`../Consignas`):
- Repo en GitHub completo y ejecutable, con todos los integrantes como colaboradores activos.
- Documento PDF: portada, link al repo, descripción (tema, objetivos, actores), arquitectura y capas, clases principales, capturas de código, diagrama de casos de uso + descripción de cada caso, DER + descripción de tablas, manual de usuario por actor con capturas.
