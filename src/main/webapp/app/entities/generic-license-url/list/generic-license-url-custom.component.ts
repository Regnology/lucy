import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IGenericLicenseUrl } from '../generic-license-url.model';
import { GenericLicenseUrlComponent } from 'app/entities/generic-license-url/list/generic-license-url.component';
import { GenericLicenseUrlCustomService } from 'app/entities/generic-license-url/service/generic-license-url-custom.service';
import { GenericLicenseUrlDeleteDialogCustomComponent } from 'app/entities/generic-license-url/delete/generic-license-url-delete-dialog-custom.component';

@Component({
  selector: 'jhi-generic-license-url-custom',
  templateUrl: './generic-license-url-custom.component.html',
})
export class GenericLicenseUrlCustomComponent extends GenericLicenseUrlComponent implements OnInit {
  constructor(protected genericLicenseUrlService: GenericLicenseUrlCustomService, protected modalService: NgbModal) {
    super(genericLicenseUrlService, modalService);
  }

  delete(genericLicenseUrl: IGenericLicenseUrl): void {
    const modalRef = this.modalService.open(GenericLicenseUrlDeleteDialogCustomComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.genericLicenseUrl = genericLicenseUrl;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
