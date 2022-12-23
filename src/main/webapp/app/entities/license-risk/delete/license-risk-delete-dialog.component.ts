import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ILicenseRisk } from '../license-risk.model';
import { LicenseRiskService } from '../service/license-risk.service';

@Component({
  templateUrl: './license-risk-delete-dialog.component.html',
})
export class LicenseRiskDeleteDialogComponent {
  licenseRisk?: ILicenseRisk;

  constructor(protected licenseRiskService: LicenseRiskService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.licenseRiskService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
