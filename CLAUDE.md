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
- `client/` — un `@Service` por recurso; reciben el `RestClient` por constructor, sin manejar tokens. `AuthClient` (login/logout/registrar).
- `dto/request`, `dto/response` — records que replican los DTOs del backend. `dto/enums` — `ClaseVuelo`, `RegimenHospedaje` (con `getEtiqueta()` para la UI).
- `security/`:
  - `SecurityConfig` — Spring Security + `VaadinSecurityConfigurer` (login view, CSRF, logout; el logout también llama a `POST /auth/logout`).
  - `BackendAuthenticationProvider` — valida credenciales contra `POST /auth/login`; principal = `UsuarioAutenticado(nombre, rol, token)`, autoridad `ROLE_<RolEnum.name()>`.
  - `SesionUsuario` — helpers estáticos de solo lectura sobre el `SecurityContext` (`getUsuario`, `getToken`, `getRol`, `tieneRol(...)`).
  - `Roles` — constantes para `@RolesAllowed` (`ADMIN`, `VENDEDOR`, `CLIENTE`, `DUENIO`), iguales a `RolEnum.name()`.
- `views/base/` — `MainLayout` (`@PermitAll`, menú según rol), `Notificaciones` (éxito/error + mensaje legible de errores del backend), `Formatos` (fechas/precios).
- `views/` — `LoginView` (`LoginForm` que postea a `/login`, link a registro), `RegistroView` (registro público), `HomeView`, grillas `Reserva/Turista/Vuelo/Hotel/Usuario/SucursalGridView`, `NuevaReservaView`, `TuristaDialog`.
- `views/TuristaForm` — campos y validaciones de turista + cuenta Cliente, compartido por `TuristaDialog` y `RegistroView` (no duplicar).

### Matriz de acceso (anotaciones en cada vista)
| Vista | Cliente | Vendedor | Admin | Dueño |
|---|---|---|---|---|
| Login, Registro | anónimo | | | |
| Inicio, Vuelos, Hoteles | x | x | x | x |
| Hacer reserva | | x | | |
| Reservas | | x (+cancelar, solo su sucursal) | x (+cancelar) | x (solo ver) |
| Turistas (+alta) | | x | x | |
| Usuarios, Sucursales | | | x | x (solo ver) |

## Convenciones
- Código, nombres y mensajes en español (`listar`, `crear`, `contrasenia`, `Duenio`).
- Vistas nuevas: `@Route(value = "...", layout = MainLayout.class)` + **anotación de acceso obligatoria** (`@RolesAllowed({Roles.X})`, `@PermitAll` o `@AnonymousAllowed`; sin anotación queda denegada). Extender `VerticalLayout implements BeforeEnterObserver` y cargar datos en `beforeEnter`.
- Llamadas al backend en vistas: `try { ... } catch (RestClientException e) { Notificaciones.error("contexto", e); }`. No capturar `Exception`: `SesionExpiradaException` tiene que propagarse para el logout automático.
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
- **Todo turista tiene cuenta Cliente** (modelo de `main` del backend: comparten ID con `@MapsId`). Se descartó la rama `gvarone` (turista con cuenta opcional), ya borrada. Alta en sucursal: `TuristaClient.crear` = `AuthClient.registrar` + `GET /turistas/{id}`. Registro público (`RegistroView`): solo `AuthClient.registrar` (sin token no puede leer `/turistas`).
- La BD local debe tener la estructura del `schema.sql` de `main` del backend (`turistas.id` = FK a `usuarios.id` con CASCADE, sin `id_usuario`). Para rehacerla: `mysql -h127.0.0.1 -P3307 -uroot -p columbia_viajes < schema.sql` (borra los datos actuales).
- **Solo el Vendedor carga reservas** (el backend toma el vendedor del token). Admin y Dueño no ven "Hacer reserva".
- **Ranking de vendedores del Dueño**: lo calcula el backend (`GET /paquetes-reservas/ranking-vendedores`); no se calcula en el front.
- Seguridad con Spring Security + Vaadin (no mecanismo casero).

## Errores conocidos del front (al 2026-10-19)
1. `README.md` desactualizado (estructura, login, pendientes).
2. `Dockerfile` usa `--spring.profiles.active=prod` pero no existe `application-prod.properties`; `backend.url` apunta a localhost.
3. `pom.xml` incluye repositorios `vaadin-prereleases` (innecesarios con versión estable).
4. Estilos con variables `--lumo-*` pero el tema cargado es Aura (verificar que se vean bien).
5. `NuevaReservaView` obtiene la sucursal del vendedor buscando su nombre en `/usuarios/vendedores`; se puede reemplazar por `GET /usuarios/me`.
6. `TuristaGridView` no incluye al Dueño, aunque el backend le permite ver turistas.
7. `ReservaGridView` no deja cancelar al Dueño (el backend sí lo permite): confirmar si es a propósito.

## Pendientes del backend (los maneja un compañero en `main` del backend; **no modificar sin consultar**)
- A conversar: endpoint propio para que el Vendedor cree clientes (hoy se usa el registro público, que funciona para cualquier rol).
- El Dueño tiene permisos de escritura sobre casi todo (regla Admin/Dueño); según la consigna solo consulta.
- `DELETE /turistas/{id}` borra solo el turista y deja la cuenta Cliente sin perfil.
- Un 404/400 esperado a veces llega como 401 (despacho de errores sin regla para `DispatcherType.ERROR`).
- JWT dura 15 min (`900000`) pero el comentario dice 24 h; con 15 min el front desloguea seguido.
- No se validan plazas disponibles de vuelos/hoteles al reservar.
- El filtro JWT responde 401 sin cuerpo; `mvnw` sin permiso de ejecución (correr con `sh ./mvnw`).

## Pendientes según la consigna
**Funcionalidad por rol**:
- **Cliente**: registro y consulta de vuelos/hoteles hechos; faltan paquetes, "mis reservas" y "mi perfil" (el backend ya los tiene).
- **Vendedor**: alta y cancelación de reservas y alta de turistas (con cuenta) hechas; faltan edición de reserva y edición de turistas.
- **Administrador**: faltan los ABM (alta/edición/baja) de usuarios, sucursales, hoteles, vuelos, paquetes y ciudades (hoy solo lectura).
- **Dueño**: falta la vista del ranking de vendedores (el endpoint ya existe).
- Cambio de contraseña (el backend ya tiene `POST /auth/cambiar-contrasenia`).

**Entregables** (`../Consignas`):
- Repo en GitHub completo y ejecutable, con todos los integrantes como colaboradores activos.
- Documento PDF: portada, link al repo, descripción (tema, objetivos, actores), arquitectura y capas, clases principales, capturas de código, diagrama de casos de uso + descripción de cada caso, DER + descripción de tablas, manual de usuario por actor con capturas.
