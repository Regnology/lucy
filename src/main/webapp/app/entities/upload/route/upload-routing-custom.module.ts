import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { UploadRoutingResolveCustomService } from 'app/entities/upload/route/upload-routing-resolve-custom.service';
import { UploadUpdateCustomComponent } from 'app/entities/upload/update/upload-update-custom.component';
import { UploadDetailCustomComponent } from 'app/entities/upload/detail/upload-detail-custom.component';
import { UploadCustomComponent } from 'app/entities/upload/list/upload-custom.component';

const uploadRoute: Routes = [
  {
    path: '',
    component: UploadCustomComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: UploadDetailCustomComponent,
    resolve: {
      upload: UploadRoutingResolveCustomService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UploadUpdateCustomComponent,
    resolve: {
      upload: UploadRoutingResolveCustomService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UploadUpdateCustomComponent,
    resolve: {
      upload: UploadRoutingResolveCustomService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(uploadRoute)],
  exports: [RouterModule],
})
export class UploadRoutingCustomModule {}
