import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILicenseNamingMapping } from '../license-naming-mapping.model';
import { LicenseNamingMappingDetailComponent } from 'app/entities/license-naming-mapping/detail/license-naming-mapping-detail.component';

@Component({
  selector: 'jhi-license-naming-mapping-detail-custom',
  templateUrl: './license-naming-mapping-detail-custom.component.html',
})
export class LicenseNamingMappingDetailCustomComponent extends LicenseNamingMappingDetailComponent {
  licenseNamingMapping: ILicenseNamingMapping | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {
    super(activatedRoute);
  }
}
