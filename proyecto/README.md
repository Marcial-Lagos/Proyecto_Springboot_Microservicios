# FoodExpress — Proyecto Semestral de Microservicios

Sistema de pedidos de comida construido con Spring Boot 4.0.6, Java 21 y una arquitectura de microservicios.

## Componentes

| Componente | Puerto | Responsabilidad |
|---|---:|---|
| API Gateway | 8080 (host) | Punto de entrada, enrutamiento y validación JWT |
| Eureka Server | 8761 (host) | Registro y descubrimiento de servicios |
| MySQL 8.4 | 3307 (host) → 3306 (contenedor) | Motor de desarrollo con una base lógica por microservicio, publicado solo en localhost |
| ms-auth | 8083 (interno) | Registro, login y emisión JWT |
| ms-usuarios | 8081 (interno) | Perfiles de usuario |
| ms-productos | 8082 (interno) | Productos y stock |
| ms-pedidos | 8084 (interno) | Pedidos y comunicación con productos |
| ms-pagos | 8085 (interno) | Procesamiento y reembolso de pagos |
| ms-envios | 8086 (interno) | Seguimiento y estados de envíos |
| ms-inventario | 8087 (interno) | Existencias y ajustes de stock |
| ms-notificaciones | 8088 (interno) | Notificaciones de usuario |
| ms-resenas | 8089 (interno) | Reseñas de productos |
| ms-cupones | 8090 (interno) | Cupones y descuentos |

## Decisiones de arquitectura

- Cada microservicio mantiene una base propia: `db_usuario` (auth), `db_usuarios`, `db_productos`, `db_pedidos`, `db_pagos`, `db_envios`, `db_inventario`, `db_notificaciones`, `db_resenas` y `db_cupones`.
- En desarrollo, un contenedor MySQL crea las diez bases; los servicios acceden por la red privada de Compose y sus puertos no se publican en el host.
- Eureka resuelve el descubrimiento de servicios. El Gateway enruta las APIs por `lb://`.
- La documentación OpenAPI se expone en cada servicio y también queda centralizada en el Gateway.
- Las pruebas son unitarias, no requieren MySQL y utilizan JUnit 5 + Mockito.

## Levantar todo con Docker

Requisitos: Docker Desktop o Docker Engine con el plugin Docker Compose.

Desde la carpeta `proyecto`:

```bash
Copy-Item .env.example .env
# Sustituya los valores de ejemplo en .env por valores locales.
docker compose up --build -d
```

`.env` is ignored by Git. The example values are for local development only; replace the password and JWT values with unique generated values and never reuse them in production. `MYSQL_USERNAME` is set to the existing MySQL root account in the local example. When reusing an existing `mysql_data` volume, set `MYSQL_ROOT_PASSWORD` to the password already stored in that database volume; Compose does not rotate credentials in an existing volume.

Verificar estado:

```bash
docker compose ps
```

Detener y conservar datos:

```bash
docker compose down
```

Detener y eliminar también la base de datos local:

```bash
docker compose down -v
```

## URLs principales

- Gateway: `http://localhost:8080`
- Eureka: `http://localhost:8761`
- Swagger centralizado: `http://localhost:8080/doc/swagger-ui.html`

Los servicios de negocio solo escuchan dentro de la red de Compose; usa el Gateway como punto de entrada. La configuración de desarrollo del Gateway define las rutas OpenAPI `/auth/v3/api-docs`, `/usuarios/v3/api-docs`, `/productos/v3/api-docs`, `/pedidos/v3/api-docs`, `/pagos/v3/api-docs`, `/envios/v3/api-docs`, `/inventario/v3/api-docs`, `/notificaciones/v3/api-docs`, `/resenas/v3/api-docs` y `/cupones/v3/api-docs`.

El puerto host de MySQL es `3307` y está enlazado a `127.0.0.1`; los servicios usan `mysql:3306` dentro de Compose.

## Ejecutar pruebas

El proyecto ahora tiene un `pom.xml` agregador. Desde la carpeta `proyecto`:

```bash
mvn clean test
```

Hay **cinco pruebas unitarias en cada uno de los diez microservicios de negocio**, para un total mínimo de **50 pruebas JUnit 5 + Mockito**.

Para probar un módulo específico:

```bash
mvn -pl ms-productos test
```

## Autenticación

1. Registrar un usuario:

```http
POST http://localhost:8080/api/v1/auth/register
Content-Type: application/json

{
  "nombre": "Usuario Demo",
  "email": "demo@foodexpress.cl",
  "password": "secreto123"
}
```

2. Iniciar sesión:

```http
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
  "email": "demo@foodexpress.cl",
  "password": "secreto123"
}
```

3. Para rutas protegidas, enviar:

```http
Authorization: Bearer <token>
```

## Correcciones aplicadas

- Java 21 y Spring Boot 4.0.6 fijados en todos los módulos, alineados con los repositorios docentes.
- Dependencias Maven duplicadas eliminadas.
- `pom.xml` agregador incorporado para pruebas y construcción coordinada.
- Swagger/OpenAPI 3.0.3 estandarizado en los diez microservicios y centralizado en el Gateway, compatible con Spring Boot 4.
- Dockerfile multi-stage añadido a Gateway, Eureka y todos los microservicios.
- `docker-compose.yml` crea MySQL, Eureka, Gateway y los diez microservicios.
- Propiedades de base de datos y Eureka parametrizadas con variables de entorno; se eliminó la contraseña local expuesta en archivos de configuración.
- Se añadió `@Builder.Default` a estados y valores iniciales de entidades. Esto evita productos/cupones inactivos por defecto y corrige el rol nulo al registrar usuarios en `ms-auth`.
- Pruebas heredadas inválidas de `ms-auth` reemplazadas por pruebas unitarias coherentes con el código actual.
