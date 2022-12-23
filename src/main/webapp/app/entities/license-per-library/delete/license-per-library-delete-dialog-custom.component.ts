import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ILicensePerLibrary } from '../license-per-library.model';
import { LicensePerLibraryCustomService } from 'app/entities/license-per-library/service/license-per-library-custom.service';
import { LicensePerLibraryDeleteDialogComponent } from 'app/entities/license-per-library/delete/license-per-library-delete-dialog.component';

@Component({
  templateUrl: './license-per-library-delete-dialog-custom.component.html',
})
export class LicensePerLibraryDeleteDialogCustomComponent extends LicensePerLibraryDeleteDialogComponent {
  licensePerLibrary?: ILicensePerLibrary;

  constructor(protected licensePerLibraryService: LicensePerLibraryCustomService, public activeModal: NgbActiveModal) {
    super(licensePerLibraryService, activeModal);
  }
}
