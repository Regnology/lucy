import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { LicensePerLibraryRoutingResolveCustomService } from 'app/entities/license-per-library/route/license-per-library-routing-resolve-custom.service';
import { LicensePerLibraryUpdateCustomComponent } from 'app/entities/license-per-library/update/license-per-library-update-custom.component';
import { LicensePerLibraryDetailCustomComponent } from 'app/entities/license-per-library/detail/license-per-library-detail-custom.component';
import { LicensePerLibraryCustomComponent } from 'app/entities/license-per-library/list/license-per-library-custom.component';

const licensePerLibraryRoute: Routes = [
  {
    path: '',
    component: LicensePerLibraryCustomComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LicensePerLibraryDetailCustomComponent,
    resolve: {
      licensePerLibrary: LicensePerLibraryRoutingResolveCustomService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LicensePerLibraryUpdateCustomComponent,
    resolve: {
      licensePerLibrary: LicensePerLibraryRoutingResolveCustomService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LicensePerLibraryUpdateCustomComponent,
    resolve: {
      licensePerLibrary: LicensePerLibraryRoutingResolveCustomService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(licensePerLibraryRoute)],
  exports: [RouterModule],
})
export class LicensePerLibraryRoutingCustomModule {}
