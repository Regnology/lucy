import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ILibraryPerProduct } from '../library-per-product.model';
import { LibraryPerProductService } from '../service/library-per-product.service';

@Component({
  templateUrl: './library-per-product-delete-dialog.component.html',
})
export class LibraryPerProductDeleteDialogComponent {
  libraryPerProduct?: ILibraryPerProduct;

  constructor(protected libraryPerProductService: LibraryPerProductService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.libraryPerProductService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
