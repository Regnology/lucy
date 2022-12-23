import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { LicenseNamingMappingUpdateCustomComponent } from 'app/entities/license-naming-mapping/update/license-naming-mapping-update-custom.component';
import { LicenseNamingMappingDetailCustomComponent } from 'app/entities/license-naming-mapping/detail/license-naming-mapping-detail-custom.component';
import { LicenseNamingMappingCustomComponent } from 'app/entities/license-naming-mapping/list/license-naming-mapping-custom.component';
import { LicenseNamingMappingRoutingResolveCustomService } from 'app/entities/license-naming-mapping/route/license-naming-mapping-routing-resolve-custom.service';

const licenseNamingMappingRoute: Routes = [
  {
    path: '',
    component: LicenseNamingMappingCustomComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LicenseNamingMappingDetailCustomComponent,
    resolve: {
      licenseNamingMapping: LicenseNamingMappingRoutingResolveCustomService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LicenseNamingMappingUpdateCustomComponent,
    resolve: {
      licenseNamingMapping: LicenseNamingMappingRoutingResolveCustomService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LicenseNamingMappingUpdateCustomComponent,
    resolve: {
      licenseNamingMapping: LicenseNamingMappingRoutingResolveCustomService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(licenseNamingMappingRoute)],
  exports: [RouterModule],
})
export class LicenseNamingMappingRoutingCustomModule {}
