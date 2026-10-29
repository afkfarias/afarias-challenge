import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { AuditoriaResponseDto, EventoRequestDto, EventoResponseDto } from './event.model';

@Injectable({
  providedIn: 'root',
})
export class EventService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/eventos';

  getEvents(): Observable<EventoResponseDto[]> {
    return this.http.get<EventoResponseDto[]>(this.baseUrl);
  }

  createEvent(request: EventoRequestDto): Observable<EventoResponseDto> {
    return this.http.post<EventoResponseDto>(this.baseUrl, request);
  }

  getEventAudit(eventoId: number): Observable<AuditoriaResponseDto> {
    return this.http.get<AuditoriaResponseDto>(`${this.baseUrl}/${eventoId}/auditoria`);
  }
}
