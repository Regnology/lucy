import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { GenericLicenseUrlDeleteDialogComponent } from 'app/entities/generic-license-url/delete/generic-license-url-delete-dialog.component';
import { GenericLicenseUrlCustomService } from 'app/entities/generic-license-url/service/generic-license-url-custom.service';

@Component({
  templateUrl: './generic-license-url-delete-dialog-custom.component.html',
})
export class GenericLicenseUrlDeleteDialogCustomComponent extends GenericLicenseUrlDeleteDialogComponent {
  constructor(protected genericLicenseUrlService: GenericLicenseUrlCustomService, public activeModal: NgbActiveModal) {
    super(genericLicenseUrlService, activeModal);
  }
}
