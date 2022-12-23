import { Route } from '@angular/router';
import { LoginCustomComponent } from './login-custom.component';

export const LOGIN_CUSTOM_ROUTE: Route = {
  path: '',
  component: LoginCustomComponent,
  data: {
    pageTitle: 'Sign in',
  },
};
