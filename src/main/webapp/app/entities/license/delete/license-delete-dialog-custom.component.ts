import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { LicenseDeleteDialogComponent } from 'app/entities/license/delete/license-delete-dialog.component';
import { LicenseCustomService } from 'app/entities/license/service/license-custom.service';

@Component({
  templateUrl: './license-delete-dialog-custom.component.html',
})
export class LicenseDeleteDialogCustomComponent extends LicenseDeleteDialogComponent {
  constructor(protected licenseService: LicenseCustomService, public activeModal: NgbActiveModal) {
    super(licenseService, activeModal);
  }
}
