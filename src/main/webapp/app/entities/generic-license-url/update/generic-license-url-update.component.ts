import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IGenericLicenseUrl, GenericLicenseUrl } from '../generic-license-url.model';
import { GenericLicenseUrlService } from '../service/generic-license-url.service';

@Component({
  selector: 'jhi-generic-license-url-update',
  templateUrl: './generic-license-url-update.component.html',
})
export class GenericLicenseUrlUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    url: [null, [Validators.required, Validators.maxLength(2048)]],
  });

  constructor(
    protected genericLicenseUrlService: GenericLicenseUrlService,
    protected activatedRoute: ActivatedRoute,
    protected fb: UntypedFormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ genericLicenseUrl }) => {
      this.updateForm(genericLicenseUrl);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const genericLicenseUrl = this.createFromForm();
    if (genericLicenseUrl.id !== undefined) {
      this.subscribeToSaveResponse(this.genericLicenseUrlService.update(genericLicenseUrl));
    } else {
      this.subscribeToSaveResponse(this.genericLicenseUrlService.create(genericLicenseUrl));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGenericLicenseUrl>>): void {
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

  protected updateForm(genericLicenseUrl: IGenericLicenseUrl): void {
    this.editForm.patchValue({
      id: genericLicenseUrl.id,
      url: genericLicenseUrl.url,
    });
  }

  protected createFromForm(): IGenericLicenseUrl {
    return {
      ...new GenericLicenseUrl(),
      id: this.editForm.get(['id'])!.value,
      url: this.editForm.get(['url'])!.value,
    };
  }
}
