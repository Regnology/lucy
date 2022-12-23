import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { LibraryPerProductDetailComponent } from 'app/entities/library-per-product/detail/library-per-product-detail.component';

@Component({
  selector: 'jhi-library-per-product-detail-custom',
  templateUrl: './library-per-product-detail-custom.component.html',
})
export class LibraryPerProductDetailCustomComponent extends LibraryPerProductDetailComponent {
  constructor(protected activatedRoute: ActivatedRoute) {
    super(activatedRoute);
  }
}
