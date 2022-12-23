import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { LibraryPerProductComponent } from '../list/library-per-product.component';
import { LibraryPerProductDetailComponent } from '../detail/library-per-product-detail.component';
import { LibraryPerProductUpdateComponent } from '../update/library-per-product-update.component';
import { LibraryPerProductRoutingResolveService } from './library-per-product-routing-resolve.service';

const libraryPerProductRoute: Routes = [
  {
    path: '',
    component: LibraryPerProductComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LibraryPerProductDetailComponent,
    resolve: {
      libraryPerProduct: LibraryPerProductRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LibraryPerProductUpdateComponent,
    resolve: {
      libraryPerProduct: LibraryPerProductRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LibraryPerProductUpdateComponent,
    resolve: {
      libraryPerProduct: LibraryPerProductRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(libraryPerProductRoute)],
  exports: [RouterModule],
})
export class LibraryPerProductRoutingModule {}
