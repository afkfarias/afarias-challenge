import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';

import { EventoResponseDto, EventoTipo } from '../event.model';

@Component({
  selector: 'app-event-list',
  imports: [DatePipe],
  templateUrl: './event-list.html',
  styleUrl: './event-list.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EventList {
  readonly events = input.required<EventoResponseDto[]>();
  readonly auditRequested = output<EventoResponseDto>();

  protected readonly eventTypes = EventoTipo;
}
