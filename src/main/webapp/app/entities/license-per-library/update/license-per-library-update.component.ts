import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ILicensePerLibrary, LicensePerLibrary } from '../license-per-library.model';
import { LicensePerLibraryService } from '../service/license-per-library.service';
import { ILicense } from 'app/entities/license/license.model';
import { LicenseService } from 'app/entities/license/service/license.service';
import { ILibrary } from 'app/entities/library/library.model';
import { LibraryService } from 'app/entities/library/service/library.service';
import { LinkType } from 'app/entities/enumerations/link-type.model';

@Component({
  selector: 'jhi-license-per-library-update',
  templateUrl: './license-per-library-update.component.html',
})
export class LicensePerLibraryUpdateComponent implements OnInit {
  isSaving = false;
  linkTypeValues = Object.keys(LinkType);

  licensesSharedCollection: ILicense[] = [];
  librariesSharedCollection: ILibrary[] = [];

  editForm = this.fb.group({
    id: [],
    linkType: [],
    license: [],
    library: [],
  });

  constructor(
    protected licensePerLibraryService: LicensePerLibraryService,
    protected licenseService: LicenseService,
    protected libraryService: LibraryService,
    protected activatedRoute: ActivatedRoute,
    protected fb: UntypedFormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ licensePerLibrary }) => {
      this.updateForm(licensePerLibrary);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const licensePerLibrary = this.createFromForm();
    if (licensePerLibrary.id !== undefined) {
      this.subscribeToSaveResponse(this.licensePerLibraryService.update(licensePerLibrary));
    } else {
      this.subscribeToSaveResponse(this.licensePerLibraryService.create(licensePerLibrary));
    }
  }

  trackLicenseById(index: number, item: ILicense): number {
    return item.id!;
  }

  trackLibraryById(index: number, item: ILibrary): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILicensePerLibrary>>): void {
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

  protected updateForm(licensePerLibrary: ILicensePerLibrary): void {
    this.editForm.patchValue({
      id: licensePerLibrary.id,
      linkType: licensePerLibrary.linkType,
      license: licensePerLibrary.license,
      library: licensePerLibrary.library,
    });

    this.licensesSharedCollection = this.licenseService.addLicenseToCollectionIfMissing(
      this.licensesSharedCollection,
      licensePerLibrary.license
    );
    this.librariesSharedCollection = this.libraryService.addLibraryToCollectionIfMissing(
      this.librariesSharedCollection,
      licensePerLibrary.library
    );
  }

  protected loadRelationshipsOptions(): void {
    this.licenseService
      .query()
      .pipe(map((res: HttpResponse<ILicense[]>) => res.body ?? []))
      .pipe(
        map((licenses: ILicense[]) => this.licenseService.addLicenseToCollectionIfMissing(licenses, this.editForm.get('license')!.value))
      )
      .subscribe((licenses: ILicense[]) => (this.licensesSharedCollection = licenses));

    this.libraryService
      .query()
      .pipe(map((res: HttpResponse<ILibrary[]>) => res.body ?? []))
      .pipe(
        map((libraries: ILibrary[]) => this.libraryService.addLibraryToCollectionIfMissing(libraries, this.editForm.get('library')!.value))
      )
      .subscribe((libraries: ILibrary[]) => (this.librariesSharedCollection = libraries));
  }

  protected createFromForm(): ILicensePerLibrary {
    return {
      ...new LicensePerLibrary(),
      id: this.editForm.get(['id'])!.value,
      linkType: this.editForm.get(['linkType'])!.value,
      license: this.editForm.get(['license'])!.value,
      library: this.editForm.get(['library'])!.value,
    };
  }
}
