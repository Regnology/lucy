import { Component } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ILicenseRisk } from '../license-risk.model';
import { ParseLinks } from 'app/core/util/parse-links.service';
import { LicenseRiskComponent } from 'app/entities/license-risk/list/license-risk.component';
import { LicenseRiskDeleteDialogCustomComponent } from 'app/entities/license-risk/delete/license-risk-delete-dialog-custom.component';
import { LicenseRiskCustomService } from 'app/entities/license-risk/service/license-risk-custom.service';

@Component({
  selector: 'jhi-license-risk-custom',
  templateUrl: './license-risk-custom.component.html',
})
export class LicenseRiskCustomComponent extends LicenseRiskComponent {
  constructor(protected licenseRiskService: LicenseRiskCustomService, protected modalService: NgbModal, protected parseLinks: ParseLinks) {
    super(licenseRiskService, modalService, parseLinks);
  }

  delete(licenseRisk: ILicenseRisk): void {
    const modalRef = this.modalService.open(LicenseRiskDeleteDialogCustomComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.licenseRisk = licenseRisk;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.reset();
      }
    });
  }
}
