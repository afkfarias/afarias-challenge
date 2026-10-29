import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize, Subscription } from 'rxjs';

import { EventForm } from '../event-form/event-form';
import { EventList } from '../event-list/event-list';
import { EVENT_MESSAGES, getEventErrorMessage } from '../event-messages';
import { EventoRequestDto, EventoResponseDto } from '../event.model';
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
  private loadSubscription?: Subscription;

  protected readonly events = signal<EventoResponseDto[]>([]);
  protected readonly loading = signal(false);
  protected readonly loadError = signal<string | null>(null);
  protected readonly saving = signal(false);
  protected readonly saveError = signal<string | null>(null);
  protected readonly saveSuccess = signal<string | null>(null);

  ngOnInit(): void {
    this.loadEvents();
  }

  protected loadEvents(): void {
    this.loadSubscription?.unsubscribe();
    this.loading.set(true);
    this.loadError.set(null);

    this.loadSubscription = this.eventService.getEvents()
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.loading.set(false)),
      )
      .subscribe({
        next: (events) => this.events.set(events),
        error: (error: unknown) => {
          this.loadError.set(getEventErrorMessage(error, 'load'));
        },
      });
  }

  protected createEvent(request: EventoRequestDto): void {
    if (this.saving()) {
      return;
    }

    this.saving.set(true);
    this.saveError.set(null);
    this.saveSuccess.set(null);

    this.eventService.createEvent(request)
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.saving.set(false)),
      )
      .subscribe({
        next: (event) => {
          this.saveSuccess.set(EVENT_MESSAGES.success.created(event.eventId));
          this.loadEvents();
        },
        error: (error: unknown) => {
          this.saveError.set(getEventErrorMessage(error, 'create', request.eventId));
        },
      });
  }
}
