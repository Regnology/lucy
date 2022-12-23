import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { LicenseRiskRoutingResolveCustomService } from 'app/entities/license-risk/route/license-risk-routing-resolve-custom.service';
import { LicenseRiskUpdateCustomComponent } from 'app/entities/license-risk/update/license-risk-update-custom.component';
import { LicenseRiskDetailCustomComponent } from 'app/entities/license-risk/detail/license-risk-detail-custom.component';
import { LicenseRiskCustomComponent } from 'app/entities/license-risk/list/license-risk-custom.component';

const licenseRiskRoute: Routes = [
  {
    path: '',
    component: LicenseRiskCustomComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LicenseRiskDetailCustomComponent,
    resolve: {
      licenseRisk: LicenseRiskRoutingResolveCustomService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LicenseRiskUpdateCustomComponent,
    resolve: {
      licenseRisk: LicenseRiskRoutingResolveCustomService,
    },
    data: {
      authorities: ['ROLE_USER', 'ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LicenseRiskUpdateCustomComponent,
    resolve: {
      licenseRisk: LicenseRiskRoutingResolveCustomService,
    },
    data: {
      authorities: ['ROLE_USER', 'ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(licenseRiskRoute)],
  exports: [RouterModule],
})
export class LicenseRiskRoutingCustomModule {}
