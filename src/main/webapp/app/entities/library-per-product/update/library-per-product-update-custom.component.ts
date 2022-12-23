import { Component } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { LibraryPerProductCustomService } from 'app/entities/library-per-product/service/library-per-product-custom.service';
import { LibraryPerProductUpdateComponent } from 'app/entities/library-per-product/update/library-per-product-update.component';
import { LibraryCustomService } from 'app/entities/library/service/library-custom.service';
import { ProductCustomService } from 'app/entities/product/service/product-custom.service';

@Component({
  selector: 'jhi-library-per-product-update-custom',
  templateUrl: './library-per-product-update-custom.component.html',
})
export class LibraryPerProductUpdateCustomComponent extends LibraryPerProductUpdateComponent {
  constructor(
    protected libraryPerProductService: LibraryPerProductCustomService,
    protected libraryService: LibraryCustomService,
    protected productService: ProductCustomService,
    protected activatedRoute: ActivatedRoute,
    protected fb: UntypedFormBuilder
  ) {
    super(libraryPerProductService, libraryService, productService, activatedRoute, fb);
  }
}
