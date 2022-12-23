import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { GenericLicenseUrlUpdateCustomComponent } from '../update/generic-license-url-update-custom.component';
import { GenericLicenseUrlRoutingResolveCustomService } from 'app/entities/generic-license-url/route/generic-license-url-routing-resolve-custom.service';
import { GenericLicenseUrlCustomComponent } from 'app/entities/generic-license-url/list/generic-license-url-custom.component';
import { GenericLicenseUrlDetailCustomComponent } from 'app/entities/generic-license-url/detail/generic-license-url-detail-custom.component';

const genericLicenseUrlRoute: Routes = [
  {
    path: '',
    component: GenericLicenseUrlCustomComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: GenericLicenseUrlDetailCustomComponent,
    resolve: {
      genericLicenseUrl: GenericLicenseUrlRoutingResolveCustomService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: GenericLicenseUrlUpdateCustomComponent,
    resolve: {
      genericLicenseUrl: GenericLicenseUrlRoutingResolveCustomService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: GenericLicenseUrlUpdateCustomComponent,
    resolve: {
      genericLicenseUrl: GenericLicenseUrlRoutingResolveCustomService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(genericLicenseUrlRoute)],
  exports: [RouterModule],
})
export class GenericLicenseUrlRoutingCustomModule {}
