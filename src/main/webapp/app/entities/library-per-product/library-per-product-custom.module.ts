import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { LibraryPerProductUpdateCustomComponent } from 'app/entities/library-per-product/update/library-per-product-update-custom.component';
import { LibraryPerProductCustomComponent } from 'app/entities/library-per-product/list/library-per-product-custom.component';
import { LibraryPerProductDeleteDialogCustomComponent } from 'app/entities/library-per-product/delete/library-per-product-delete-dialog-custom.component';
import { LibraryPerProductRoutingCustomModule } from 'app/entities/library-per-product/route/library-per-product-routing-custom.module';
import { LibraryPerProductDetailCustomComponent } from 'app/entities/library-per-product/detail/library-per-product-detail-custom.component';
import { LibraryPerProductComponent } from 'app/entities/library-per-product/list/library-per-product.component';
import { LibraryPerProductDetailComponent } from 'app/entities/library-per-product/detail/library-per-product-detail.component';
import { LibraryPerProductUpdateComponent } from 'app/entities/library-per-product/update/library-per-product-update.component';
import { LibraryPerProductDeleteDialogComponent } from 'app/entities/library-per-product/delete/library-per-product-delete-dialog.component';

@NgModule({
  imports: [SharedModule, LibraryPerProductRoutingCustomModule],
  declarations: [
    LibraryPerProductComponent,
    LibraryPerProductDetailComponent,
    LibraryPerProductUpdateComponent,
    LibraryPerProductDeleteDialogComponent,
    LibraryPerProductCustomComponent,
    LibraryPerProductDetailCustomComponent,
    LibraryPerProductUpdateCustomComponent,
    LibraryPerProductDeleteDialogCustomComponent,
  ],
  entryComponents: [LibraryPerProductDeleteDialogCustomComponent],
})
export class LibraryPerProductCustomModule {}
