import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { UploadComponent } from '../list/upload.component';
import { UploadDetailComponent } from '../detail/upload-detail.component';
import { UploadUpdateComponent } from '../update/upload-update.component';
import { UploadRoutingResolveService } from './upload-routing-resolve.service';

const uploadRoute: Routes = [
  {
    path: '',
    component: UploadComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: UploadDetailComponent,
    resolve: {
      upload: UploadRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UploadUpdateComponent,
    resolve: {
      upload: UploadRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UploadUpdateComponent,
    resolve: {
      upload: UploadRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(uploadRoute)],
  exports: [RouterModule],
})
export class UploadRoutingModule {}
