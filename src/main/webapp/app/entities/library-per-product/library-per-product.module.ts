import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { LibraryPerProductComponent } from './list/library-per-product.component';
import { LibraryPerProductDetailComponent } from './detail/library-per-product-detail.component';
import { LibraryPerProductUpdateComponent } from './update/library-per-product-update.component';
import { LibraryPerProductDeleteDialogComponent } from './delete/library-per-product-delete-dialog.component';
import { LibraryPerProductRoutingModule } from './route/library-per-product-routing.module';

@NgModule({
  imports: [SharedModule, LibraryPerProductRoutingModule],
  declarations: [
    LibraryPerProductComponent,
    LibraryPerProductDetailComponent,
    LibraryPerProductUpdateComponent,
    LibraryPerProductDeleteDialogComponent,
  ],
  entryComponents: [LibraryPerProductDeleteDialogComponent],
})
export class LibraryPerProductModule {}
