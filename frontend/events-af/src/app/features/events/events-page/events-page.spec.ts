import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { EventForm } from '../event-form/event-form';
import { EventoRequestDto, EventoResponseDto, EventoTipo } from '../event.model';
import { EventsPage } from './events-page';

describe('EventsPage', () => {
  let fixture: ComponentFixture<EventsPage>;
  let httpTesting: HttpTestingController;

  const event: EventoResponseDto = {
    id: 1,
    eventId: 'EVT-1001',
    timestamp: '2026-05-20T10:00:00',
    eventType: EventoTipo.PRODUCTION,
    description: 'Inicio de producción',
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventsPage],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();

    httpTesting = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(EventsPage);
    fixture.detectChanges();
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('requests events automatically and shows loading until the response arrives', () => {
    const request = httpTesting.expectOne({ method: 'GET', url: '/eventos' });
    const element = fixture.nativeElement as HTMLElement;

    expect(element.querySelector('[role="status"]')?.textContent)
      .toContain('Cargando eventos');
    expect(element.querySelector<HTMLButtonElement>('.refresh-button')?.disabled)
      .toBeTrue();

    request.flush([]);
    fixture.detectChanges();

    expect(element.textContent).not.toContain('Cargando eventos');
  });

  it('renders the events returned by the service', () => {
    httpTesting.expectOne('/eventos').flush([event]);
    fixture.detectChanges();

    const rows = (fixture.nativeElement as HTMLElement).querySelectorAll('tbody tr');

    expect(rows.length).toBe(1);
    expect(rows[0].textContent).toContain('EVT-1001');
    expect(rows[0].textContent).toContain('Inicio de producción');
  });

  it('shows an empty state when no events have been registered', () => {
    httpTesting.expectOne('/eventos').flush([]);
    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;

    expect(element.textContent).toContain('Todavía no hay eventos registrados.');
    expect(element.querySelector('table')).toBeNull();
    expect(element.querySelector('[role="alert"]')).toBeNull();
  });

  it('shows a request error and allows retrying successfully', () => {
    httpTesting.expectOne('/eventos').flush(
      { mensaje: 'Error interno del servidor', codigo: 500 },
      { status: 500, statusText: 'Internal Server Error' },
    );
    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;
    const alert = element.querySelector('[role="alert"]');

    expect(alert?.textContent).toContain('No se pudieron cargar los eventos.');
    expect(element.textContent).not.toContain('Todavía no hay eventos registrados.');

    const retryButton = alert?.querySelector<HTMLButtonElement>('button');
    expect(retryButton).not.toBeNull();
    retryButton?.click();
    fixture.detectChanges();

    httpTesting.expectOne({ method: 'GET', url: '/eventos' }).flush([event]);
    fixture.detectChanges();

    expect(element.querySelector('[role="alert"]')).toBeNull();
    expect(element.querySelector('tbody')?.textContent).toContain('EVT-1001');
  });

  it('shows a connection error when the server cannot be reached', () => {
    httpTesting.expectOne('/eventos').error(new ProgressEvent('error'));
    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;

    expect(element.querySelector('[role="alert"]')?.textContent)
      .toContain('No se pudo conectar con el servidor.');
    expect(element.querySelector('table')).toBeNull();
  });

  it('refreshes the list when Actualizar is pressed', () => {
    httpTesting.expectOne('/eventos').flush([event]);
    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;
    const button = element.querySelector<HTMLButtonElement>('.refresh-button');

    button?.click();
    fixture.detectChanges();

    expect(button?.disabled).toBeTrue();

    httpTesting.expectOne({ method: 'GET', url: '/eventos' }).flush([
      event,
      {
        ...event,
        id: 2,
        eventId: 'EVT-1002',
        eventType: EventoTipo.QUALITY,
      },
    ]);
    fixture.detectChanges();

    expect(element.querySelectorAll('tbody tr').length).toBe(2);
    expect(button?.disabled).toBeFalse();
  });

  it('cancels a pending request when the page is destroyed', () => {
    const request = httpTesting.expectOne('/eventos');

    fixture.destroy();

    expect(request.cancelled).toBeTrue();
  });

  function submitEvent(values: EventoRequestDto): void {
    const form = fixture.debugElement.query(By.directive(EventForm)).componentInstance as EventForm;
    form.form.setValue(values);
    const formElement = (fixture.nativeElement as HTMLElement).querySelector('form');
    formElement?.dispatchEvent(new Event('submit', { bubbles: true, cancelable: true }));
    fixture.detectChanges();
  }

  const request: EventoRequestDto = {
    eventId: 'EVT-1001',
    eventType: EventoTipo.PRODUCTION,
    timestamp: '2026-05-20T10:00:00',
    description: 'Inicio de producción',
  };

  it('registers the entered event, retains the form and refreshes the table', () => {
    httpTesting.expectOne('/eventos').flush([]);
    fixture.detectChanges();

    submitEvent(request);

    const post = httpTesting.expectOne({ method: 'POST', url: '/eventos' });
    const element = fixture.nativeElement as HTMLElement;

    expect(post.request.body).toEqual(request);
    expect(element.querySelector<HTMLButtonElement>('.submit-button')?.disabled).toBeTrue();

    post.flush(event, { status: 201, statusText: 'Created' });
    fixture.detectChanges();

    expect(element.querySelector('.save-feedback')?.textContent)
      .toContain('Evento EVT-1001 registrado correctamente.');

    httpTesting.expectOne({ method: 'GET', url: '/eventos' }).flush([event]);
    fixture.detectChanges();

    expect(element.querySelector('tbody')?.textContent).toContain('EVT-1001');
    expect(element.querySelector<HTMLInputElement>('#event-id')?.value).toBe('EVT-1001');
    expect(element.querySelector<HTMLButtonElement>('.submit-button')?.disabled).toBeFalse();
  });

  it('reports duplicates and allows resending the same identifier', () => {
    httpTesting.expectOne('/eventos').flush([event]);
    fixture.detectChanges();
    submitEvent(request);

    httpTesting.expectOne({ method: 'POST', url: '/eventos' }).flush(
      { mensaje: 'Ya existe un registro para el eventId', codigo: 409 },
      { status: 409, statusText: 'Conflict' },
    );
    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;

    expect(element.querySelector('.save-feedback[role="alert"]')?.textContent)
      .toContain('Ya existe un evento con el identificador EVT-1001.');
    expect(element.querySelectorAll('tbody tr').length).toBe(1);
    expect(element.querySelector<HTMLInputElement>('#event-id')?.value).toBe('EVT-1001');
    httpTesting.expectNone({ method: 'GET', url: '/eventos' });

    submitEvent(request);

    const repeatedPost = httpTesting.expectOne({ method: 'POST', url: '/eventos' });
    expect(repeatedPost.request.body).toEqual(request);
    repeatedPost.flush(
      { mensaje: 'Ya existe un registro para el eventId', codigo: 409 },
      { status: 409, statusText: 'Conflict' },
    );
    fixture.detectChanges();
  });

  for (const failure of [
    { status: 400, message: 'Los datos del evento no son válidos.' },
    { status: 500, message: 'No se pudo registrar el evento.' },
  ]) {
    it('reports a registration error with HTTP status ' + failure.status, () => {
      httpTesting.expectOne('/eventos').flush([]);
      fixture.detectChanges();
      submitEvent(request);

      httpTesting.expectOne({ method: 'POST', url: '/eventos' }).flush(
        { mensaje: 'Solicitud rechazada', codigo: failure.status },
        { status: failure.status, statusText: 'Error' },
      );
      fixture.detectChanges();

      const element = fixture.nativeElement as HTMLElement;

      expect(element.querySelector('.save-feedback[role="alert"]')?.textContent)
        .toContain(failure.message);
      expect(element.querySelector<HTMLButtonElement>('.submit-button')?.disabled).toBeFalse();
      expect(element.querySelector<HTMLInputElement>('#event-id')?.value).toBe('EVT-1001');
      httpTesting.expectNone({ method: 'GET', url: '/eventos' });
    });
  }

  it('reports a connection error when registering an event', () => {
    httpTesting.expectOne('/eventos').flush([]);
    fixture.detectChanges();
    submitEvent(request);

    httpTesting.expectOne({ method: 'POST', url: '/eventos' }).error(new ProgressEvent('error'));
    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;

    expect(element.querySelector('.save-feedback[role="alert"]')?.textContent)
      .toContain('No se pudo conectar con el servidor.');
    expect(element.querySelector<HTMLButtonElement>('.submit-button')?.disabled).toBeFalse();
  });

  it('does not send simultaneous registration requests', () => {
    httpTesting.expectOne('/eventos').flush([]);
    fixture.detectChanges();
    submitEvent(request);
    submitEvent(request);

    const posts = httpTesting.match({ method: 'POST', url: '/eventos' });
    expect(posts.length).toBe(1);
    const post = posts[0];
    post.flush(
      { mensaje: 'Ya existe un registro para el eventId', codigo: 409 },
      { status: 409, statusText: 'Conflict' },
    );
    fixture.detectChanges();
  });

  it('preserves registration success if refreshing the list fails', () => {
    httpTesting.expectOne('/eventos').flush([]);
    fixture.detectChanges();
    submitEvent(request);

    httpTesting.expectOne({ method: 'POST', url: '/eventos' })
      .flush(event, { status: 201, statusText: 'Created' });
    httpTesting.expectOne({ method: 'GET', url: '/eventos' }).flush(
      { mensaje: 'Error interno del servidor', codigo: 500 },
      { status: 500, statusText: 'Internal Server Error' },
    );
    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;

    expect(element.querySelector('.save-feedback[role="status"]')?.textContent)
      .toContain('registrado correctamente');
    expect(element.querySelector('.save-feedback[role="alert"]')).toBeNull();
    expect(element.querySelector('[aria-labelledby="events-title"] [role="alert"]')?.textContent)
      .toContain('No se pudieron cargar los eventos.');
  });

  it('replaces a pending list request after registering an event', () => {
    const initialGet = httpTesting.expectOne({ method: 'GET', url: '/eventos' });
    submitEvent(request);

    httpTesting.expectOne({ method: 'POST', url: '/eventos' })
      .flush(event, { status: 201, statusText: 'Created' });

    expect(initialGet.cancelled).toBeTrue();

    httpTesting.expectOne({ method: 'GET', url: '/eventos' }).flush([event]);
    fixture.detectChanges();

    expect((fixture.nativeElement as HTMLElement).querySelector('tbody')?.textContent)
      .toContain('EVT-1001');
  });

  it('cancels a pending registration request when the page is destroyed', () => {
    httpTesting.expectOne('/eventos').flush([]);
    fixture.detectChanges();
    submitEvent(request);

    const post = httpTesting.expectOne({ method: 'POST', url: '/eventos' });

    fixture.destroy();

    expect(post.cancelled).toBeTrue();
  });
});
