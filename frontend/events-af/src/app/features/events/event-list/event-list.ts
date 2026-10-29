import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import { EventoResponseDto } from '../event.model';

@Component({
  selector: 'app-event-list',
  imports: [DatePipe],
  templateUrl: './event-list.html',
  styleUrl: './event-list.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EventList {
  readonly events = input.required<EventoResponseDto[]>();
}
