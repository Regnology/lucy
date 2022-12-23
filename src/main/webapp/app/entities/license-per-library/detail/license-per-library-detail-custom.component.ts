import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILicensePerLibrary } from '../license-per-library.model';
import { LicensePerLibraryDetailComponent } from 'app/entities/license-per-library/detail/license-per-library-detail.component';

@Component({
  selector: 'jhi-license-per-library-detail-custom',
  templateUrl: './license-per-library-detail-custom.component.html',
})
export class LicensePerLibraryDetailCustomComponent extends LicensePerLibraryDetailComponent {
  licensePerLibrary: ILicensePerLibrary | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {
    super(activatedRoute);
  }
}
