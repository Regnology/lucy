import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { ProductOverviewModule } from 'app/shared/statistics/product-overview/product-overview.module';
import { AbsoluteNumberPipe } from 'app/shared/number/absolute-number.pipe';
import { ProductDeleteDialogCustomComponent } from 'app/entities/product/delete/product-delete-dialog-custom.component';
import { ProductUpdateCustomComponent } from 'app/entities/product/update/product-update-custom.component';
import { ProductDetailCustomComponent } from 'app/entities/product/detail/product-detail-custom.component';
import { ProductCustomComponent } from 'app/entities/product/list/product-custom.component';
import { ProductRoutingCustomModule } from 'app/entities/product/route/product-routing-custom.module';
import { ProductComponent } from 'app/entities/product/list/product.component';
import { ProductDetailComponent } from 'app/entities/product/detail/product-detail.component';
import { ProductUpdateComponent } from 'app/entities/product/update/product-update.component';
import { ProductDeleteDialogComponent } from 'app/entities/product/delete/product-delete-dialog.component';
import { ProductModule } from './product.module';
import { ProductUpdateLibraryComponent } from './update-library/product-update-library.component';
import { DifferenceViewComponent } from '../../shared/modals/difference-view-modal/difference-view.component';

@NgModule({
  imports: [SharedModule, ProductRoutingCustomModule, NgxChartsModule, ProductOverviewModule, ProductModule],
  declarations: [
    ProductComponent,
    ProductDetailComponent,
    ProductUpdateComponent,
    ProductDeleteDialogComponent,
    ProductCustomComponent,
    ProductDetailCustomComponent,
    ProductUpdateCustomComponent,
    ProductDeleteDialogCustomComponent,
    AbsoluteNumberPipe,
    ProductUpdateLibraryComponent,
    DifferenceViewComponent,
  ],
  entryComponents: [ProductDeleteDialogCustomComponent],
})
export class ProductCustomModule {}
