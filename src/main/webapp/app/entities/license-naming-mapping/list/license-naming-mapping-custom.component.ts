import { Component } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { LicenseNamingMappingCustomService } from 'app/entities/license-naming-mapping/service/license-naming-mapping-custom.service';
import { LicenseNamingMappingComponent } from 'app/entities/license-naming-mapping/list/license-naming-mapping.component';
import { ILicenseNamingMapping } from 'app/entities/license-naming-mapping/license-naming-mapping.model';
import { LicenseNamingMappingDeleteDialogCustomComponent } from 'app/entities/license-naming-mapping/delete/license-naming-mapping-delete-dialog-custom.component';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'jhi-license-naming-mapping-custom',
  templateUrl: './license-naming-mapping-custom.component.html',
})
export class LicenseNamingMappingCustomComponent extends LicenseNamingMappingComponent {
  constructor(
    protected licenseNamingMappingService: LicenseNamingMappingCustomService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected modalService: NgbModal
  ) {
    super(licenseNamingMappingService, activatedRoute, router, modalService);
  }

  delete(licenseNamingMapping: ILicenseNamingMapping): void {
    const modalRef = this.modalService.open(LicenseNamingMappingDeleteDialogCustomComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.licenseNamingMapping = licenseNamingMapping;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadPage();
      }
    });
  }
}
