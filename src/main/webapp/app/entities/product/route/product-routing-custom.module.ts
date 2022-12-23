import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProductCustomComponent } from 'app/entities/product/list/product-custom.component';
import { ProductDetailCustomComponent } from 'app/entities/product/detail/product-detail-custom.component';
import { ProductRoutingResolveCustomService } from 'app/entities/product/route/product-routing-resolve-custom.service';
import { ProductUpdateCustomComponent } from 'app/entities/product/update/product-update-custom.component';

const productRoute: Routes = [
  {
    path: '',
    component: ProductCustomComponent,
    data: {
      defaultSort: 'lastUpdatedDate,desc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProductDetailCustomComponent,
    data: {
      defaultSort: 'library.artifactId,asc',
    },
    resolve: {
      product: ProductRoutingResolveCustomService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProductUpdateCustomComponent,
    resolve: {
      product: ProductRoutingResolveCustomService,
    },
    data: {
      authorities: ['ROLE_USER', 'ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProductUpdateCustomComponent,
    resolve: {
      product: ProductRoutingResolveCustomService,
    },
    data: {
      authorities: ['ROLE_USER', 'ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(productRoute)],
  exports: [RouterModule],
})
export class ProductRoutingCustomModule {}
