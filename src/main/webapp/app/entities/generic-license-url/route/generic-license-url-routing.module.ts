import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { GenericLicenseUrlComponent } from '../list/generic-license-url.component';
import { GenericLicenseUrlDetailComponent } from '../detail/generic-license-url-detail.component';
import { GenericLicenseUrlUpdateComponent } from '../update/generic-license-url-update.component';
import { GenericLicenseUrlRoutingResolveService } from './generic-license-url-routing-resolve.service';

const genericLicenseUrlRoute: Routes = [
  {
    path: '',
    component: GenericLicenseUrlComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: GenericLicenseUrlDetailComponent,
    resolve: {
      genericLicenseUrl: GenericLicenseUrlRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: GenericLicenseUrlUpdateComponent,
    resolve: {
      genericLicenseUrl: GenericLicenseUrlRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: GenericLicenseUrlUpdateComponent,
    resolve: {
      genericLicenseUrl: GenericLicenseUrlRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(genericLicenseUrlRoute)],
  exports: [RouterModule],
})
export class GenericLicenseUrlRoutingModule {}
