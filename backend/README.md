# Prueba Técnica - API de Gestión de Eventos

API REST desarrollada con **Spring Boot 3** y **Java 17** para registrar y consultar eventos de una fábrica.

## Instalación y ejecución

Requisitos: Java 17 y Maven 3.9+. Para ejecutar en contenedores, Docker con Compose y el motor de Docker iniciado. Los comandos se ejecutan desde la carpeta `backend`.

### Ejecutar con Maven

```sh
git clone https://github.com/afkfarias/afarias-challenge.git
cd afarias-challenge/backend
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

### Ejecutar con Docker

El Dockerfile utiliza el JAR generado previamente:

```sh
mvn clean package -DskipTests
docker build -t event-challenge-afarias .
docker run --rm -p 8080:8080 event-challenge-afarias
```

### Ejecutar con Docker Compose

```sh
mvn clean package -DskipTests
docker compose up --build
```

Para detener los contenedores:

```sh
docker compose down
```

## Documentación de la API

Swagger UI: http://localhost:8080/swagger-ui/index.html

- `POST /eventos`: registra un evento.
- `GET /eventos`: lista los eventos registrados.
- `GET /eventos/{eventoId}/auditoria`: consulta la auditoría por **ID interno del evento**, no por el identificador externo `eventId`.

## Arquitectura y decisiones

- Capas `controller → service → repository`: el controlador recibe y valida solicitudes; el servicio aplica las reglas de negocio; los repositorios persisten mediante Spring Data JPA.
- Los DTO definen el contrato JSON y MapStruct realiza el mapeo con las entidades. `GlobalExceptionHandler` centraliza las respuestas de error.
- H2 funciona en memoria y Hibernate crea las tablas al iniciar. **Los datos se pierden al reiniciar la aplicación o el contenedor**; no requiere una base externa.
- `timestamp` lo envía el cliente porque representa cuándo ocurrió el evento en el sistema de origen. Así se conserva ese momento aunque el envío llegue después. Se usa `LocalDateTime` en formato ISO 8601 sin zona horaria, por ejemplo `2026-05-20T10:00:00`.
- `createdAt` de la auditoría lo genera el backend porque representa cuándo registró la auditoría del evento QUALITY, un dato de su procesamiento interno. Ambas fechas se persisten en la base del backend y pueden ser diferentes.
- `eventId` identifica el evento de origen y lo proporciona el cliente. Se verifica su existencia y se aplica una restricción única en la base para cubrir envíos simultáneos.
- PRODUCTION solo se registra. QUALITY genera además una auditoría; ambas escrituras pertenecen a una misma transacción. Cada evento puede tener como máximo una auditoría.

## Tests

```sh
mvn test
```

JUnit 5, Mockito y MockMvc cubren el servicio y el controlador: registro de ambos tipos, duplicados, auditorías, validaciones y respuestas de error.

## Comprobar duplicados y auditoría

Desde Swagger, enviar a `POST /eventos`:

```json
{
  "eventId": "EVT-1001",
  "timestamp": "2026-05-20T10:00:00",
  "eventType": "QUALITY",
  "description": "Inspección de calidad completada"
}
```

1. El primer envío devuelve `201`. Guardar el `id` interno de la respuesta.
2. Consultar `GET /eventos/{id}/auditoria`: devuelve `200` con el identificador de la auditoría, la referencia al evento y `createdAt`.
3. Reenviar el mismo payload: devuelve `409`. `GET /eventos` conserva un solo evento con ese `eventId` y la consulta de auditoría devuelve el mismo registro.
4. Crear otro evento con `eventId` diferente y tipo PRODUCTION: devuelve `201`; su consulta de auditoría devuelve `404`.

Los datos inválidos devuelven `400`; las auditorías inexistentes, `404`; los duplicados, `409`; los errores inesperados, `500`.

## Posibles mejoras

- Base persistente y migraciones de esquema.
- Pruebas de integración para concurrencia y rollback de auditoría.
- Paginación y logs estructurados para facilitar el seguimiento de los eventos.
