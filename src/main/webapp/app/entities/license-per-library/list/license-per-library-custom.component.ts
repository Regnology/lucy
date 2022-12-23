import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ILicensePerLibrary } from '../license-per-library.model';
import { LicensePerLibraryComponent } from 'app/entities/license-per-library/list/license-per-library.component';
import { LicensePerLibraryCustomService } from 'app/entities/license-per-library/service/license-per-library-custom.service';
import { LicensePerLibraryDeleteDialogCustomComponent } from 'app/entities/license-per-library/delete/license-per-library-delete-dialog-custom.component';

@Component({
  selector: 'jhi-license-per-library-custom',
  templateUrl: './license-per-library-custom.component.html',
})
export class LicensePerLibraryCustomComponent extends LicensePerLibraryComponent {
  constructor(
    protected licensePerLibraryService: LicensePerLibraryCustomService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal
  ) {
    super(licensePerLibraryService, activatedRoute, router, modalService);
  }

  delete(licensePerLibrary: ILicensePerLibrary): void {
    const modalRef = this.modalService.open(LicensePerLibraryDeleteDialogCustomComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.licensePerLibrary = licensePerLibrary;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadPage();
      }
    });
  }
}
