import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductOverviewComponent } from './product-overview.component';
import { NgxChartsModule } from '@swimlane/ngx-charts';

@NgModule({
  declarations: [ProductOverviewComponent],
  imports: [CommonModule, NgxChartsModule],
  exports: [ProductOverviewComponent],
})
export class ProductOverviewModule {}
