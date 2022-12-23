import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { LibraryPerProductCustomService } from 'app/entities/library-per-product/service/library-per-product-custom.service';
import { LibraryPerProductComponent } from 'app/entities/library-per-product/list/library-per-product.component';
import { ILibraryPerProduct } from 'app/entities/library-per-product/library-per-product.model';
import { LibraryPerProductDeleteDialogCustomComponent } from 'app/entities/library-per-product/delete/library-per-product-delete-dialog-custom.component';

@Component({
  selector: 'jhi-library-per-product-custom',
  templateUrl: './library-per-product-custom.component.html',
})
export class LibraryPerProductCustomComponent extends LibraryPerProductComponent {
  constructor(
    protected libraryPerProductService: LibraryPerProductCustomService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal
  ) {
    super(libraryPerProductService, activatedRoute, router, modalService);
  }

  delete(libraryPerProduct: ILibraryPerProduct): void {
    const modalRef = this.modalService.open(LibraryPerProductDeleteDialogCustomComponent, {
      size: 'lg',
      backdrop: 'static',
    });
    modalRef.componentInstance.libraryPerProduct = libraryPerProduct;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadPage();
      }
    });
  }
}
