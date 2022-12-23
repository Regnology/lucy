import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { LibraryCustomComponent } from 'app/entities/library/list/library-custom.component';
import { LibraryUpdateCustomComponent } from 'app/entities/library/update/library-update-custom.component';
import { LibraryDetailCustomComponent } from 'app/entities/library/detail/library-detail-custom.component';
import { LibraryRoutingResolveCustomService } from 'app/entities/library/route/library-routing-resolve-custom.service';

const libraryRoute: Routes = [
  {
    path: '',
    component: LibraryCustomComponent,
    data: {
      defaultSort: 'artifactId,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LibraryDetailCustomComponent,
    resolve: {
      library: LibraryRoutingResolveCustomService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LibraryUpdateCustomComponent,
    resolve: {
      library: LibraryRoutingResolveCustomService,
    },
    data: {
      authorities: ['ROLE_USER', 'ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LibraryUpdateCustomComponent,
    resolve: {
      library: LibraryRoutingResolveCustomService,
    },
    data: {
      authorities: ['ROLE_USER', 'ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(libraryRoute)],
  exports: [RouterModule],
})
export class LibraryRoutingCustomModule {}
