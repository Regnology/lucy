import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ILicensePerLibrary } from '../license-per-library.model';
import { LicensePerLibraryService } from '../service/license-per-library.service';

@Component({
  templateUrl: './license-per-library-delete-dialog.component.html',
})
export class LicensePerLibraryDeleteDialogComponent {
  licensePerLibrary?: ILicensePerLibrary;

  constructor(protected licensePerLibraryService: LicensePerLibraryService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.licensePerLibraryService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
