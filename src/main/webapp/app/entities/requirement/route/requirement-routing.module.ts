import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RequirementComponent } from '../list/requirement.component';
import { RequirementDetailComponent } from '../detail/requirement-detail.component';
import { RequirementUpdateComponent } from '../update/requirement-update.component';
import { RequirementRoutingResolveService } from './requirement-routing-resolve.service';

const requirementRoute: Routes = [
  {
    path: '',
    component: RequirementComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RequirementDetailComponent,
    resolve: {
      requirement: RequirementRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RequirementUpdateComponent,
    resolve: {
      requirement: RequirementRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RequirementUpdateComponent,
    resolve: {
      requirement: RequirementRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(requirementRoute)],
  exports: [RouterModule],
})
export class RequirementRoutingModule {}
