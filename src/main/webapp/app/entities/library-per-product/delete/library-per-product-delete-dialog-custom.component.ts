import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { LibraryPerProductCustomService } from 'app/entities/library-per-product/service/library-per-product-custom.service';
import { LibraryPerProductDeleteDialogComponent } from 'app/entities/library-per-product/delete/library-per-product-delete-dialog.component';

@Component({
  templateUrl: './library-per-product-delete-dialog-custom.component.html',
})
export class LibraryPerProductDeleteDialogCustomComponent extends LibraryPerProductDeleteDialogComponent {
  constructor(protected libraryPerProductService: LibraryPerProductCustomService, public activeModal: NgbActiveModal) {
    super(libraryPerProductService, activeModal);
  }
}
