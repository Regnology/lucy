import { Route } from '@angular/router';
import { HomeCustomComponent } from 'app/home/home-custom.component';
import { UserRouteAccessService } from '../core/auth/user-route-access.service';

export const HOME_CUSTOM_ROUTE: Route = {
  path: '',
  component: HomeCustomComponent,
  data: {
    pageTitle: 'Lucy',
  },
  canActivate: [UserRouteAccessService],
};
