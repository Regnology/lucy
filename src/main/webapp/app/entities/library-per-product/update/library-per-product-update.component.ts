import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ILibraryPerProduct, LibraryPerProduct } from '../library-per-product.model';
import { LibraryPerProductService } from '../service/library-per-product.service';
import { ILibrary } from 'app/entities/library/library.model';
import { LibraryService } from 'app/entities/library/service/library.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';

@Component({
  selector: 'jhi-library-per-product-update',
  templateUrl: './library-per-product-update.component.html',
})
export class LibraryPerProductUpdateComponent implements OnInit {
  isSaving = false;

  librariesSharedCollection: ILibrary[] = [];
  productsSharedCollection: IProduct[] = [];

  editForm = this.fb.group({
    id: [],
    addedDate: [],
    addedManually: [],
    hideForPublishing: [],
    library: [],
    product: [],
  });

  constructor(
    protected libraryPerProductService: LibraryPerProductService,
    protected libraryService: LibraryService,
    protected productService: ProductService,
    protected activatedRoute: ActivatedRoute,
    protected fb: UntypedFormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ libraryPerProduct }) => {
      this.updateForm(libraryPerProduct);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const libraryPerProduct = this.createFromForm();
    if (libraryPerProduct.id !== undefined) {
      this.subscribeToSaveResponse(this.libraryPerProductService.update(libraryPerProduct));
    } else {
      this.subscribeToSaveResponse(this.libraryPerProductService.create(libraryPerProduct));
    }
  }

  trackLibraryById(index: number, item: ILibrary): number {
    return item.id!;
  }

  trackProductById(index: number, item: IProduct): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILibraryPerProduct>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(libraryPerProduct: ILibraryPerProduct): void {
    this.editForm.patchValue({
      id: libraryPerProduct.id,
      addedDate: libraryPerProduct.addedDate,
      addedManually: libraryPerProduct.addedManually,
      hideForPublishing: libraryPerProduct.hideForPublishing,
      library: libraryPerProduct.library,
      product: libraryPerProduct.product,
    });

    this.librariesSharedCollection = this.libraryService.addLibraryToCollectionIfMissing(
      this.librariesSharedCollection,
      libraryPerProduct.library
    );
    this.productsSharedCollection = this.productService.addProductToCollectionIfMissing(
      this.productsSharedCollection,
      libraryPerProduct.product
    );
  }

  protected loadRelationshipsOptions(): void {
    this.libraryService
      .query()
      .pipe(map((res: HttpResponse<ILibrary[]>) => res.body ?? []))
      .pipe(
        map((libraries: ILibrary[]) => this.libraryService.addLibraryToCollectionIfMissing(libraries, this.editForm.get('library')!.value))
      )
      .subscribe((libraries: ILibrary[]) => (this.librariesSharedCollection = libraries));

    this.productService
      .query()
      .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
      .pipe(
        map((products: IProduct[]) => this.productService.addProductToCollectionIfMissing(products, this.editForm.get('product')!.value))
      )
      .subscribe((products: IProduct[]) => (this.productsSharedCollection = products));
  }

  protected createFromForm(): ILibraryPerProduct {
    return {
      ...new LibraryPerProduct(),
      id: this.editForm.get(['id'])!.value,
      addedDate: this.editForm.get(['addedDate'])!.value,
      addedManually: this.editForm.get(['addedManually'])!.value,
      hideForPublishing: this.editForm.get(['hideForPublishing'])!.value,
      library: this.editForm.get(['library'])!.value,
      product: this.editForm.get(['product'])!.value,
    };
  }
}
