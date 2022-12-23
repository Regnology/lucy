import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { LicensePerLibraryComponent } from '../list/license-per-library.component';
import { LicensePerLibraryDetailComponent } from '../detail/license-per-library-detail.component';
import { LicensePerLibraryUpdateComponent } from '../update/license-per-library-update.component';
import { LicensePerLibraryRoutingResolveService } from './license-per-library-routing-resolve.service';

const licensePerLibraryRoute: Routes = [
  {
    path: '',
    component: LicensePerLibraryComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LicensePerLibraryDetailComponent,
    resolve: {
      licensePerLibrary: LicensePerLibraryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LicensePerLibraryUpdateComponent,
    resolve: {
      licensePerLibrary: LicensePerLibraryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LicensePerLibraryUpdateComponent,
    resolve: {
      licensePerLibrary: LicensePerLibraryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(licensePerLibraryRoute)],
  exports: [RouterModule],
})
export class LicensePerLibraryRoutingModule {}
