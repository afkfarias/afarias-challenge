import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventoResponseDto } from '../event.model';
import { EventsPage } from './events-page';

describe('EventsPage', () => {
  let fixture: ComponentFixture<EventsPage>;
  let httpTesting: HttpTestingController;

  const event: EventoResponseDto = {
    id: 1,
    eventId: 'EVT-1001',
    timestamp: '2026-05-20T10:00:00',
    eventType: 'PRODUCTION',
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
        eventType: 'QUALITY',
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
});
