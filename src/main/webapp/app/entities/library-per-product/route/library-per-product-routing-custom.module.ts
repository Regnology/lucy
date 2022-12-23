import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { LibraryPerProductDetailCustomComponent } from 'app/entities/library-per-product/detail/library-per-product-detail-custom.component';
import { LibraryPerProductCustomComponent } from 'app/entities/library-per-product/list/library-per-product-custom.component';
import { LibraryPerProductRoutingResolveCustomService } from 'app/entities/library-per-product/route/library-per-product-routing-resolve-custom.service';
import { LibraryPerProductUpdateCustomComponent } from 'app/entities/library-per-product/update/library-per-product-update-custom.component';

const libraryPerProductRoute: Routes = [
  {
    path: '',
    component: LibraryPerProductCustomComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LibraryPerProductDetailCustomComponent,
    resolve: {
      libraryPerProduct: LibraryPerProductRoutingResolveCustomService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LibraryPerProductUpdateCustomComponent,
    resolve: {
      libraryPerProduct: LibraryPerProductRoutingResolveCustomService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LibraryPerProductUpdateCustomComponent,
    resolve: {
      libraryPerProduct: LibraryPerProductRoutingResolveCustomService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(libraryPerProductRoute)],
  exports: [RouterModule],
})
export class LibraryPerProductRoutingCustomModule {}
