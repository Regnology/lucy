import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { LicenseCustomComponent } from 'app/entities/license/list/license-custom.component';
import { LicenseDetailCustomComponent } from 'app/entities/license/detail/license-detail-custom.component';
import { LicenseRoutingResolveCustomService } from 'app/entities/license/route/license-routing-resolve-custom.service';
import { LicenseUpdateCustomComponent } from 'app/entities/license/update/license-update-custom.component';

const licenseRoute: Routes = [
  {
    path: '',
    component: LicenseCustomComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LicenseDetailCustomComponent,
    resolve: {
      license: LicenseRoutingResolveCustomService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LicenseUpdateCustomComponent,
    resolve: {
      license: LicenseRoutingResolveCustomService,
    },
    data: {
      authorities: ['ROLE_USER', 'ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LicenseUpdateCustomComponent,
    resolve: {
      license: LicenseRoutingResolveCustomService,
    },
    data: {
      authorities: ['ROLE_USER', 'ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(licenseRoute)],
  exports: [RouterModule],
})
export class LicenseRoutingCustomModule {}
