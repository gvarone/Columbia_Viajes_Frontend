-- =====================================================================
-- Columbia Viajes - Datos iniciales adicionales
-- Se ejecutan DESPUÉS de schema.sql (del repo del backend), sobre una base
-- recién creada: los IDs continúan los que trae schema.sql.
--
--   mysql -h127.0.0.1 -P3307 -uroot -p columbia_viajes < schema.sql
--   mysql -h127.0.0.1 -P3307 -uroot -p columbia_viajes < datos_iniciales.sql
--
-- Todas las cuentas de muestra quedan con la clave de prueba "test1234"
-- (los clientes nuevos al crearse y las cuentas de schema.sql con el UPDATE del final).
-- Para usar otra clave, generar su hash BCrypt y reemplazarlo en @hash_prueba:
--
--   python3 -c 'import bcrypt,getpass; print(bcrypt.hashpw(getpass.getpass().encode(), bcrypt.gensalt(10)).decode())'
--
-- =====================================================================

SET NAMES utf8mb4;
START TRANSACTION;

-- Hash BCrypt de la clave de prueba "test1234" (verificado con BCryptPasswordEncoder de Spring)
SET @hash_prueba = '$2b$10$8NHN.fEuyCReLMImtTh9HONA3HOxoNGwTs1wMOqd4tLAaKTAhkZES';

-- ---------- Ciudades (8: Santiago de Chile queda sin uso, para mostrar una baja) ----------
INSERT INTO `ciudades` (`id`, `nombre`) VALUES (5,'Mendoza');
INSERT INTO `ciudades` (`id`, `nombre`) VALUES (6,'Salta');
INSERT INTO `ciudades` (`id`, `nombre`) VALUES (7,'Florianópolis');
INSERT INTO `ciudades` (`id`, `nombre`) VALUES (8,'Santiago de Chile');

-- ---------- Hoteles (6: Pousada Ilha Bela queda sin paquetes, para mostrar una baja) ----------
INSERT INTO `hoteles` (`id`, `nombre`, `direccion`, `id_ciudad`, `telefono`, `plazas_totales`) VALUES (2,'Hotel Atlántico Copacabana','Av. Atlântica 1702, Copacabana',2,'552125550100',150);
INSERT INTO `hoteles` (`id`, `nombre`, `direccion`, `id_ciudad`, `telefono`, `plazas_totales`) VALUES (3,'Hotel Plaza Cusco','Plaza de Armas 120',3,'5184240100',90);
INSERT INTO `hoteles` (`id`, `nombre`, `direccion`, `id_ciudad`, `telefono`, `plazas_totales`) VALUES (4,'Hotel Andes Malbec','Av. San Martín 1250',5,'2614250300',110);
INSERT INTO `hoteles` (`id`, `nombre`, `direccion`, `id_ciudad`, `telefono`, `plazas_totales`) VALUES (5,'Hotel Colonial Salta','Caseros 545',6,'3874310200',70);
INSERT INTO `hoteles` (`id`, `nombre`, `direccion`, `id_ciudad`, `telefono`, `plazas_totales`) VALUES (6,'Pousada Ilha Bela','Rua das Gaivotas 88, Canasvieiras',7,'554832660400',40);

-- ---------- Vuelos (ida y vuelta de cada paquete; todas las salidas son futuras) ----------
-- CABA -> Rio de Janeiro
INSERT INTO `vuelos` (`id`, `id_ciudad_origen`, `id_ciudad_destino`, `fecha_salida`, `fecha_llegada`, `plazas_turista_totales`, `plazas_primera_totales`) VALUES (3,1,2,'2026-12-10 07:00:00','2026-12-10 10:00:00',160,16);
-- Rio de Janeiro -> CABA
INSERT INTO `vuelos` (`id`, `id_ciudad_origen`, `id_ciudad_destino`, `fecha_salida`, `fecha_llegada`, `plazas_turista_totales`, `plazas_primera_totales`) VALUES (4,2,1,'2026-12-17 15:00:00','2026-12-17 18:00:00',160,16);
-- CABA -> Cusco
INSERT INTO `vuelos` (`id`, `id_ciudad_origen`, `id_ciudad_destino`, `fecha_salida`, `fecha_llegada`, `plazas_turista_totales`, `plazas_primera_totales`) VALUES (5,1,3,'2027-01-05 06:00:00','2027-01-05 11:30:00',140,12);
-- Cusco -> CABA
INSERT INTO `vuelos` (`id`, `id_ciudad_origen`, `id_ciudad_destino`, `fecha_salida`, `fecha_llegada`, `plazas_turista_totales`, `plazas_primera_totales`) VALUES (6,3,1,'2027-01-12 13:00:00','2027-01-12 20:00:00',140,12);
-- CABA -> Mendoza
INSERT INTO `vuelos` (`id`, `id_ciudad_origen`, `id_ciudad_destino`, `fecha_salida`, `fecha_llegada`, `plazas_turista_totales`, `plazas_primera_totales`) VALUES (7,1,5,'2027-02-13 08:00:00','2027-02-13 09:50:00',120,10);
-- Mendoza -> CABA
INSERT INTO `vuelos` (`id`, `id_ciudad_origen`, `id_ciudad_destino`, `fecha_salida`, `fecha_llegada`, `plazas_turista_totales`, `plazas_primera_totales`) VALUES (8,5,1,'2027-02-17 19:00:00','2027-02-17 20:50:00',120,10);
-- CABA -> Salta
INSERT INTO `vuelos` (`id`, `id_ciudad_origen`, `id_ciudad_destino`, `fecha_salida`, `fecha_llegada`, `plazas_turista_totales`, `plazas_primera_totales`) VALUES (9,1,6,'2027-03-20 09:00:00','2027-03-20 11:15:00',120,10);
-- Salta -> CABA
INSERT INTO `vuelos` (`id`, `id_ciudad_origen`, `id_ciudad_destino`, `fecha_salida`, `fecha_llegada`, `plazas_turista_totales`, `plazas_primera_totales`) VALUES (10,6,1,'2027-03-25 17:00:00','2027-03-25 19:15:00',120,10);

-- ---------- Paquetes (sin imagen: se cargan desde Administración > Paquetes) ----------
INSERT INTO `paquetes` (`id`, `id_sucursal`, `id_vuelo_ida`, `id_vuelo_vuelta`, `id_hotel`, `nombre`, `descripcion`, `fecha_llegada_hotel`, `fecha_partida_hotel`, `precio`, `fecha_creacion`, `imagen`) VALUES (2,2,3,4,2,'Río de Janeiro 7 noches','Playas de Copacabana e Ipanema, Cristo Redentor y Pan de Azúcar. Vuelos directos desde Buenos Aires.','2026-12-10','2026-12-17',950000.00,'2026-10-15 10:00:00',NULL);
INSERT INTO `paquetes` (`id`, `id_sucursal`, `id_vuelo_ida`, `id_vuelo_vuelta`, `id_hotel`, `nombre`, `descripcion`, `fecha_llegada_hotel`, `fecha_partida_hotel`, `precio`, `fecha_creacion`, `imagen`) VALUES (3,3,5,6,3,'Cusco y Machu Picchu','Siete noches en el centro histórico de Cusco, con tiempo para visitar el Valle Sagrado y Machu Picchu.','2027-01-05','2027-01-12',1250000.00,'2026-10-15 10:00:00',NULL);
INSERT INTO `paquetes` (`id`, `id_sucursal`, `id_vuelo_ida`, `id_vuelo_vuelta`, `id_hotel`, `nombre`, `descripcion`, `fecha_llegada_hotel`, `fecha_partida_hotel`, `precio`, `fecha_creacion`, `imagen`) VALUES (4,3,7,8,4,'Mendoza entre viñedos','Cuatro noches para recorrer bodegas del Valle de Uco y Luján de Cuyo.','2027-02-13','2027-02-17',520000.00,'2026-10-15 10:00:00',NULL);
INSERT INTO `paquetes` (`id`, `id_sucursal`, `id_vuelo_ida`, `id_vuelo_vuelta`, `id_hotel`, `nombre`, `descripcion`, `fecha_llegada_hotel`, `fecha_partida_hotel`, `precio`, `fecha_creacion`, `imagen`) VALUES (5,2,9,10,5,'Salta la linda','Cinco noches: Quebrada de Humahuaca, Cafayate y el Tren a las Nubes.','2027-03-20','2027-03-25',610000.00,'2026-10-15 10:00:00',NULL);
INSERT INTO `paquetes` (`id`, `id_sucursal`, `id_vuelo_ida`, `id_vuelo_vuelta`, `id_hotel`, `nombre`, `descripcion`, `fecha_llegada_hotel`, `fecha_partida_hotel`, `precio`, `fecha_creacion`, `imagen`) VALUES (6,2,1,2,1,'Bariloche gastronómico','Mismos vuelos que "Bariloche entre lagos", con circuito de cervecerías y chocolaterías. Sin reservas: sirve de ejemplo de baja.','2026-11-15','2026-11-22',990000.00,'2026-10-15 10:00:00',NULL);

-- ---------- Clientes: primero la cuenta (usuarios, rol 1 = Cliente), después el turista con el MISMO id ----------
INSERT INTO `usuarios` (`id`, `nombre`, `contrasenia`, `id_rol`, `fecha_creacion`, `id_sucursal`, `version_autenticacion`) VALUES (8,'maria_gomez',@hash_prueba,1,'2026-10-12 09:00:00',NULL,0);
INSERT INTO `usuarios` (`id`, `nombre`, `contrasenia`, `id_rol`, `fecha_creacion`, `id_sucursal`, `version_autenticacion`) VALUES (9,'lucas_fernandez',@hash_prueba,1,'2026-10-12 09:00:00',NULL,0);
INSERT INTO `usuarios` (`id`, `nombre`, `contrasenia`, `id_rol`, `fecha_creacion`, `id_sucursal`, `version_autenticacion`) VALUES (10,'sofia_rodriguez',@hash_prueba,1,'2026-10-12 09:00:00',NULL,0);
INSERT INTO `usuarios` (`id`, `nombre`, `contrasenia`, `id_rol`, `fecha_creacion`, `id_sucursal`, `version_autenticacion`) VALUES (11,'martin_lopez',@hash_prueba,1,'2026-10-12 09:00:00',NULL,0);
INSERT INTO `usuarios` (`id`, `nombre`, `contrasenia`, `id_rol`, `fecha_creacion`, `id_sucursal`, `version_autenticacion`) VALUES (12,'valentina_diaz',@hash_prueba,1,'2026-10-12 09:00:00',NULL,0);
INSERT INTO `turistas` (`id`, `nombre`, `apellidos`, `direccion`, `email`, `telefono`) VALUES (8,'María','Gómez','Av. Santa Fe 2450, CABA','maria.gomez@example.com','1145671234');
INSERT INTO `turistas` (`id`, `nombre`, `apellidos`, `direccion`, `email`, `telefono`) VALUES (9,'Lucas','Fernández','Calle 7 N° 1120, La Plata','lucas.fernandez@example.com','2214567890');
INSERT INTO `turistas` (`id`, `nombre`, `apellidos`, `direccion`, `email`, `telefono`) VALUES (10,'Sofía','Rodríguez','Bv. Oroño 845, Rosario','sofia.rodriguez@example.com','3414123456');
INSERT INTO `turistas` (`id`, `nombre`, `apellidos`, `direccion`, `email`, `telefono`) VALUES (11,'Martín','López','Av. Colón 3300, Córdoba','martin.lopez@example.com','3514789012');
INSERT INTO `turistas` (`id`, `nombre`, `apellidos`, `direccion`, `email`, `telefono`) VALUES (12,'Valentina','Díaz','Av. Rivadavia 6100, CABA','valentina.diaz@example.com','1156783456');

-- ---------- Reservas (precio = precio del paquete x (1 + 0.10 primera + 0.10 pensión completa);
-- el vendedor siempre es de la sucursal del paquete) ----------
INSERT INTO `paquetes_reservas` (`id`, `id_paquete`, `id_turista`, `clase_vuelo`, `fecha_reserva`, `regimen_hospedaje`, `precio_reserva`, `id_vendedor`) VALUES (2,2,8,'TURISTA','2026-10-20 11:05:00','MEDIA_PENSION',950000.00,4);
INSERT INTO `paquetes_reservas` (`id`, `id_paquete`, `id_turista`, `clase_vuelo`, `fecha_reserva`, `regimen_hospedaje`, `precio_reserva`, `id_vendedor`) VALUES (3,2,9,'PRIMERA','2026-10-21 16:30:00','PENSION_COMPLETA',1140000.00,5);
INSERT INTO `paquetes_reservas` (`id`, `id_paquete`, `id_turista`, `clase_vuelo`, `fecha_reserva`, `regimen_hospedaje`, `precio_reserva`, `id_vendedor`) VALUES (4,2,10,'TURISTA','2026-10-22 10:15:00','PENSION_COMPLETA',1045000.00,4);
INSERT INTO `paquetes_reservas` (`id`, `id_paquete`, `id_turista`, `clase_vuelo`, `fecha_reserva`, `regimen_hospedaje`, `precio_reserva`, `id_vendedor`) VALUES (5,3,11,'PRIMERA','2026-10-18 09:45:00','MEDIA_PENSION',1375000.00,6);
INSERT INTO `paquetes_reservas` (`id`, `id_paquete`, `id_turista`, `clase_vuelo`, `fecha_reserva`, `regimen_hospedaje`, `precio_reserva`, `id_vendedor`) VALUES (6,3,8,'TURISTA','2026-10-23 14:00:00','MEDIA_PENSION',1250000.00,6);
INSERT INTO `paquetes_reservas` (`id`, `id_paquete`, `id_turista`, `clase_vuelo`, `fecha_reserva`, `regimen_hospedaje`, `precio_reserva`, `id_vendedor`) VALUES (7,3,7,'PRIMERA','2026-10-24 12:20:00','PENSION_COMPLETA',1500000.00,6);
INSERT INTO `paquetes_reservas` (`id`, `id_paquete`, `id_turista`, `clase_vuelo`, `fecha_reserva`, `regimen_hospedaje`, `precio_reserva`, `id_vendedor`) VALUES (8,4,9,'TURISTA','2026-10-26 17:10:00','PENSION_COMPLETA',572000.00,6);
INSERT INTO `paquetes_reservas` (`id`, `id_paquete`, `id_turista`, `clase_vuelo`, `fecha_reserva`, `regimen_hospedaje`, `precio_reserva`, `id_vendedor`) VALUES (9,4,10,'TURISTA','2026-10-27 11:00:00','MEDIA_PENSION',520000.00,6);
INSERT INTO `paquetes_reservas` (`id`, `id_paquete`, `id_turista`, `clase_vuelo`, `fecha_reserva`, `regimen_hospedaje`, `precio_reserva`, `id_vendedor`) VALUES (10,5,11,'TURISTA','2026-10-28 15:40:00','MEDIA_PENSION',610000.00,5);
INSERT INTO `paquetes_reservas` (`id`, `id_paquete`, `id_turista`, `clase_vuelo`, `fecha_reserva`, `regimen_hospedaje`, `precio_reserva`, `id_vendedor`) VALUES (11,5,7,'PRIMERA','2026-10-29 10:30:00','MEDIA_PENSION',671000.00,4);
INSERT INTO `paquetes_reservas` (`id`, `id_paquete`, `id_turista`, `clase_vuelo`, `fecha_reserva`, `regimen_hospedaje`, `precio_reserva`, `id_vendedor`) VALUES (12,1,9,'TURISTA','2026-10-30 18:05:00','PENSION_COMPLETA',935000.00,5);

-- ---------- OPCIONAL: nombres más prolijos para los datos de prueba de schema.sql ----------
-- (descomentar si se quiere; no cambia IDs ni relaciones)
-- UPDATE `sucursales` SET `direccion` = 'Av. Corrientes 1500, CABA',   `email` = 'centro@columbiaviajes.com'  WHERE `id` = 2;
-- UPDATE `sucursales` SET `direccion` = 'Av. Cabildo 2200, CABA',      `email` = 'belgrano@columbiaviajes.com' WHERE `id` = 3;
-- UPDATE `hoteles`   SET `nombre` = 'Hotel Lago Azul', `direccion` = 'Av. Bustillo km 5, Bariloche', `telefono` = '2944520100' WHERE `id` = 1;
-- UPDATE `paquetes`  SET `nombre` = 'Bariloche entre lagos', `descripcion` = 'Vuelos CABA-Bariloche ida y vuelta y siete noches frente al lago Nahuel Huapi.' WHERE `id` = 1;

-- ---------- Claves de prueba para las cuentas que trae schema.sql ----------
-- Así se puede entrar con cada rol apenas se puebla la base (todas con "test1234").
UPDATE `usuarios` SET `contrasenia` = @hash_prueba
 WHERE `nombre` IN ('prueba_admin', 'prueba_duenio', 'vendedor_a1', 'vendedor_a2', 'vendedor_b1', 'juan_viajes');

COMMIT;
