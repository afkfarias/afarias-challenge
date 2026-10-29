# afarias-challenge

Solución fullstack para registrar y consultar eventos, evitar duplicados y generar auditorías para eventos de tipo `QUALITY`.

## Estructura y documentación

- [Backend](backend/README.md): API REST con Java 17 y Spring Boot 3. Incluye instrucciones de ejecución, pruebas, Swagger y ejemplos para verificar duplicados y auditorías.
- [Frontend](frontend/events-af/README.md): aplicación Angular 20. Incluye instalación, ejecución, pruebas y organización de los componentes.

## Decisiones principales

- **Separación de responsabilidades:** el backend organiza la lógica en controladores, servicios y repositorios. El frontend utiliza componentes standalone agrupados por funcionalidad, con formulario y tabla en una misma pantalla y un servicio para las peticiones HTTP.
- **Contrato de la API:** se utilizan DTO para intercambiar datos y un enum para los tipos `PRODUCTION` y `QUALITY`. MapStruct realiza el mapeo de las entidades en el backend.
- **Persistencia sencilla:** se utiliza H2 en memoria para facilitar la ejecución. Los datos se pierden al reiniciar el backend.
- **Duplicados controlados en el backend:** el cliente proporciona el `eventId` y puede reenviarlo. Se valida su existencia y se aplica una restricción única en la base de datos.
- **Auditoría transaccional:** `PRODUCTION` solo registra el evento; `QUALITY` genera además una auditoría dentro de la misma transacción, con un máximo de una auditoría por evento.
- **Fechas:** `timestamp` se ingresa desde el frontend y representa cuándo ocurrió el evento. Se envía como fecha y hora local sin zona horaria. La fecha `createdAt` de la auditoría la genera el backend.
- **Mensajes consistentes:** el backend centraliza las excepciones y devuelve códigos HTTP adecuados; el frontend centraliza los textos y muestra estados de carga, confirmación y error.

## Puesta en marcha

El backend puede ejecutarse con **Docker o Docker Compose**, exponiendo la API en `http://localhost:8080`. Seguir los pasos del [README del backend](backend/README.md#instalación-y-ejecución); el Dockerfile actual requiere generar previamente el JAR, como se indica en ese manual.

Con el backend en ejecución, iniciar el frontend localmente desde la raíz del repositorio:

```bash
cd frontend/events-af
npm ci
npm start
```

Abrir `http://localhost:4200/events`. El frontend utiliza el proxy de desarrollo para comunicarse con el backend del contenedor. Más detalles en el [README del frontend](frontend/events-af/README.md).

Los comandos de pruebas y los pasos para comprobar duplicados y auditorías se encuentran en la documentación de cada parte.

## Mejoras futuras

Incorporar una base persistente con migraciones, pruebas de integración de concurrencia y rollback, paginación y logs estructurados.
