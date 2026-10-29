import { HttpErrorResponse } from '@angular/common/http';
import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs';

import { EventForm } from '../event-form/event-form';
import { EventList } from '../event-list/event-list';
import { EventoResponseDto } from '../event.model';
import { EventService } from '../event.service';

@Component({
  selector: 'app-events-page',
  imports: [EventForm, EventList],
  templateUrl: './events-page.html',
  styleUrl: './events-page.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EventsPage implements OnInit {
  private readonly eventService = inject(EventService);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly events = signal<EventoResponseDto[]>([]);
  protected readonly loading = signal(false);
  protected readonly loadError = signal<string | null>(null);

  ngOnInit(): void {
    this.loadEvents();
  }

  protected loadEvents(): void {
    if (this.loading()) {
      return;
    }

    this.loading.set(true);
    this.loadError.set(null);

    this.eventService.getEvents()
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (events) => this.events.set(events),
        error: (error: HttpErrorResponse) => {
          this.loadError.set(
            error.status === 0
              ? 'No se pudo conectar con el servidor. Intentá nuevamente.'
              : 'No se pudieron cargar los eventos. Intentá nuevamente.',
          );
        },
      });
  }
}
