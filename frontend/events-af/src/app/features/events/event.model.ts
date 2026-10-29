export enum EventoTipo {
  PRODUCTION = 'PRODUCTION',
  QUALITY = 'QUALITY',
}

export interface EventoRequestDto {
  eventId: string;
  eventType: EventoTipo;
  description: string;
  timestamp: string;
}

export interface EventoResponseDto {
  id: number;
  eventId: string;
  eventType: EventoTipo;
  description: string;
  timestamp: string;
}

export interface AuditoriaResponseDto {
  id: number;
  eventoId: number;
  eventId: string;
  createdAt: string;
}

export interface ErrorResponse {
  mensaje: string;
  codigo: number;
}
