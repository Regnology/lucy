import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RequirementRoutingResolveCustomService } from 'app/entities/requirement/route/requirement-routing-resolve-custom.service';
import { RequirementUpdateCustomComponent } from 'app/entities/requirement/update/requirement-update-custom.component';
import { RequirementDetailCustomComponent } from 'app/entities/requirement/detail/requirement-detail-custom.component';
import { RequirementCustomComponent } from 'app/entities/requirement/list/requirement-custom.component';

const requirementRoute: Routes = [
  {
    path: '',
    component: RequirementCustomComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RequirementDetailCustomComponent,
    resolve: {
      requirement: RequirementRoutingResolveCustomService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RequirementUpdateCustomComponent,
    resolve: {
      requirement: RequirementRoutingResolveCustomService,
    },
    data: {
      authorities: ['ROLE_USER', 'ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RequirementUpdateCustomComponent,
    resolve: {
      requirement: RequirementRoutingResolveCustomService,
    },
    data: {
      authorities: ['ROLE_USER', 'ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(requirementRoute)],
  exports: [RouterModule],
})
export class RequirementRoutingCustomModule {}
