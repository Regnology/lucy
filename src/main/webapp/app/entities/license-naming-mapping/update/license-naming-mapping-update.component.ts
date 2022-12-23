import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ILicenseNamingMapping, LicenseNamingMapping } from '../license-naming-mapping.model';
import { LicenseNamingMappingService } from '../service/license-naming-mapping.service';

@Component({
  selector: 'jhi-license-naming-mapping-update',
  templateUrl: './license-naming-mapping-update.component.html',
})
export class LicenseNamingMappingUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    regex: [null, [Validators.required, Validators.maxLength(512)]],
    uniformShortIdentifier: [],
  });

  constructor(
    protected licenseNamingMappingService: LicenseNamingMappingService,
    protected activatedRoute: ActivatedRoute,
    protected fb: UntypedFormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ licenseNamingMapping }) => {
      this.updateForm(licenseNamingMapping);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const licenseNamingMapping = this.createFromForm();
    if (licenseNamingMapping.id !== undefined) {
      this.subscribeToSaveResponse(this.licenseNamingMappingService.update(licenseNamingMapping));
    } else {
      this.subscribeToSaveResponse(this.licenseNamingMappingService.create(licenseNamingMapping));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILicenseNamingMapping>>): void {
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

  protected updateForm(licenseNamingMapping: ILicenseNamingMapping): void {
    this.editForm.patchValue({
      id: licenseNamingMapping.id,
      regex: licenseNamingMapping.regex,
      uniformShortIdentifier: licenseNamingMapping.uniformShortIdentifier,
    });
  }

  protected createFromForm(): ILicenseNamingMapping {
    return {
      ...new LicenseNamingMapping(),
      id: this.editForm.get(['id'])!.value,
      regex: this.editForm.get(['regex'])!.value,
      uniformShortIdentifier: this.editForm.get(['uniformShortIdentifier'])!.value,
    };
  }
}
