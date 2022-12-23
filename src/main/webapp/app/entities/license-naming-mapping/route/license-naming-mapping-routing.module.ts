import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { LicenseNamingMappingComponent } from '../list/license-naming-mapping.component';
import { LicenseNamingMappingDetailComponent } from '../detail/license-naming-mapping-detail.component';
import { LicenseNamingMappingUpdateComponent } from '../update/license-naming-mapping-update.component';
import { LicenseNamingMappingRoutingResolveService } from './license-naming-mapping-routing-resolve.service';

const licenseNamingMappingRoute: Routes = [
  {
    path: '',
    component: LicenseNamingMappingComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LicenseNamingMappingDetailComponent,
    resolve: {
      licenseNamingMapping: LicenseNamingMappingRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LicenseNamingMappingUpdateComponent,
    resolve: {
      licenseNamingMapping: LicenseNamingMappingRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LicenseNamingMappingUpdateComponent,
    resolve: {
      licenseNamingMapping: LicenseNamingMappingRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(licenseNamingMappingRoute)],
  exports: [RouterModule],
})
export class LicenseNamingMappingRoutingModule {}
