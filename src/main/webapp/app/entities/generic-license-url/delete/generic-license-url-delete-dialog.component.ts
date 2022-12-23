import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IGenericLicenseUrl } from '../generic-license-url.model';
import { GenericLicenseUrlService } from '../service/generic-license-url.service';

@Component({
  templateUrl: './generic-license-url-delete-dialog.component.html',
})
export class GenericLicenseUrlDeleteDialogComponent {
  genericLicenseUrl?: IGenericLicenseUrl;

  constructor(protected genericLicenseUrlService: GenericLicenseUrlService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.genericLicenseUrlService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
