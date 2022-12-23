import { Route } from '@angular/router';
import { NavbarCustomComponent } from './navbar-custom.component';

export const navbarRoute: Route = {
  path: '',
  component: NavbarCustomComponent,
  outlet: 'navbar',
};
