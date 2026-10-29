import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventoResponseDto, EventoTipo } from '../event.model';
import { EventList } from './event-list';

describe('EventList', () => {
  let fixture: ComponentFixture<EventList>;

  const events: EventoResponseDto[] = [
    {
      id: 1,
      eventId: 'EVT-1001',
      timestamp: '2026-05-20T10:00:00',
      eventType: EventoTipo.PRODUCTION,
      description: 'Inicio de producción',
    },
    {
      id: 2,
      eventId: 'EVT-1002',
      timestamp: '2026-05-20T11:30:00',
      eventType: EventoTipo.QUALITY,
      description: 'Control de calidad',
    },
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventList],
    }).compileComponents();

    fixture = TestBed.createComponent(EventList);
    fixture.componentRef.setInput('events', events);
    fixture.detectChanges();
  });

  it('shows every event field and preserves the local date and time', () => {
    const element = fixture.nativeElement as HTMLElement;
    const rows = element.querySelectorAll('tbody tr');

    expect(rows.length).toBe(2);

    const cells = Array.from(rows[0].querySelectorAll('td'))
      .map((cell) => cell.textContent?.trim());

    expect(cells).toEqual([
      '1',
      'EVT-1001',
      'PRODUCTION',
      'Inicio de producción',
      '20/05/2026 10:00:00',
      '',
    ]);
    expect(rows[1].textContent).toContain('QUALITY');
    expect(rows[1].textContent).toContain('Control de calidad');
    expect(rows[1].querySelector('time')?.getAttribute('datetime'))
      .toBe('2026-05-20T11:30:00');
  });

  it('updates the table when a new list is received', () => {
    fixture.componentRef.setInput('events', [events[1]]);
    fixture.detectChanges();

    const rows = (fixture.nativeElement as HTMLElement).querySelectorAll('tbody tr');

    expect(rows.length).toBe(1);
    expect(rows[0].textContent).toContain('EVT-1002');
    expect(rows[0].textContent).not.toContain('EVT-1001');
  });

  it('shows Ver only for QUALITY events and emits the selected event', () => {
    const element = fixture.nativeElement as HTMLElement;
    const rows = element.querySelectorAll('tbody tr');
    const selected = jasmine.createSpy('auditRequested');
    fixture.componentInstance.auditRequested.subscribe(selected);

    expect(rows[0].querySelector('button')).toBeNull();
    expect(element.querySelectorAll('.view-button').length).toBe(1);

    const button = rows[1].querySelector<HTMLButtonElement>('.view-button')!;
    expect(button.textContent?.trim()).toBe('Ver');
    button.click();

    expect(selected).toHaveBeenCalledOnceWith(events[1]);
  });
});
