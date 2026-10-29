import { ChangeDetectionStrategy, Component, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { EVENT_MESSAGES } from '../event-messages';
import { EventoRequestDto, EventoTipo } from '../event.model';

@Component({
  selector: 'app-event-form',
  imports: [ReactiveFormsModule],
  templateUrl: './event-form.html',
  styleUrl: './event-form.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EventForm {
  private readonly formBuilder = inject(FormBuilder).nonNullable;

  readonly submitting = input(false);
  readonly submitted = output<EventoRequestDto>();

  readonly form = this.formBuilder.group({
    eventId: ['', [Validators.required, Validators.pattern(/\S/)]],
    eventType: this.formBuilder.control<EventoTipo>(EventoTipo.PRODUCTION, Validators.required),
    timestamp: [
      '',
      [
        Validators.required,
        Validators.pattern(/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}(?::\d{2})?$/),
      ],
    ],
    description: ['', [Validators.required, Validators.pattern(/\S/)]],
  });

  protected readonly eventTypes = Object.values(EventoTipo);
  protected readonly messages = EVENT_MESSAGES;

  protected isInvalid(field: keyof EventoRequestDto): boolean {
    const control = this.form.controls[field];
    return control.invalid && control.touched;
  }

  protected submit(): void {
    if (this.submitting()) {
      return;
    }

    this.form.markAllAsTouched();

    if (this.form.invalid) {
      return;
    }

    const values = this.form.getRawValue();
    const request: EventoRequestDto = {
      ...values,
      eventId: values.eventId.trim(),
      description: values.description.trim(),
      timestamp: values.timestamp.length === 16 ? values.timestamp + ':00' : values.timestamp,
    };

    this.form.patchValue(request);
    this.submitted.emit(request);
  }

  protected resetForm(): void {
    if (this.submitting()) {
      return;
    }

    this.form.reset({
      eventId: '',
      eventType: EventoTipo.PRODUCTION,
      timestamp: '',
      description: '',
    });
  }
}
