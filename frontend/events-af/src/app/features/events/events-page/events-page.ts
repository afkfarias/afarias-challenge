import { Component } from '@angular/core';

import { EventList } from '../event-list/event-list';
import { EventForm } from '../event-form/event-form';

@Component({
  selector: 'app-events-page',
  imports: [EventForm, EventList],
  templateUrl: './events-page.html',
  styleUrl: './events-page.css',
})
export class EventsPage {

}
