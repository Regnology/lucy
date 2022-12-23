import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ILicenseNamingMapping } from '../license-naming-mapping.model';
import { LicenseNamingMappingDeleteDialogComponent } from 'app/entities/license-naming-mapping/delete/license-naming-mapping-delete-dialog.component';
import { LicenseNamingMappingCustomService } from 'app/entities/license-naming-mapping/service/license-naming-mapping-custom.service';

@Component({
  templateUrl: './license-naming-mapping-delete-dialog-custom.component.html',
})
export class LicenseNamingMappingDeleteDialogCustomComponent extends LicenseNamingMappingDeleteDialogComponent {
  licenseNamingMapping?: ILicenseNamingMapping;

  constructor(protected licenseNamingMappingService: LicenseNamingMappingCustomService, public activeModal: NgbActiveModal) {
    super(licenseNamingMappingService, activeModal);
  }
}
