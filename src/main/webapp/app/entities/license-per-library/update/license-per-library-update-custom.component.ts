import { Component } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { LibraryCustomService } from 'app/entities/library/service/library-custom.service';
import { LicenseCustomService } from 'app/entities/license/service/license-custom.service';
import { LicensePerLibraryCustomService } from 'app/entities/license-per-library/service/license-per-library-custom.service';
import { LicensePerLibraryUpdateComponent } from 'app/entities/license-per-library/update/license-per-library-update.component';

@Component({
  selector: 'jhi-license-per-library-update-custom',
  templateUrl: './license-per-library-update-custom.component.html',
})
export class LicensePerLibraryUpdateCustomComponent extends LicensePerLibraryUpdateComponent {
  constructor(
    protected licensePerLibraryService: LicensePerLibraryCustomService,
    protected licenseService: LicenseCustomService,
    protected libraryService: LibraryCustomService,
    protected activatedRoute: ActivatedRoute,
    protected fb: UntypedFormBuilder
  ) {
    super(licensePerLibraryService, licenseService, libraryService, activatedRoute, fb);
  }
}
