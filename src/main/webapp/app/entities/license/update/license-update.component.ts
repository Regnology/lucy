import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ILicense, License } from '../license.model';
import { LicenseService } from '../service/license.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ILicenseRisk } from 'app/entities/license-risk/license-risk.model';
import { LicenseRiskService } from 'app/entities/license-risk/service/license-risk.service';
import { IRequirement } from 'app/entities/requirement/requirement.model';
import { RequirementService } from 'app/entities/requirement/service/requirement.service';

@Component({
  selector: 'jhi-license-update',
  templateUrl: './license-update.component.html',
})
export class LicenseUpdateComponent implements OnInit {
  isSaving = false;

  usersSharedCollection: IUser[] = [];
  licenseRisksSharedCollection: ILicenseRisk[] = [];
  requirementsSharedCollection: IRequirement[] = [];

  editForm = this.fb.group({
    id: [],
    fullName: [null, [Validators.required]],
    shortIdentifier: [null, [Validators.required]],
    spdxIdentifier: [],
    url: [null, [Validators.maxLength(2048)]],
    genericLicenseText: [],
    other: [null, [Validators.maxLength(2048)]],
    reviewed: [],
    lastReviewedDate: [],
    lastReviewedBy: [],
    licenseRisk: [],
    requirements: [],
    licenseConflicts: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected licenseService: LicenseService,
    protected userService: UserService,
    protected licenseRiskService: LicenseRiskService,
    protected requirementService: RequirementService,
    protected activatedRoute: ActivatedRoute,
    protected fb: UntypedFormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ license }) => {
      this.updateForm(license);

      this.loadRelationshipsOptions();
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
    const license = this.createFromForm();
    if (license.id !== undefined) {
      this.subscribeToSaveResponse(this.licenseService.update(license));
    } else {
      this.subscribeToSaveResponse(this.licenseService.create(license));
    }
  }

  trackUserById(index: number, item: IUser): number {
    return item.id!;
  }

  trackLicenseRiskById(index: number, item: ILicenseRisk): number {
    return item.id!;
  }

  trackRequirementById(index: number, item: IRequirement): number {
    return item.id!;
  }

  getSelectedRequirement(option: IRequirement, selectedVals?: IRequirement[]): IRequirement {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILicense>>): void {
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

  protected updateForm(license: ILicense): void {
    this.editForm.patchValue({
      id: license.id,
      fullName: license.fullName,
      shortIdentifier: license.shortIdentifier,
      spdxIdentifier: license.spdxIdentifier,
      url: license.url,
      genericLicenseText: license.genericLicenseText,
      other: license.other,
      reviewed: license.reviewed,
      lastReviewedDate: license.lastReviewedDate,
      lastReviewedBy: license.lastReviewedBy,
      licenseRisk: license.licenseRisk,
      requirements: license.requirements,
      licenseConflicts: license.licenseConflicts,
    });

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, license.lastReviewedBy);
    this.licenseRisksSharedCollection = this.licenseRiskService.addLicenseRiskToCollectionIfMissing(
      this.licenseRisksSharedCollection,
      license.licenseRisk
    );
    this.requirementsSharedCollection = this.requirementService.addRequirementToCollectionIfMissing(
      this.requirementsSharedCollection,
      ...(license.requirements ?? [])
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('lastReviewedBy')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.licenseRiskService
      .query()
      .pipe(map((res: HttpResponse<ILicenseRisk[]>) => res.body ?? []))
      .pipe(
        map((licenseRisks: ILicenseRisk[]) =>
          this.licenseRiskService.addLicenseRiskToCollectionIfMissing(licenseRisks, this.editForm.get('licenseRisk')!.value)
        )
      )
      .subscribe((licenseRisks: ILicenseRisk[]) => (this.licenseRisksSharedCollection = licenseRisks));

    this.requirementService
      .query()
      .pipe(map((res: HttpResponse<IRequirement[]>) => res.body ?? []))
      .pipe(
        map((requirements: IRequirement[]) =>
          this.requirementService.addRequirementToCollectionIfMissing(requirements, ...(this.editForm.get('requirements')!.value ?? []))
        )
      )
      .subscribe((requirements: IRequirement[]) => (this.requirementsSharedCollection = requirements));
  }

  protected createFromForm(): ILicense {
    return {
      ...new License(),
      id: this.editForm.get(['id'])!.value,
      fullName: this.editForm.get(['fullName'])!.value,
      shortIdentifier: this.editForm.get(['shortIdentifier'])!.value,
      spdxIdentifier: this.editForm.get(['spdxIdentifier'])!.value,
      url: this.editForm.get(['url'])!.value,
      genericLicenseText: this.editForm.get(['genericLicenseText'])!.value,
      other: this.editForm.get(['other'])!.value,
      reviewed: this.editForm.get(['reviewed'])!.value,
      lastReviewedDate: this.editForm.get(['lastReviewedDate'])!.value,
      lastReviewedBy: this.editForm.get(['lastReviewedBy'])!.value,
      licenseRisk: this.editForm.get(['licenseRisk'])!.value,
      requirements: this.editForm.get(['requirements'])!.value,
      licenseConflicts: this.editForm.get(['licenseConflicts'])!.value,
    };
  }
}
