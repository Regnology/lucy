import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ILicenseNamingMapping } from '../license-naming-mapping.model';
import { LicenseNamingMappingService } from '../service/license-naming-mapping.service';

@Component({
  templateUrl: './license-naming-mapping-delete-dialog.component.html',
})
export class LicenseNamingMappingDeleteDialogComponent {
  licenseNamingMapping?: ILicenseNamingMapping;

  constructor(protected licenseNamingMappingService: LicenseNamingMappingService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.licenseNamingMappingService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
