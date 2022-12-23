import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { LicenseRiskComponent } from '../list/license-risk.component';
import { LicenseRiskDetailComponent } from '../detail/license-risk-detail.component';
import { LicenseRiskUpdateComponent } from '../update/license-risk-update.component';
import { LicenseRiskRoutingResolveService } from './license-risk-routing-resolve.service';

const licenseRiskRoute: Routes = [
  {
    path: '',
    component: LicenseRiskComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LicenseRiskDetailComponent,
    resolve: {
      licenseRisk: LicenseRiskRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LicenseRiskUpdateComponent,
    resolve: {
      licenseRisk: LicenseRiskRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LicenseRiskUpdateComponent,
    resolve: {
      licenseRisk: LicenseRiskRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(licenseRiskRoute)],
  exports: [RouterModule],
})
export class LicenseRiskRoutingModule {}
