import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProduct } from '../product.model';
import { ProductDeleteDialogComponent } from 'app/entities/product/delete/product-delete-dialog.component';
import { ProductCustomService } from 'app/entities/product/service/product-custom.service';

@Component({
  templateUrl: './product-delete-dialog-custom.component.html',
})
export class ProductDeleteDialogCustomComponent extends ProductDeleteDialogComponent {
  product?: IProduct;

  constructor(protected productService: ProductCustomService, public activeModal: NgbActiveModal) {
    super(productService, activeModal);
  }
}
