import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { LibraryErrorLogUpdateCustomComponent } from 'app/entities/library-error-log/update/library-error-log-update-custom.component';
import { LibraryErrorLogDetailCustomComponent } from 'app/entities/library-error-log/detail/library-error-log-detail-custom.component';
import { LibraryErrorLogCustomComponent } from 'app/entities/library-error-log/list/library-error-log-custom.component';
import { LibraryErrorLogRoutingResolveCustomService } from 'app/entities/library-error-log/route/library-error-log-routing-resolve-custom.service';

const libraryErrorLogRoute: Routes = [
  {
    path: '',
    component: LibraryErrorLogCustomComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LibraryErrorLogDetailCustomComponent,
    resolve: {
      libraryErrorLog: LibraryErrorLogRoutingResolveCustomService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LibraryErrorLogUpdateCustomComponent,
    resolve: {
      libraryErrorLog: LibraryErrorLogRoutingResolveCustomService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LibraryErrorLogUpdateCustomComponent,
    resolve: {
      libraryErrorLog: LibraryErrorLogRoutingResolveCustomService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(libraryErrorLogRoute)],
  exports: [RouterModule],
})
export class LibraryErrorLogRoutingCustomModule {}
