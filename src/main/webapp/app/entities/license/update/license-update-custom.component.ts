import { Component } from '@angular/core';
import { FormArray, UntypedFormArray, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { EventManager } from 'app/core/util/event-manager.service';
import { map } from 'rxjs/operators';
import { DataUtils } from 'app/core/util/data-util.service';
import { LicenseUpdateComponent } from 'app/entities/license/update/license-update.component';
import { LicenseCustomService } from 'app/entities/license/service/license-custom.service';
import { UserCustomService } from 'app/entities/user/user-custom.service';
import { LicenseRiskCustomService } from 'app/entities/license-risk/service/license-risk-custom.service';
import { RequirementCustomService } from 'app/entities/requirement/service/requirement-custom.service';
import { IUser } from 'app/entities/user/user.model';
import { ILicenseRisk } from '../../license-risk/license-risk.model';
import { ILicense, License } from '../license.model';
import { ILicenseConflict } from '../../license-conflict/license-conflict.model';
import { IRequirement } from 'app/entities/requirement/requirement.model';
import { CompatibilityState } from '../../enumerations/compatibility-state.model';
import { HttpResponse } from '@angular/common/http';
import { AlertService } from 'app/core/util/alert.service';

@Component({
  selector: 'jhi-license-update-custom',
  templateUrl: './license-update-custom.component.html',
})
export class LicenseUpdateCustomComponent extends LicenseUpdateComponent {
  compatiblityStates = Object.keys(CompatibilityState);
  selectedLicenseRisk?: ILicenseRisk | null;
  isLoadingLicenseConflicts = false;

  editForm = this.fb.group({
    id: [],
    fullName: [null, [Validators.required]],
    shortIdentifier: [null, [Validators.required]],
    spdxIdentifier: [],
    url: [null, [Validators.maxLength(2048)]],
    genericLicenseText: [],
    other: [null, [Validators.maxLength(8192)]],
    reviewed: [],
    lastReviewedDate: [],
    lastReviewedBy: [],
    licenseRisk: [],
    requirements: [],
    licenseConflicts: this.fb.array([]),
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected licenseService: LicenseCustomService,
    protected userService: UserCustomService,
    protected licenseRiskService: LicenseRiskCustomService,
    protected requirementService: RequirementCustomService,
    protected activatedRoute: ActivatedRoute,
    protected fb: UntypedFormBuilder,
    protected alertService: AlertService
  ) {
    super(dataUtils, eventManager, licenseService, userService, licenseRiskService, requirementService, activatedRoute, fb);
  }

  onLicenseRiskChange(risk: ILicenseRisk): void {
    this.selectedLicenseRisk = risk;
  }

  trackLicenseById(index: number, item: ILicense): number {
    return item.id!;
  }

  getLicenseConflicts(): UntypedFormArray | null {
    return this.editForm.get('licenseConflicts') as UntypedFormArray;
  }

  getSingleLicense(option: ILicense, selectedVal?: ILicense): ILicense {
    if (selectedVal) {
      if (option.id === selectedVal.id) {
        return selectedVal;
      }
    }
    return option;
  }

  getFirstLicense(i: number): ILicense {
    return this.getLicenseConflicts()?.at(i).get('firstLicenseConflict')?.value as ILicense;
  }

  getSecondLicense(i: number): ILicense | null {
    return this.getLicenseConflicts()?.at(i).get('secondLicenseConflict')?.value as ILicense;
  }

  navTabChange(event: any): void {
    this.isLoadingLicenseConflicts = true;

    const licenseConflictsFormArray = this.editForm.get('licenseConflicts')!.value as UntypedFormArray;

    if (event.nextId === 'License Conflicts' && licenseConflictsFormArray.length === 0) {
      this.licenseService.fetchLicenseConflictsWithRisk(this.editForm.get('id')!.value).subscribe({
        next: (res: HttpResponse<ILicenseConflict[]>) => {
          this.updateLicenseConflictsForm(res.body);
          this.isLoadingLicenseConflicts = false;
        },
        error: () => (this.isLoadingLicenseConflicts = false),
      });
    }
  }

  selectAllAsCompatible(licenseRisk: string | undefined): void {
    const licenseConflicts = this.editForm.get('licenseConflicts')! as FormArray;

    for (const value of licenseConflicts.controls) {
      if (value.get('secondLicenseConflict')!.value.licenseRisk?.name === licenseRisk) {
        value.get('compatibility')!.patchValue(CompatibilityState.Compatible);
      }
    }

    this.alertService.addAlert({
      type: 'success',
      message: `All '${licenseRisk!}' licenses has been set to Compatible`,
    });
  }

  selectAllAsIncompatible(licenseRisk: string | undefined): void {
    const licenseConflicts = this.editForm.get('licenseConflicts')! as FormArray;

    for (const value of licenseConflicts.controls) {
      if (value.get('secondLicenseConflict')!.value.licenseRisk?.name === licenseRisk) {
        value.get('compatibility')!.patchValue(CompatibilityState.Incompatible);
      }
    }

    this.alertService.addAlert({
      type: 'success',
      message: `All '${licenseRisk!}' licenses has been set to Incompatible`,
    });
  }

  selectAllAsUnknown(licenseRisk: string | undefined): void {
    const licenseConflicts = this.editForm.get('licenseConflicts')! as FormArray;

    for (const value of licenseConflicts.controls) {
      if (value.get('secondLicenseConflict')!.value.licenseRisk?.name === licenseRisk) {
        value.get('compatibility')!.patchValue(CompatibilityState.Unknown);
      }
    }

    this.alertService.addAlert({
      type: 'success',
      message: `All '${licenseRisk!}' licenses has been set to Unknown`,
    });
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

    // this.updateLicenseConflictsForm(license.licenseConflicts ?? null);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, license.lastReviewedBy);
    this.licenseRisksSharedCollection = this.licenseRiskService.addLicenseRiskToCollectionIfMissing(
      this.licenseRisksSharedCollection,
      license.licenseRisk
    );
    this.requirementsSharedCollection = this.requirementService.addRequirementToCollectionIfMissing(
      this.requirementsSharedCollection,
      ...(license.requirements ?? [])
    );

    this.selectedLicenseRisk = license.licenseRisk;
  }

  protected updateLicenseConflictsForm(licenseConflict: ILicenseConflict[] | null): void {
    const licenseConflicts = this.getLicenseConflicts();

    // TODO create new formGroup and add new Controls for every license risk

    if (licenseConflicts) {
      licenseConflicts.clear();
      if (licenseConflict) {
        licenseConflict.forEach((value: ILicenseConflict) => {
          licenseConflicts.push(
            this.newLicenseConflictParam(
              value.id ?? null,
              new License(
                this.editForm.get('id')!.value,
                this.editForm.get('fullName')!.value,
                this.editForm.get('shortIdentifier')!.value
              ),
              value.compatibility ?? null,
              value.secondLicenseConflict ?? null,
              value.comment ?? null
            )
          );
        });
      }
    }
  }

  protected newLicenseConflictParam(
    id: number | null,
    firstLicenseConflict: ILicense | null,
    compatibility: CompatibilityState | null,
    secondLicenseConflict: ILicense | null,
    comment: string | null
  ): UntypedFormGroup {
    return this.fb.group({
      id,
      firstLicenseConflict,
      compatibility,
      secondLicenseConflict,
      comment,
    });
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('lastReviewedBy')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.licenseRiskService
      .query({
        sort: ['level', 'asc'],
        page: 0,
        size: 10,
      })
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
}
