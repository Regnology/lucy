import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { LicenseRiskDeleteDialogComponent } from 'app/entities/license-risk/delete/license-risk-delete-dialog.component';
import { LicenseRiskCustomService } from 'app/entities/license-risk/service/license-risk-custom.service';

@Component({
  templateUrl: './license-risk-delete-dialog-custom.component.html',
})
export class LicenseRiskDeleteDialogCustomComponent extends LicenseRiskDeleteDialogComponent {
  constructor(protected licenseRiskService: LicenseRiskCustomService, public activeModal: NgbActiveModal) {
    super(licenseRiskService, activeModal);
  }
}
