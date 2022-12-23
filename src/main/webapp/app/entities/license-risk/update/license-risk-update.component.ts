import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ILicenseRisk, LicenseRisk } from '../license-risk.model';
import { LicenseRiskService } from '../service/license-risk.service';

@Component({
  selector: 'jhi-license-risk-update',
  templateUrl: './license-risk-update.component.html',
})
export class LicenseRiskUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    level: [null, [Validators.required]],
    description: [null, [Validators.maxLength(1024)]],
    color: [],
  });

  constructor(
    protected licenseRiskService: LicenseRiskService,
    protected activatedRoute: ActivatedRoute,
    protected fb: UntypedFormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ licenseRisk }) => {
      this.updateForm(licenseRisk);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const licenseRisk = this.createFromForm();
    if (licenseRisk.id !== undefined) {
      this.subscribeToSaveResponse(this.licenseRiskService.update(licenseRisk));
    } else {
      this.subscribeToSaveResponse(this.licenseRiskService.create(licenseRisk));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILicenseRisk>>): void {
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

  protected updateForm(licenseRisk: ILicenseRisk): void {
    this.editForm.patchValue({
      id: licenseRisk.id,
      name: licenseRisk.name,
      level: licenseRisk.level,
      description: licenseRisk.description,
      color: licenseRisk.color,
    });
  }

  protected createFromForm(): ILicenseRisk {
    return {
      ...new LicenseRisk(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      level: this.editForm.get(['level'])!.value,
      description: this.editForm.get(['description'])!.value,
      color: this.editForm.get(['color'])!.value,
    };
  }
}
