import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILibraryPerProduct } from '../library-per-product.model';

@Component({
  selector: 'jhi-library-per-product-detail',
  templateUrl: './library-per-product-detail.component.html',
})
export class LibraryPerProductDetailComponent implements OnInit {
  libraryPerProduct: ILibraryPerProduct | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ libraryPerProduct }) => {
      this.libraryPerProduct = libraryPerProduct;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
