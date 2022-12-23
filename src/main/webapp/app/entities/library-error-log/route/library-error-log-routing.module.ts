import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { LibraryErrorLogComponent } from '../list/library-error-log.component';
import { LibraryErrorLogDetailComponent } from '../detail/library-error-log-detail.component';
import { LibraryErrorLogUpdateComponent } from '../update/library-error-log-update.component';
import { LibraryErrorLogRoutingResolveService } from './library-error-log-routing-resolve.service';

const libraryErrorLogRoute: Routes = [
  {
    path: '',
    component: LibraryErrorLogComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LibraryErrorLogDetailComponent,
    resolve: {
      libraryErrorLog: LibraryErrorLogRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LibraryErrorLogUpdateComponent,
    resolve: {
      libraryErrorLog: LibraryErrorLogRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LibraryErrorLogUpdateComponent,
    resolve: {
      libraryErrorLog: LibraryErrorLogRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(libraryErrorLogRoute)],
  exports: [RouterModule],
})
export class LibraryErrorLogRoutingModule {}
