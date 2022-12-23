import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProductDeleteDialogComponent } from './delete/product-delete-dialog.component';
import { ProductRoutingModule } from './route/product-routing.module';

@NgModule({
  imports: [SharedModule, ProductRoutingModule],
  declarations: [],
  entryComponents: [ProductDeleteDialogComponent],
})
export class ProductModule {}
