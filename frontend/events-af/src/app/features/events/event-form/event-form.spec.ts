import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventoRequestDto, EventoTipo } from '../event.model';
import { EventForm } from './event-form';

describe('EventForm', () => {
  let fixture: ComponentFixture<EventForm>;
  let component: EventForm;
  let submitted: jasmine.Spy;

  const request: EventoRequestDto = {
    eventId: 'EVT-1001',
    eventType: EventoTipo.PRODUCTION,
    timestamp: '2026-05-20T10:00:00',
    description: 'Inicio de producción',
  };

  function submitForm(): void {
    const form = (fixture.nativeElement as HTMLElement).querySelector('form');
    form?.dispatchEvent(new Event('submit', { bubbles: true, cancelable: true }));
    fixture.detectChanges();
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventForm],
    }).compileComponents();

    fixture = TestBed.createComponent(EventForm);
    component = fixture.componentInstance;
    submitted = jasmine.createSpy('submitted');
    component.submitted.subscribe(submitted);
    fixture.detectChanges();
  });

  it('shows required errors and does not submit an empty form', () => {
    submitForm();

    const element = fixture.nativeElement as HTMLElement;

    expect(submitted).not.toHaveBeenCalled();
    expect(element.querySelector('#event-id-error')).not.toBeNull();
    expect(element.querySelector('#event-timestamp-error')).not.toBeNull();
    expect(element.querySelector('#event-description-error')).not.toBeNull();
  });

  it('rejects identifiers and descriptions containing only whitespace', () => {
    component.form.setValue({
      ...request,
      eventId: '   ',
      description: ' \n ',
    });

    submitForm();

    expect(submitted).not.toHaveBeenCalled();
    expect(component.form.controls.eventId.invalid).toBeTrue();
    expect(component.form.controls.description.invalid).toBeTrue();
  });

  it('submits entered values without changing their local time', () => {
    const element = fixture.nativeElement as HTMLElement;

    for (const [selector, value] of [
      ['#event-id', '  EVT-1001  '],
      ['#event-timestamp', '2026-05-20T10:00'],
      ['#event-description', '  Inicio de producción  '],
    ]) {
      const inputElement = element.querySelector<HTMLInputElement | HTMLTextAreaElement>(selector);
      if (!inputElement) {
        throw new Error('No se encontro el campo ' + selector);
      }
      inputElement.value = value;
      inputElement.dispatchEvent(new Event('input', { bubbles: true }));
    }

    submitForm();

    expect(submitted).toHaveBeenCalledOnceWith(request);
    expect(component.form.getRawValue()).toEqual(request);
  });

  it('allows QUALITY events and preserves entered seconds', () => {
    component.form.setValue({
      ...request,
      eventType: EventoTipo.QUALITY,
      timestamp: '2026-05-20T11:30:45',
    });

    submitForm();

    expect(submitted).toHaveBeenCalledOnceWith({
      ...request,
      eventType: EventoTipo.QUALITY,
      timestamp: '2026-05-20T11:30:45',
    });
  });

  it('rejects timestamps with a timezone instead of a local date and time', () => {
    component.form.setValue({
      ...request,
      timestamp: '2026-05-20T10:00:00Z',
    });

    submitForm();

    expect(submitted).not.toHaveBeenCalled();
    expect(component.form.controls.timestamp.invalid).toBeTrue();
  });

  it('disables the form and prevents another submission while saving', () => {
    component.form.setValue(request);
    fixture.componentRef.setInput('submitting', true);
    fixture.detectChanges();

    submitForm();

    const element = fixture.nativeElement as HTMLElement;

    expect(submitted).not.toHaveBeenCalled();
    expect(element.querySelector<HTMLInputElement>('#event-id')?.matches(':disabled')).toBeTrue();
    expect(element.querySelector<HTMLButtonElement>('.submit-button')?.disabled).toBeTrue();
    expect(element.querySelector<HTMLButtonElement>('.secondary-button')?.disabled).toBeTrue();
  });

  it('clears the fields only when the user presses Limpiar', () => {
    component.form.setValue(request);
    submitForm();

    const element = fixture.nativeElement as HTMLElement;
    element.querySelector<HTMLButtonElement>('.secondary-button')?.click();
    fixture.detectChanges();

    expect(component.form.getRawValue()).toEqual({
      eventId: '',
      eventType: EventoTipo.PRODUCTION,
      timestamp: '',
      description: '',
    });
    expect(component.form.touched).toBeFalse();
    expect(element.querySelector('#event-id-error')).toBeNull();
  });

  it('builds the selector options from EventoTipo', () => {
    const options = (fixture.nativeElement as HTMLElement)
      .querySelectorAll<HTMLOptionElement>('#event-type option');

    expect(Array.from(options).map((option) => option.value)).toEqual(Object.values(EventoTipo));
  });
});
