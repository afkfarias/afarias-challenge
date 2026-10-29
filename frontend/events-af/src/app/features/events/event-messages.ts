import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';

export const EVENT_MESSAGES = {
  validation: {
    eventId: 'Ingresá un identificador que no esté vacío.',
    eventType: 'Seleccioná un tipo de evento.',
    timestamp: 'Ingresá una fecha y hora válidas.',
    description: 'Ingresá una descripción que no esté vacía.',
  },
  errors: {
    connection: 'No se pudo conectar con el servidor. Intentá nuevamente.',
    load: 'No se pudieron cargar los eventos. Intentá nuevamente.',
    invalidData: 'Los datos del evento no son válidos. Revisá los campos e intentá nuevamente.',
    create: 'No se pudo registrar el evento. Intentá nuevamente.',
    audit: 'No se pudo consultar la auditoría. Intentá nuevamente.',
    auditNotFound: 'No se encontró la auditoría de este evento.',
    duplicate: (eventId?: string): string => {
      const identifier = eventId?.trim();
      return identifier
        ? 'Ya existe un evento con el identificador ' + identifier + '.'
        : 'Ya existe un evento con ese identificador.';
    },
  },
  success: {
    created: (eventId: string): string => 'Evento ' + eventId + ' registrado correctamente.',
  },
} as const;

export function getEventErrorMessage(
  error: unknown,
  operation: 'load' | 'create' | 'audit',
  eventId?: string,
): string {
  const fallback = EVENT_MESSAGES.errors[operation];

  if (!(error instanceof HttpErrorResponse)) {
    return fallback;
  }

  if (error.status === 0) {
    return EVENT_MESSAGES.errors.connection;
  }

  if (operation === 'audit' && error.status === HttpStatusCode.NotFound) {
    return EVENT_MESSAGES.errors.auditNotFound;
  }

  if (operation === 'create') {
    switch (error.status) {
      case HttpStatusCode.BadRequest:
        return EVENT_MESSAGES.errors.invalidData;
      case HttpStatusCode.Conflict:
        return EVENT_MESSAGES.errors.duplicate(eventId);
    }
  }

  return fallback;
}
