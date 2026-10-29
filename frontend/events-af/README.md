# Frontend de eventos

Aplicación Angular 20 para registrar y consultar eventos.

## Ejecutar el frontend

Requisitos: Node.js 22.12 o posterior de la rama 22, npm y el backend iniciado en `http://localhost:8080`.

Desde la carpeta `frontend/events-af`:

```bash
npm ci
npm start
```

Abrir `http://localhost:4200/events`. El proxy de desarrollo reenvía las peticiones de `/eventos` al backend.

## Ejecutar las pruebas

```bash
npm test
```

Las pruebas se ejecutan en el navegador y se repiten al modificar archivos. Para detenerlas, presionar `Ctrl+C`.

## Arquitectura

El frontend usa Angular 20 con componentes standalone y se organiza por funcionalidad en `src/app/features/events`:

- **EventsPage** coordina el registro, las consultas, los estados de pantalla y el modal de auditoría.
- **EventForm** administra y valida el formulario; **EventList** muestra la tabla y permite solicitar la auditoría.
- **EventService** concentra las peticiones HTTP al backend.
- **event.model.ts** define los DTO y el enum de tipos; **event-messages.ts** centraliza los mensajes.

## Registro y consulta de eventos

- El formulario requiere identificador externo, tipo, fecha y hora, y descripción.
- El selector obtiene sus opciones del enum `EventoTipo`: `PRODUCTION` y `QUALITY`.
- La fecha se ingresa con el selector nativo del navegador. Se envía como texto ISO sin zona horaria ni conversión a UTC, de acuerdo con `LocalDateTime`. La tabla la muestra como `dd/MM/yyyy HH:mm:ss`.
- El registro utiliza `POST /eventos`. Si funciona, se confirma el resultado y se actualiza el listado mediante `GET /eventos`.
- Los datos se conservan después del envío. Para comprobar duplicados, reenviar el mismo identificador y verificar el mensaje correspondiente al HTTP 409. El botón **Limpiar** permite comenzar otro registro.
- La auditoría de los eventos `QUALITY` se genera en el backend. El botón **Ver** de la tabla permite consultarla en un modal.
