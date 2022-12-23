import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { LicenseComponent } from '../list/license.component';
import { LicenseDetailComponent } from '../detail/license-detail.component';
import { LicenseUpdateComponent } from '../update/license-update.component';
import { LicenseRoutingResolveService } from './license-routing-resolve.service';

const licenseRoute: Routes = [
  {
    path: '',
    component: LicenseComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LicenseDetailComponent,
    resolve: {
      license: LicenseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LicenseUpdateComponent,
    resolve: {
      license: LicenseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LicenseUpdateComponent,
    resolve: {
      license: LicenseRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(licenseRoute)],
  exports: [RouterModule],
})
export class LicenseRoutingModule {}
