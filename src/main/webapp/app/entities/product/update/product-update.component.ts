import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IProduct, Product } from '../product.model';
import { ProductService } from '../service/product.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { UploadState } from 'app/entities/enumerations/upload-state.model';

@Component({
  selector: 'jhi-product-update',
  templateUrl: './product-update.component.html',
})
export class ProductUpdateComponent implements OnInit {
  isSaving = false;
  uploadStateValues = Object.keys(UploadState);

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    identifier: [null, [Validators.required]],
    version: [null, [Validators.required]],
    createdDate: [],
    lastUpdatedDate: [],
    targetUrl: [null, [Validators.maxLength(2048)]],
    uploadState: [],
    disclaimer: [],
    delivered: [],
    deliveredDate: [],
    contact: [null, [Validators.maxLength(2048)]],
    comment: [null, [Validators.maxLength(4096)]],
    previousProductId: [],
    uploadFilter: [null, [Validators.maxLength(2048)]],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected productService: ProductService,
    protected activatedRoute: ActivatedRoute,
    protected fb: UntypedFormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ product }) => {
      if (product.id === undefined) {
        const today = dayjs().startOf('day');
        product.deliveredDate = today;
      }

      this.updateForm(product);
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('lucyApp.error', { message: err.message })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const product = this.createFromForm();
    if (product.id !== undefined) {
      this.subscribeToSaveResponse(this.productService.update(product));
    } else {
      this.subscribeToSaveResponse(this.productService.create(product));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProduct>>): void {
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

  protected updateForm(product: IProduct): void {
    this.editForm.patchValue({
      id: product.id,
      name: product.name,
      identifier: product.identifier,
      version: product.version,
      createdDate: product.createdDate,
      lastUpdatedDate: product.lastUpdatedDate,
      targetUrl: product.targetUrl,
      uploadState: product.uploadState,
      disclaimer: product.disclaimer,
      delivered: product.delivered,
      deliveredDate: product.deliveredDate ? product.deliveredDate.format(DATE_TIME_FORMAT) : null,
      contact: product.contact,
      comment: product.comment,
      previousProductId: product.previousProductId,
      uploadFilter: product.uploadFilter,
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
      previousProductId: this.editForm.get(['previousProductId'])!.value,
      uploadFilter: this.editForm.get(['uploadFilter'])!.value,
    };
  }
}
