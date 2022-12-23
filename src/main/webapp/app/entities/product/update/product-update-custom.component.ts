import { Component } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { IProduct, Product } from '../product.model';
import { EventManager } from 'app/core/util/event-manager.service';
import { DataUtils } from 'app/core/util/data-util.service';
import { ProductUpdateComponent } from 'app/entities/product/update/product-update.component';
import { ProductCustomService } from 'app/entities/product/service/product-custom.service';
import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from '../../../config/input.constants';

@Component({
  selector: 'jhi-product-update-custom',
  templateUrl: './product-update-custom.component.html',
})
export class ProductUpdateCustomComponent extends ProductUpdateComponent {
  productsSharedCollection: IProduct[] = [];

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected productService: ProductCustomService,
    protected activatedRoute: ActivatedRoute,
    protected fb: UntypedFormBuilder
  ) {
    super(dataUtils, eventManager, productService, activatedRoute, fb);
  }

  trackProductById(index: number, item: IProduct): number {
    return item.id!;
  }

  protected updateForm(product: IProduct): void {
    this.productService
      .count({
        'identifier.equals': product.identifier,
        'version.notEquals': product.version,
      })
      .subscribe((count: HttpResponse<number>) => {
        this.productService
          .query({
            'identifier.equals': product.identifier,
            'version.notEquals': product.version,
            page: 0,
            size: count.body,
          })
          .subscribe((res: HttpResponse<IProduct[]>) => {
            this.productsSharedCollection = res.body ?? [];
            this.editForm.patchValue({
              id: product.id,
              name: product.name,
              identifier: product.identifier,
              version: product.version,
              createdDate: product.createdDate,
              lastUpdatedDate: product.lastUpdatedDate,
              targetUrl: product.targetUrl,
              uploadState: product.uploadState,
              delivered: product.delivered,
              deliveredDate: product.deliveredDate,
              contact: product.contact,
              comment: product.comment,
              disclaimer: product.disclaimer,
              previousProductId: this.productsSharedCollection.find((ele: IProduct) => ele.id === product.previousProductId) ?? null,
              uploadFilter: product.uploadFilter,
            });
          });
      });
  }

  protected createFromForm(): IProduct {
    return {
      ...new Product(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      identifier: this.editForm.get(['identifier'])!.value,
      version: this.editForm.get(['version'])!.value,
      createdDate: this.editForm.get(['createdDate'])!.value,
      lastUpdatedDate: this.editForm.get(['lastUpdatedDate'])!.value,
      targetUrl: this.editForm.get(['targetUrl'])!.value,
      uploadState: this.editForm.get(['uploadState'])!.value,
      disclaimer: this.editForm.get(['disclaimer'])!.value,
      delivered: this.editForm.get(['delivered'])!.value,
      deliveredDate: this.editForm.get(['deliveredDate'])!.value
        ? dayjs(this.editForm.get(['deliveredDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      contact: this.editForm.get(['contact'])!.value,
      comment: this.editForm.get(['comment'])!.value,
      previousProductId: (this.editForm.get(['previousProductId'])?.value as IProduct | undefined)?.id,
      uploadFilter: this.editForm.get(['uploadFilter'])!.value,
    };
  }
}
