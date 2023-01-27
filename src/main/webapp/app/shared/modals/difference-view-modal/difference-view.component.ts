import { Component, Input, OnInit } from '@angular/core';

import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';
import { HttpResponse } from '@angular/common/http';
import { ProductCustomService } from '../../../entities/product/service/product-custom.service';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { IProduct } from '../../../entities/product/product.model';
import { ActivatedRoute } from '@angular/router';
import { IDifferenceView } from './difference-view.model';
import { ASC } from '../../../config/pagination.constants';
import { ILibrary } from '../../../entities/library/library.model';
import { LibraryCustomService } from '../../../entities/library/service/library-custom.service';

@Component({
  selector: 'jhi-difference-view',
  templateUrl: './difference-view.component.html',
})
export class DifferenceViewComponent implements OnInit {
  @Input()
  showProductSelection = true;

  differenceView?: IDifferenceView | null;
  noDifferenceViewPossible = false;

  isLoading = false;
  isCollapsed = true;
  productsSharedCollection: IProduct[] = [];

  productComparisonForm = this.fb.group({
    firstProduct: [null, [Validators.required]],
    secondProduct: [null, [Validators.required]],
  });

  optionsForm = this.fb.group({
    showLeft: true,
    showCenter: true,
    showRight: true,
    onlyNewLibraries: false,
    showLicense: true,
    showRisk: false,
  });

  constructor(
    protected productService: ProductCustomService,
    protected fb: UntypedFormBuilder,
    protected activatedRoute: ActivatedRoute,
    protected libraryService: LibraryCustomService
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(() => {
      if (this.showProductSelection) {
        this.loadRelationshipsOptions();
      }
    });
  }

  compare(): void {
    const firstProduct = this.productComparisonForm.get('firstProduct')!.value as unknown as IProduct;
    const secondProduct = this.productComparisonForm.get('secondProduct')!.value as unknown as IProduct;

    this.isLoading = true;

    this.productService
      .compareProducts({
        firstProductId: firstProduct.id,
        secondProductId: secondProduct.id,
      })
      .subscribe({
        next: (res: HttpResponse<IDifferenceView>) => {
          this.differenceView = res.body;
          this.isLoading = false;
        },
        error: () => (this.isLoading = false),
      });
  }

  compareWithParameters(firstProductId: number | undefined | null, secondProductId: number | undefined | null): void {
    this.isLoading = true;

    if (firstProductId && secondProductId) {
      this.productService
        .compareProducts({
          firstProductId,
          secondProductId,
        })
        .subscribe({
          next: (res: HttpResponse<IDifferenceView>) => {
            this.differenceView = res.body;

            this.loadRelationshipsOptionsAndInitProductComparisonForm(firstProductId, secondProductId);
          },
          error: () => (this.isLoading = false),
        });
    } else {
      this.noDifferenceViewPossible = true;
      this.isLoading = false;
    }
  }

  trackProductById(index: number, item: IProduct): number {
    return item.id!;
  }

  getSingleProduct(option: IProduct, selectedVal?: IProduct): IProduct {
    if (selectedVal) {
      if (option.id === selectedVal.id) {
        return selectedVal;
      }
    }
    return option;
  }

  isNew(newLibraryList: ILibrary[] | undefined | null, library: ILibrary): boolean {
    return !!newLibraryList?.some(e => e.id === library.id);
  }

  protected loadRelationshipsOptions(): void {
    this.productService.count().subscribe((count: HttpResponse<number>) => {
      this.productService
        .query({
          page: 0,
          size: count.body,
          sort: ['identifier' + ',' + ASC],
        })
        .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
        .subscribe((products: IProduct[]) => {
          this.productsSharedCollection = products;
        });
    });
  }

  protected loadRelationshipsOptionsAndInitProductComparisonForm(
    firstProductId: number | undefined | null,
    secondProductId: number | undefined | null
  ): void {
    this.productService.count().subscribe((count: HttpResponse<number>) => {
      this.productService
        .query({
          page: 0,
          size: count.body,
          sort: ['identifier' + ',' + ASC],
        })
        .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
        .subscribe((products: IProduct[]) => {
          this.productsSharedCollection = products;

          const firstProduct = this.productsSharedCollection.find(e => e.id === firstProductId);
          if (firstProduct) {
            this.productComparisonForm.get('firstProduct')?.setValue(firstProduct);
          }

          const secondProduct = this.productsSharedCollection.find(e => e.id === secondProductId);
          if (secondProduct) {
            this.productComparisonForm.get('secondProduct')?.setValue(secondProduct);
          }
          this.isLoading = false;
        });
    });
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<{}>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    // Api for inheritance.
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }
}
