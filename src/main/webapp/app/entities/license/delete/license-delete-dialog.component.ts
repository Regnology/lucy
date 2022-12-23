import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ILicense } from '../license.model';
import { LicenseService } from '../service/license.service';

@Component({
  templateUrl: './license-delete-dialog.component.html',
})
export class LicenseDeleteDialogComponent {
  license?: ILicense;

  constructor(protected licenseService: LicenseService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.licenseService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
