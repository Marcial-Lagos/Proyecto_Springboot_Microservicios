# FoodExpress — Proyecto Semestral de Microservicios

Sistema de pedidos de comida construido con Spring Boot 4.0.6, Java 21 y una arquitectura de microservicios.

## Componentes

| Componente | Puerto | Responsabilidad |
|---|---:|---|
| API Gateway | 8080 | Punto de entrada, enrutamiento y validación JWT |
| Eureka Server | 8761 | Registro y descubrimiento de servicios |
| MySQL 8.4 | 3306 | Motor único de desarrollo con una base lógica por microservicio |
| ms-auth | 8089 | Registro, login y emisión JWT |
| ms-usuarios | 8081 | Perfiles de usuario |
| ms-productos | 8082 | Productos y stock |
| ms-pedidos | 8083 | Pedidos y comunicación con productos |
| ms-pagos | 8084 | Procesamiento y reembolso de pagos |
| ms-envios | 8085 | Seguimiento y estados de envíos |
| ms-inventario | 8086 | Existencias y ajustes de stock |
| ms-notificaciones | 8087 | Notificaciones de usuario |
| ms-resenas | 8088 | Reseñas de productos |
| ms-cupones | 8090 | Cupones y descuentos |

## Decisiones de arquitectura

- Cada microservicio mantiene una base propia: `db_auth`, `db_usuarios`, `db_productos`, `db_pedidos`, `db_pagos`, `db_envios`, `db_inventario`, `db_notificaciones`, `db_resenas` y `db_cupones`.
- En desarrollo, un solo contenedor MySQL crea las diez bases y el usuario técnico `foodexpress`. Esto simplifica la ejecución local sin perder el aislamiento lógico de datos.
- Eureka resuelve el descubrimiento de servicios. El Gateway enruta las APIs por `lb://`.
- La documentación OpenAPI se expone en cada servicio y también queda centralizada en el Gateway.
- Las pruebas son unitarias, no requieren MySQL y utilizan JUnit 5 + Mockito.

## Levantar todo con Docker

Requisitos: Docker Desktop o Docker Engine con el plugin Docker Compose.

Desde la carpeta `proyecto`:

```bash
docker compose up --build -d
```

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
- Swagger centralizado: `http://localhost:8080/swagger-ui/index.html`

La UI central del Gateway permite seleccionar cualquiera de los diez documentos OpenAPI.

| Microservicio | Swagger directo | Documento OpenAPI por Gateway |
|---|---|---|
| Auth | `http://localhost:8089/swagger-ui.html` | `http://localhost:8080/api/v1/auth/v3/api-docs` |
| Usuarios | `http://localhost:8081/swagger-ui.html` | `http://localhost:8080/api/v1/usuarios/v3/api-docs` |
| Productos | `http://localhost:8082/swagger-ui.html` | `http://localhost:8080/api/v1/productos/v3/api-docs` |
| Pedidos | `http://localhost:8083/swagger-ui.html` | `http://localhost:8080/api/v1/pedidos/v3/api-docs` |
| Pagos | `http://localhost:8084/swagger-ui.html` | `http://localhost:8080/api/v1/pagos/v3/api-docs` |
| Envíos | `http://localhost:8085/swagger-ui.html` | `http://localhost:8080/api/v1/envios/v3/api-docs` |
| Inventario | `http://localhost:8086/swagger-ui.html` | `http://localhost:8080/api/v1/inventario/v3/api-docs` |
| Notificaciones | `http://localhost:8087/swagger-ui.html` | `http://localhost:8080/api/v1/notificaciones/v3/api-docs` |
| Reseñas | `http://localhost:8088/swagger-ui.html` | `http://localhost:8080/api/v1/resenas/v3/api-docs` |
| Cupones | `http://localhost:8090/swagger-ui.html` | `http://localhost:8080/api/v1/cupones/v3/api-docs` |

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
