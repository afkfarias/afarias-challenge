import { Routes } from '@angular/router';

export const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        redirectTo: 'events',
    }, 
    {
        path: 'events',
        loadComponent: () => import('./features/events/events-page/events-page')
            .then((m) => m.EventsPage),
    },
    {
        path: '**',
        redirectTo: 'events',
    }
];
